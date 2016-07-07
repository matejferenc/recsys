package recsys.evaluator.abstr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
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
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.evaluator.RecommenderFairEvaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class AbstractRecommenderFairEvaluator implements RecommenderFairEvaluator {
	
	protected static final Logger log = LoggerFactory.getLogger(AbstractRecommenderFairEvaluator.class);

	private final Random random;
	
	private static Map<Pair<Double, Integer>, List<List<Integer>>> usersSplitted = new HashMap<Pair<Double, Integer>, List<List<Integer>>>();

	private static Map<Pair<Pair<Integer, Integer>, Double>, Pair<GenericUserPreferenceArray, GenericUserPreferenceArray>> preferencesSplitted
		= new HashMap<Pair<Pair<Integer, Integer>, Double>, Pair<GenericUserPreferenceArray, GenericUserPreferenceArray>>();
	
	private Map<Integer, Set<Integer>> alreadySelectedItems;
	
	protected DataModel dataModel;
	
	protected AtomicInteger noEstimateCounter = new AtomicInteger();

	protected AtomicInteger estimateCounter = new AtomicInteger();
	
	protected Map<Integer, List<Double>> estimatesForItems;
	
	protected Map<Integer, List<Double>> estimatesForUsers;

	protected AbstractRecommenderFairEvaluator(DataModel dataModel) {
		this.dataModel = dataModel;
		random = RandomUtils.getRandom(0L);
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
	
	/**
	 * Builds a testing and training dataset.
	 */
	@Override
	public List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset(double testingPercentage, double evaluationPercentage) throws TasteException {
		Preconditions.checkArgument(testingPercentage >= 0.0 && testingPercentage <= 1.0, "Invalid testingPercentage: " + testingPercentage + ". Must be: 0.0 <= testingPercentage <= 1.0");
		alreadySelectedItems.clear();
		
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> parts = new ArrayList<Pair<FastByIDMap<PreferenceArray>,FastByIDMap<PreferenceArray>>>(); 
		
		List<List<Integer>> userGroups = splitUsers(testingPercentage);
		int userGroupsCount = (int) Math.floor(1 / (testingPercentage));
		assert (userGroupsCount == userGroups.size());
		
		int evaluationGroupsCount = (int) Math.floor(1 / (evaluationPercentage));
		for (int evaluationGroupId = 0; evaluationGroupId < evaluationGroupsCount; evaluationGroupId++) {
			for (List<Integer> userGroup: userGroups) {
				boolean lastGroup = evaluationGroupId == (evaluationGroupsCount - 1);
				Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testAndTrainDatasetForPart = createTestAndTrainDatasetForPart(evaluationPercentage, evaluationGroupId, userGroup, lastGroup);
				parts.add(testAndTrainDatasetForPart);
			}
		}
		alreadySelectedItems.clear();
		return parts;
	}

	public Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> createTestAndTrainDatasetForPart(double evaluationPercentage, int evaluationGroupId, List<Integer> userGroup, boolean lastGroup) throws TasteException {
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>();
		FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>();
		List<Integer> userIDs = getUserIDs();
		for (int userId: userIDs) {
			if (!userGroup.contains(userId)) {
				//adding preferences from training set
				PreferenceArray prefs = dataModel.getPreferencesFromUser(userId);
				trainingPrefs.put(userId, prefs);
			} else {
				//adding preferences from testing set - have to be split in train and test sets
				Pair<GenericUserPreferenceArray,GenericUserPreferenceArray> splitOneUsersPrefs = splitOneUsersPrefs(evaluationPercentage, userId, evaluationGroupId, lastGroup);
				if (splitOneUsersPrefs.getFirst() != null) {
					trainingPrefs.put(userId, splitOneUsersPrefs.getFirst());
					if (splitOneUsersPrefs.getSecond() != null) {
						testPrefs.put(userId, splitOneUsersPrefs.getSecond());
					}
				}
			}
		}
		Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testAndTrainDatasetForPart = new Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>(trainingPrefs, testPrefs);
		return testAndTrainDatasetForPart;
	}

	public List<List<Integer>> splitUsers(double testingPercentage) throws TasteException {
		List<Integer> userIDs = getUserIDs();
		int numUsers = dataModel.getNumUsers();
//		if (usersSplitted.containsKey(new Pair<Double, Integer>(testingPercentage, numUsers))) {
//			return usersSplitted.get(new Pair<Double, Integer>(testingPercentage, numUsers));
//		}
		int userGroupsCount = (int) Math.round(1 / testingPercentage);
		List<List<Integer>> userGroups = new ArrayList<List<Integer>>();
		Set<Integer> alreadySelectedUsers = new HashSet<Integer>();
		for (int i = 0; i < userGroupsCount; i++) {
			int usersInGroup;
			// if last group
			if (i == userGroupsCount - 1) {
				usersInGroup = numUsers - alreadySelectedUsers.size();
			} else {
				usersInGroup = (int) Math.ceil(numUsers * testingPercentage);
			}
			List<Integer> evaluationSample = createSample(userIDs, usersInGroup, alreadySelectedUsers);
			alreadySelectedUsers.addAll(evaluationSample);
			userGroups.add(evaluationSample);
		}
//		usersSplitted.put(new Pair<Double, Integer>(testingPercentage, numUsers), userGroups);
		return userGroups;
	}

	public List<Integer> getUserIDs() throws TasteException {
		IntPrimitiveIterator userIDsIterator = dataModel.getUserIDs();
		List<Integer> userIDs = new ArrayList<Integer>();
		while (userIDsIterator.hasNext()) {
			userIDs.add(userIDsIterator.next());
		}
		return userIDs;
	}
	
	protected abstract double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException;

	/**
	 * Splits the preferences of user with id userId into training and testing data structures. EvaluationPercentage of preferences
	 * will go to testing dataset and 1-evaluationPercentage of preferences will go to testing dataset.
	 * @param evaluationPercentage
	 * @param userID
	 * @param lastGroup flag if the evaluation group is the last one. That means we have to return all the remaining preferences
	 * @throws TasteException
	 */
	public Pair<GenericUserPreferenceArray, GenericUserPreferenceArray> splitOneUsersPrefs(double evaluationPercentage, int userID, int evaluationGroupId, boolean lastGroup) throws TasteException {
//		if (preferencesSplitted.containsKey(new Pair<Pair<Long, Integer>, Double>(new Pair<Long, Integer>(userID, evaluationGroupId), evaluationPercentage))) {
//			return preferencesSplitted.get(new Pair<Pair<Long, Integer>, Double>(new Pair<Long, Integer>(userID, evaluationGroupId), evaluationPercentage));
//		}
		GenericUserPreferenceArray trainingPrefs = null;
		GenericUserPreferenceArray testPrefs = null;
		List<Preference> oneUserTrainingPrefs = null;
		List<Preference> oneUserTestPrefs = null;
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		int userPreferencesCount = prefs.length();
		int testSampleSize = (int) Math.floor(userPreferencesCount * evaluationPercentage);
		Set<Integer> alreadySelectedForUser = alreadySelectedItems.get(userID);
		if (alreadySelectedForUser == null) {
			alreadySelectedForUser = new HashSet<Integer>();
			alreadySelectedItems.put(userID, alreadySelectedForUser);
		} else {
			if (lastGroup) {
				testSampleSize = userPreferencesCount - alreadySelectedForUser.size();
			}
		}
		List<Integer> testSample = createSample(Arrays.asList(ArrayUtils.toObject(prefs.getIDs())), testSampleSize, alreadySelectedForUser);
		alreadySelectedForUser.addAll(testSample);
		for (int itemID: prefs.getIDs()) {
			Preference newPref = new GenericPreference(userID, itemID, dataModel.getPreferenceValue(userID, itemID));
			if (testSample.contains(itemID)) {
				if (oneUserTestPrefs == null) {
					oneUserTestPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTestPrefs.add(newPref);
			} else {
				if (oneUserTrainingPrefs == null) {
					oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTrainingPrefs.add(newPref);
			}
		}
		if (oneUserTrainingPrefs != null) {
			trainingPrefs = new GenericUserPreferenceArray(oneUserTrainingPrefs);
			if (oneUserTestPrefs != null) {
				testPrefs = new GenericUserPreferenceArray(oneUserTestPrefs);
			}
		}
		Pair<GenericUserPreferenceArray, GenericUserPreferenceArray> result = new Pair<GenericUserPreferenceArray, GenericUserPreferenceArray>(trainingPrefs, testPrefs);
//		preferencesSplitted.put(new Pair<Pair<Integer, Integer>, Double>(new Pair<Integer, Integer>(userID, evaluationGroupId), evaluationPercentage), result);
		return result;
	}

	/**
	 * Create sample set of numbers from provided list of IDs with size sampleSize.
	 * @param max
	 * @param sampleSize size of the result sample
	 * @param alreadySelected IDs of users/items that have already been assigned to another group
	 * @return
	 */
	public List<Integer> createSample(List<Integer> ids, int sampleSize, Set<Integer> alreadySelected) {
		int maxId = Collections.max(ids).intValue();
		Set<Integer> results = new HashSet<Integer>();
		for (int i = 0; i < sampleSize; i++) {
			int rand = random.nextInt(maxId + 1);
			while ((!ids.contains(rand)) || results.contains(rand) || alreadySelected.contains(rand)) {
				rand = random.nextInt(maxId + 1);
			}
			results.add(rand);
		}
		return new ArrayList<Integer>(results);
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
