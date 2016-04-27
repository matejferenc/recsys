package recsys.evaluator.abstr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.RunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.eval.StatsCallable;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.evaluator.RecommenderCrossEvaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Abstract superclass of a couple implementations, providing shared functionality.
 */
public abstract class AbstractDifferenceRecommenderCrossEvaluator implements RecommenderCrossEvaluator {

	private static final Logger log = LoggerFactory.getLogger(AbstractDifferenceRecommenderCrossEvaluator.class);

	private final Random random;

	private AtomicInteger noEstimateCounter = new AtomicInteger();
	private AtomicInteger estimateCounter = new AtomicInteger();
	

	private DataModel dataModel;

	protected AbstractDifferenceRecommenderCrossEvaluator() {
		random = RandomUtils.getRandom();
	}

	@Override
	public List<Double> evaluate(RecommenderBuilder recommenderBuilder, DataModel dataModel, double trainingPercentage) throws TasteException {
		this.dataModel = dataModel;
		Preconditions.checkNotNull(recommenderBuilder);
		Preconditions.checkNotNull(dataModel);
		Preconditions.checkArgument(trainingPercentage >= 0.0 && trainingPercentage <= 1.0, "Invalid trainingPercentage: " + trainingPercentage + ". Must be: 0.0 <= trainingPercentage <= 1.0");

		log.info("Beginning evaluation using {} of {}", trainingPercentage, dataModel);

		List<Double> results = new ArrayList<Double>();

		int numUsers = dataModel.getNumUsers();
		// FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>(1 + (int) (evaluationPercentage * numUsers));
		// FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>(1 + (int) (evaluationPercentage * numUsers));

		int numParts = 9;// we divide training set 9 times
		int usersInOnePart = numUsers / numParts;
		int rest = numUsers - usersInOnePart * numParts;

		List<List<Long>> userPartitions = partitionUsers(usersInOnePart, rest);

		for (int i = 0; i < numParts; i++) {
			FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>();
			FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>();
			createDataModel(userPartitions, i, trainingPrefs, testPrefs, trainingPercentage);

			DataModel trainingModel = new GenericDataModel(trainingPrefs);
			Recommender recommender = recommenderBuilder.buildRecommender(trainingModel);
			double result = getEvaluation(testPrefs, recommender);
			log.info("Evaluation result: {}", result);
			results.add(result);
		}

		logResults(results);

		return results;
	}

	private void logResults(List<Double> results) {
		String resString = "";
		for (Double result : results) {
			resString += result + ", ";
		}
		log.info("Evaluation results: {}", resString);
	}

	private void createDataModel(List<List<Long>> userPartitions, int i, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs, double trainingPercentage) throws TasteException {
		for (List<Long> list : userPartitions) {
			if (userPartitions.get(i) == list) {
				for (Long userID : list) {
					splitOneUsersPrefs(trainingPercentage, trainingPrefs, testPrefs, userID);
				}
			} else {
				for (Long userID : list) {
					trainingPrefs.put(userID, dataModel.getPreferencesFromUser(userID));
				}
			}
		}
	}

	private List<List<Long>> partitionUsers(int usersInOnePart, int rest) throws TasteException {
		List<List<Long>> userPartitions = new ArrayList<List<Long>>();
		userPartitions.add(new ArrayList<Long>());
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		int i = 0;
		int actualPart = 0;
		while (userIDs.hasNext()) {
			if (i >= usersInOnePart + (actualPart < rest ? 1 : 0)) {
				actualPart++;
				i = 0;
				userPartitions.add(new ArrayList<Long>());
			}
			i++;
			Long userID = userIDs.next();
			userPartitions.get(actualPart).add(userID);
		}
		return userPartitions;
	}

	private void splitOneUsersPrefs(double trainingPercentage, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs, long userID) throws TasteException {
		List<Preference> oneUserTrainingPrefs = null;
		List<Preference> oneUserTestPrefs = null;
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		int size = prefs.length();
		int trainSampleSize = (int) Math.floor(size * trainingPercentage);
		List<Integer> trainingSample = createTrainingSample(size, trainSampleSize);
		for (int i = 0; i < size; i++) {
			Preference newPref = new GenericPreference(userID, prefs.getItemID(i), prefs.getValue(i));
			if (trainingSample.contains(i)) {
				if (oneUserTrainingPrefs == null) {
					oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTrainingPrefs.add(newPref);
			} else {
				if (oneUserTestPrefs == null) {
					oneUserTestPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTestPrefs.add(newPref);
			}
		}
		if (oneUserTrainingPrefs != null) {
			trainingPrefs.put(userID, new GenericUserPreferenceArray(oneUserTrainingPrefs));
			if (oneUserTestPrefs != null) {
				testPrefs.put(userID, new GenericUserPreferenceArray(oneUserTestPrefs));
			}
		}
	}

	private List<Integer> createTrainingSample(int max, int trainSampleSize) {
		List<Integer> results = new ArrayList<>();
		for (int i = 0; i < trainSampleSize; i++) {
			int rand = random.nextInt(max);
			while(results.contains(rand)){
				rand = random.nextInt(max);
			}
			results.add(rand);
		}
		return results;
	}

	private float capEstimatedPreference(float estimate) {
		float maxPreference = dataModel.getMaxPreference();
		if (estimate > maxPreference) {
			return maxPreference;
		}
		float minPreference = dataModel.getMinPreference();
		if (estimate < minPreference) {
			return minPreference;
		}
		return estimate;
	}

	private double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException {
		reset();
		Collection<Callable<Void>> estimateCallables = Lists.newArrayList();
		for (Map.Entry<Long, PreferenceArray> entry : testPrefs.entrySet()) {
			estimateCallables.add(new PreferenceEstimateCallable(recommender, entry.getKey(), entry.getValue(), noEstimateCounter, estimateCounter));
		}
		log.info("Beginning evaluation of {} users", estimateCallables.size());
		RunningAverageAndStdDev timing = new FullRunningAverageAndStdDev();
		execute(estimateCallables, noEstimateCounter, estimateCounter, timing);
		return computeFinalEvaluation();
	}

	protected static void execute(Collection<Callable<Void>> callables, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter, RunningAverageAndStdDev timing) throws TasteException {

		Collection<Callable<Void>> wrappedCallables = wrapWithStatsCallables(callables, noEstimateCounter, estimateCounter, timing);
		int numProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
		log.info("Starting timing of {} tasks in {} threads", wrappedCallables.size(), numProcessors);
		try {
			List<Future<Void>> futures = executor.invokeAll(wrappedCallables);
			// Go look for exceptions here, really
			for (Future<Void> future : futures) {
				future.get();
			}

		} catch (InterruptedException ie) {
			throw new TasteException(ie);
		} catch (ExecutionException ee) {
			throw new TasteException(ee.getCause());
		}

		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new TasteException(e.getCause());
		}
	}

	private static Collection<Callable<Void>> wrapWithStatsCallables(Iterable<Callable<Void>> callables, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter, RunningAverageAndStdDev timing) {
		Collection<Callable<Void>> wrapped = Lists.newArrayList();
		int count = 0;
		for (Callable<Void> callable : callables) {
			boolean logStats = count++ % 100 == 0; // log every 1000 or so iterations
			wrapped.add(new StatsCallable(callable, logStats, timing, noEstimateCounter, estimateCounter));
		}
		return wrapped;
	}

	protected abstract void reset();

	protected abstract void processOneEstimate(float estimatedPreference, Preference realPref);

	protected abstract double computeFinalEvaluation();

	public final class PreferenceEstimateCallable implements Callable<Void> {

		private final Recommender recommender;
		private final long testUserID;
		private final PreferenceArray prefs;
		private final AtomicInteger noEstimateCounter;
		private AtomicInteger estimateCounter;

		public PreferenceEstimateCallable(Recommender recommender, long testUserID, PreferenceArray prefs, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter) {
			this.recommender = recommender;
			this.testUserID = testUserID;
			this.prefs = prefs;
			this.noEstimateCounter = noEstimateCounter;
			this.estimateCounter = estimateCounter;
		}

		@Override
		public Void call() throws TasteException {
			for (Preference realPref : prefs) {
				float estimatedPreference = Float.NaN;
				try {
					// estimatedPreference = recommender.estimatePreference(testUserID, realPref.getItemID());
					// only takes relevant neighbors into account:
					estimatedPreference = recommender.estimatePreference(testUserID, realPref.getItemID());
				} catch (NoSuchUserException nsue) {
					// It's possible that an item exists in the test data but not training data in which case
					// NSEE will be thrown. Just ignore it and move on.
					log.info("User exists in test data but not training data: {}", testUserID);
				} catch (NoSuchItemException nsie) {
					log.info("Item exists in test data but not training data: {}", realPref.getItemID());
				}
				if (Float.isNaN(estimatedPreference)) {
					noEstimateCounter.incrementAndGet();
				} else {
					int counter = estimateCounter.incrementAndGet();
					if (counter % 1000 == 0) {
						log.info("Successfully estimated: {} preferences", counter);
					}
					estimatedPreference = capEstimatedPreference(estimatedPreference);
					processOneEstimate(estimatedPreference, realPref);
				}
				if (estimateCounter.get() % 1000 == 0) {
					log.info("Recommended {} preferences", estimateCounter.get());
				}
			}
			return null;
		}

	}

	public AtomicInteger getNoEstimateCounter() {
		return noEstimateCounter;
	}

	public AtomicInteger getEstimateCounter() {
		return estimateCounter;
	}

}
