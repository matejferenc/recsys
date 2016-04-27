package recsys.evaluator.abstr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
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

import recsys.evaluator.RecommenderFairEvaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Superclass for FairEvaluator counting estimated and not-estimated cases.
 *
 */
public abstract class AbstractRecommenderFairEvaluator implements RecommenderFairEvaluator {
	
	protected static final Logger log = LoggerFactory.getLogger(AbstractRecommenderFairEvaluator.class);

	private final Random random;

	protected DataModel dataModel;
	
	protected AtomicInteger noEstimateCounter = new AtomicInteger();

	protected AtomicInteger estimateCounter = new AtomicInteger();

	protected AbstractRecommenderFairEvaluator() {
		random = RandomUtils.getRandom();
	}

	/**
	 * Builds a testing and training dataset, trains the recommender on training dataset and evaluates the recommender on testing dataset.
	 */
	@Override
	public double evaluate(RecommenderBuilder recommenderBuilder, DataModel dataModel, double trainingPercentage, double evaluationPercentage) throws TasteException {
		this.dataModel = dataModel;
		Preconditions.checkNotNull(recommenderBuilder);
		Preconditions.checkNotNull(dataModel);
		Preconditions.checkArgument(trainingPercentage >= 0.0 && trainingPercentage <= 1.0, "Invalid trainingPercentage: " + trainingPercentage + ". Must be: 0.0 <= trainingPercentage <= 1.0");

		log.info("Beginning evaluation using {} of {}", trainingPercentage, dataModel);

		int numUsers = dataModel.getNumUsers();
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>(1 + (int) (evaluationPercentage * numUsers));
		FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>(1 + (int) (evaluationPercentage * numUsers));

		List<Integer> evaluationSample = createSample(numUsers, (int) Math.floor(numUsers * evaluationPercentage));
		int i = 0;
		LongPrimitiveIterator it = dataModel.getUserIDs();
		while (it.hasNext()) {
			long userID = it.nextLong();
			if (evaluationSample.contains(i)) {
				splitOneUsersPrefs(trainingPercentage, trainingPrefs, testPrefs, userID);
			}
			i++;
		}

		DataModel trainingModel = new GenericDataModel(trainingPrefs);

		Recommender recommender = recommenderBuilder.buildRecommender(trainingModel);

		double result = getEvaluation(testPrefs, recommender);
		log.info("Evaluation result: {}", result);
		return result;
	}
	
	protected abstract double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException;

	/**
	 * Splits the preferences of user with id userId into training and testing data structures. TrainingPercentage of preferences
	 * will go to training dataset and 1-trainingPercentage of preferences will go to testing dataset.
	 * @param trainingPercentage
	 * @param trainingPrefs
	 * @param testPrefs
	 * @param userID
	 * @throws TasteException
	 */
	private void splitOneUsersPrefs(double trainingPercentage, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs, long userID) throws TasteException {
		List<Preference> oneUserTrainingPrefs = null;
		List<Preference> oneUserTestPrefs = null;
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		int size = prefs.length();
		int trainSampleSize = (int) Math.floor(size * trainingPercentage);
		List<Integer> trainingSample = createSample(size, trainSampleSize);
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

	/**
	 * Create sample set of numbers 0..max of size sampleSize.
	 * @param max
	 * @param sampleSize
	 * @return
	 */
	private List<Integer> createSample(int max, int sampleSize) {
		List<Integer> results = new ArrayList<>();
		for (int i = 0; i < sampleSize; i++) {
			int rand = random.nextInt(max);
			while (results.contains(rand)) {
				rand = random.nextInt(max);
			}
			results.add(rand);
		}
		return results;
	}
	
	protected float capEstimatedPreference(float estimate) {
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

	protected static void execute(Collection<Callable<Void>> callables, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter, RunningAverageAndStdDev timing) throws TasteException {

		Collection<Callable<Void>> wrappedCallables = wrapWithStatsCallables(callables, noEstimateCounter, estimateCounter, timing);
		int numProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
		log.info("Starting timing of {} tasks in {} threads", wrappedCallables.size(), numProcessors);
		try {
			List<Future<Void>> futures = executor.invokeAll(wrappedCallables);
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
			boolean logStats = count++ % 100 == 0; // log every 100 iterations
			wrapped.add(new StatsCallable(callable, logStats, timing, noEstimateCounter, estimateCounter));
		}
		return wrapped;
	}

	protected abstract void reset();

	protected abstract double computeFinalEvaluation();

	public AtomicInteger getNoEstimateCounter() {
		return noEstimateCounter;
	}

	public AtomicInteger getEstimateCounter() {
		return estimateCounter;
	}
}
