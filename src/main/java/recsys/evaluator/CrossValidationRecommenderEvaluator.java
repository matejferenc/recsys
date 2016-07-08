package recsys.evaluator;

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
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.common.IntPrimitiveIterator;
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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Abstract superclass of a couple implementations, providing shared functionality.
 */
public abstract class CrossValidationRecommenderEvaluator implements RecommenderEvaluator {

	private static final Logger log = LoggerFactory.getLogger(CrossValidationRecommenderEvaluator.class);

	private final Random random;
	private Double maxPreference;
	private Double minPreference;

	private AtomicInteger noEstimateCounter;

	private AtomicInteger estimateCounter;

	protected CrossValidationRecommenderEvaluator() {
		random = RandomUtils.getRandom();
		maxPreference = Double.NaN;
		minPreference = Double.NaN;
	}

	@Override
	public final Double getMaxPreference() {
		return maxPreference;
	}

	@Override
	public final void setMaxPreference(Double maxPreference) {
		this.maxPreference = maxPreference;
	}

	@Override
	public final Double getMinPreference() {
		return minPreference;
	}

	@Override
	public final void setMinPreference(Double minPreference) {
		this.minPreference = minPreference;
	}

	@Override
	public Double evaluate(RecommenderBuilder recommenderBuilder, DataModelBuilder dataModelBuilder, DataModel dataModel, Double trainingPercentage, Double evaluationPercentage) throws TasteException {
		Preconditions.checkNotNull(recommenderBuilder);
		Preconditions.checkNotNull(dataModel);
		Preconditions.checkArgument(trainingPercentage >= 0.0 && trainingPercentage <= 1.0, "Invalid trainingPercentage: " + trainingPercentage + ". Must be: 0.0 <= trainingPercentage <= 1.0");
		Preconditions.checkArgument(evaluationPercentage >= 0.0 && evaluationPercentage <= 1.0, "Invalid evaluationPercentage: " + evaluationPercentage + ". Must be: 0.0 <= evaluationPercentage <= 1.0");

		log.info("Beginning evaluation using {} of {}", trainingPercentage, dataModel);

		int numUsers = dataModel.getNumUsers();
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>(1 + (int) (evaluationPercentage * numUsers));
		FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>(1 + (int) (evaluationPercentage * numUsers));

		IntPrimitiveIterator it = dataModel.getUserIDs();
		while (it.hasNext()) {
			Integer userID = it.nextInt();
			if (random.nextDouble() < evaluationPercentage) {
				splitOneUsersPrefs(trainingPercentage, trainingPrefs, testPrefs, userID, dataModel);
			}
		}

		DataModel trainingModel = dataModelBuilder == null ? new GenericDataModel(trainingPrefs) : dataModelBuilder.buildDataModel(trainingPrefs);

		Recommender recommender = recommenderBuilder.buildRecommender(trainingModel);

		Double result = getEvaluation(testPrefs, recommender);
		log.info("Evaluation result: {}", result);
		return result;
	}

	private void splitOneUsersPrefs(Double trainingPercentage, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs, Integer userID, DataModel dataModel) throws TasteException {
		List<Preference> oneUserTrainingPrefs = null;
		List<Preference> oneUserTestPrefs = null;
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		int size = prefs.length();
		for (int i = 0; i < size; i++) {
			Preference newPref = new GenericPreference(userID, prefs.getItemID(i), prefs.getValue(i));
			if (random.nextDouble() < trainingPercentage) {
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

	private Double capEstimatedPreference(Double estimate) {
		if (estimate > maxPreference) {
			return maxPreference;
		}
		if (estimate < minPreference) {
			return minPreference;
		}
		return estimate;
	}

	private Double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException {
		reset();
		Collection<Callable<Void>> estimateCallables = Lists.newArrayList();
		noEstimateCounter = new AtomicInteger();
		estimateCounter = new AtomicInteger();
		for (Map.Entry<Integer, PreferenceArray> entry : testPrefs.entrySet()) {
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
			boolean logStats = count++ % 1000 == 0; // log every 1000 or so iterations
			wrapped.add(new StatsCallable(callable, logStats, timing, noEstimateCounter, estimateCounter));
		}
		return wrapped;
	}

	protected abstract void reset();

	protected abstract void processOneEstimate(Double estimatedPreference, Preference realPref);

	protected abstract Double computeFinalEvaluation();

	public final class PreferenceEstimateCallable implements Callable<Void> {

		private final Recommender recommender;
		private final Integer testUserID;
		private final PreferenceArray prefs;
		private final AtomicInteger noEstimateCounter;
		private AtomicInteger estimateCounter;

		public PreferenceEstimateCallable(Recommender recommender, Integer testUserID, PreferenceArray prefs, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter) {
			this.recommender = recommender;
			this.testUserID = testUserID;
			this.prefs = prefs;
			this.noEstimateCounter = noEstimateCounter;
			this.estimateCounter = estimateCounter;
		}

		@Override
		public Void call() throws TasteException {
			for (Preference realPref : prefs) {
				Double estimatedPreference = Double.NaN;
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
				if (Double.isNaN(estimatedPreference)) {
					noEstimateCounter.incrementAndGet();
				} else {
					estimateCounter.incrementAndGet();
					estimatedPreference = capEstimatedPreference(estimatedPreference);
					processOneEstimate(estimatedPreference, realPref);
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
