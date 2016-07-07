package recsys.evaluator.abstr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.apache.mahout.cf.taste.impl.common.RunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.eval.StatsCallable;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.evaluator.RecommenderFairEvaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class AbstractRecommenderFairEvaluator implements RecommenderFairEvaluator {
	
	protected static final Logger log = LoggerFactory.getLogger(AbstractRecommenderFairEvaluator.class);

	private Map<Integer, Set<Integer>> alreadySelectedItems;
	
	protected DataModel dataModel;
	
	protected AtomicInteger noEstimateCounter = new AtomicInteger();

	protected AtomicInteger estimateCounter = new AtomicInteger();
	
	protected Map<Integer, List<Double>> estimatesForItems;
	
	protected Map<Integer, List<Double>> estimatesForUsers;

	protected AbstractRecommenderFairEvaluator(DataModel dataModel) {
		this.dataModel = dataModel;
		alreadySelectedItems = new HashMap<Integer, Set<Integer>>();
		estimatesForItems = new HashMap<Integer, List<Double>>();
		estimatesForUsers = new HashMap<Integer, List<Double>>();
	}

	/**
	 * Trains the recommender on training dataset and evaluates the recommender on test dataset.
	 */
	@Override
	public double evaluate(RecommenderBuilder recommenderBuilder, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs) throws TasteException {
		Preconditions.checkNotNull(recommenderBuilder);
		log.info("Beginning evaluation using of {}", dataModel);
		DataModel trainingModel = new GenericDataModel(trainingPrefs);
		Recommender recommender = recommenderBuilder.buildRecommender(trainingModel);
		double result = getEvaluation(testPrefs, recommender);
		log.info("Evaluation result: {}", result);
		return result;
	}
	

	protected abstract double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException;

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
	
	public Map<Integer, Set<Integer>> getAlreadySelectedItems() {
		return alreadySelectedItems;
	}
	
	protected void processEasiestAndHardestItems(float estimatedPreference, Preference realPref) {
		float absoluteError = Math.abs(estimatedPreference - realPref.getValue());
		processItems(realPref, absoluteError);
		processUsers(realPref, absoluteError);
	}

	private void processUsers(Preference realPref, float absoluteError) {
		int userID = realPref.getUserID();
		List<Double> estimatesForUser= estimatesForUsers.get(userID);
		if (estimatesForUser != null) {
			estimatesForUser.add((double) absoluteError);
		} else {
			ArrayList<Double> list = new ArrayList<Double>();
			list.add((double) absoluteError);
			estimatesForUsers.put(userID, list);
		}
	}

	private void processItems(Preference realPref, float absoluteError) {
		int itemID = realPref.getItemID();
		List<Double> estimatesForItem = estimatesForItems.get(itemID);
		if (estimatesForItem != null) {
			estimatesForItem.add((double) absoluteError);
		} else {
			ArrayList<Double> list = new ArrayList<Double>();
			list.add((double) absoluteError);
			estimatesForItems.put(itemID, list);
		}
	}
	
}
