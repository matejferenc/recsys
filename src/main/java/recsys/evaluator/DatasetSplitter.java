package recsys.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.RandomUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class DatasetSplitter {
	
	private final int totalGroups;
	
	private int returnedGroupsCount;
	
	private final Random random;

	private final DataModel dataModel;
	
	private int actualUserGroupId;
	
	private final int userGroupsCount;
	
	private int actualEvaluationGroupId;
	
	private final int evaluationGroupsCount;
	
	private Map<Long, Set<Long>> alreadySelectedItems;

	private double evaluationPercentage;

	private List<List<Long>> userGroups;
	
	private Map<Pair<Double, Integer>, List<List<Long>>> usersSplitted;

	public DatasetSplitter(DataModel dataModel, double testingPercentage, double evaluationPercentage) throws TasteException {
		this.dataModel = dataModel;
		this.evaluationPercentage = evaluationPercentage;
		Preconditions.checkArgument(testingPercentage >= 0.0 && testingPercentage <= 1.0, "Invalid testingPercentage: " + testingPercentage + ". Must be: 0.0 <= testingPercentage <= 1.0");
		userGroupsCount = (int) Math.floor(1 / (testingPercentage));
		evaluationGroupsCount = (int) Math.floor(1 / (evaluationPercentage));
		totalGroups = userGroupsCount * evaluationGroupsCount;
		returnedGroupsCount = 0;
		random = RandomUtils.getRandom(0L);
		alreadySelectedItems = new HashMap<Long, Set<Long>>();
		usersSplitted = new HashMap<Pair<Double,Integer>, List<List<Long>>>();
		userGroups = splitUsers(testingPercentage);
	}
	
	public Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> createTestAndTrainDatasetForPart(int evaluationGroupId, List<Long> userGroup, boolean lastGroup) throws TasteException {
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>();
		FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>();
		List<Long> userIDs = getUserIDs();
		for (long userId: userIDs) {
			if (!userGroup.contains(userId)) {
				//adding preferences from training set
				PreferenceArray prefs = dataModel.getPreferencesFromUser(userId);
				trainingPrefs.put(userId, prefs);
			} else {
				//adding preferences from testing set - have to be split in train and test sets
				Pair<GenericUserPreferenceArray,GenericUserPreferenceArray> splitOneUsersPrefs = splitOneUsersPrefs(userId, evaluationGroupId, lastGroup);
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
	
	/**
	 * Splits the preferences of user with id userId into training and testing data structures. EvaluationPercentage of preferences
	 * will go to testing dataset and 1-evaluationPercentage of preferences will go to testing dataset.
	 * @param evaluationPercentage
	 * @param userID
	 * @param lastGroup flag if the evaluation group is the last one. That means we have to return all the remaining preferences
	 * @throws TasteException
	 */
	public Pair<GenericUserPreferenceArray, GenericUserPreferenceArray> splitOneUsersPrefs(long userID, int evaluationGroupId, boolean lastGroup) throws TasteException {
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
		Set<Long> alreadySelectedForUser = alreadySelectedItems.get(userID);
		if (alreadySelectedForUser == null) {
			alreadySelectedForUser = new HashSet<Long>();
			alreadySelectedItems.put(userID, alreadySelectedForUser);
		} else {
			if (lastGroup) {
				testSampleSize = userPreferencesCount - alreadySelectedForUser.size();
			}
		}
		List<Long> testSample = createSample(Arrays.asList(ArrayUtils.toObject(prefs.getIDs())), testSampleSize, alreadySelectedForUser);
		alreadySelectedForUser.addAll(testSample);
		for (long itemID: prefs.getIDs()) {
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
	
	public List<List<Long>> splitUsers(double testingPercentage) throws TasteException {
		List<Long> userIDs = getUserIDs();
		int numUsers = dataModel.getNumUsers();
		if (usersSplitted.containsKey(new Pair<Double, Integer>(testingPercentage, numUsers))) {
			return usersSplitted.get(new Pair<Double, Integer>(testingPercentage, numUsers));
		}
		int userGroupsCount = (int) Math.round(1 / testingPercentage);
		List<List<Long>> userGroups = new ArrayList<List<Long>>();
		Set<Long> alreadySelectedUsers = new HashSet<Long>();
		for (int i = 0; i < userGroupsCount; i++) {
			int usersInGroup;
			// if last group
			if (i == userGroupsCount - 1) {
				usersInGroup = numUsers - alreadySelectedUsers.size();
			} else {
				usersInGroup = (int) Math.ceil(numUsers * testingPercentage);
			}
			List<Long> evaluationSample = createSample(userIDs, usersInGroup, alreadySelectedUsers);
			alreadySelectedUsers.addAll(evaluationSample);
			userGroups.add(evaluationSample);
		}
		usersSplitted.put(new Pair<Double, Integer>(testingPercentage, numUsers), userGroups);
		return userGroups;
	}
	
	public List<Long> getUserIDs() throws TasteException {
		LongPrimitiveIterator userIDsIterator = dataModel.getUserIDs();
		List<Long> userIDs = new ArrayList<Long>();
		while (userIDsIterator.hasNext()) {
			userIDs.add(userIDsIterator.next());
		}
		return userIDs;
	}
	
	/**
	 * Create sample set of numbers from provided list of IDs with size sampleSize.
	 * @param max
	 * @param sampleSize size of the result sample
	 * @param alreadySelected IDs of users/items that have already been assigned to another group
	 * @return
	 */
	public List<Long> createSample(List<Long> ids, int sampleSize, Set<Long> alreadySelected) {
		int maxId = Collections.max(ids).intValue();
		Set<Long> results = new HashSet<Long>();
		for (int i = 0; i < sampleSize; i++) {
			long rand = random.nextInt(maxId + 1);
			while ((!ids.contains(rand)) || results.contains(rand) || alreadySelected.contains(rand)) {
				rand = random.nextInt(maxId + 1);
			}
			results.add((long) rand);
		}
		return new ArrayList<Long>(results);
	}

	public boolean hasNext() {
		return returnedGroupsCount < totalGroups;
	}
	
	public Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> next() throws TasteException {
		boolean lastGroup = actualEvaluationGroupId == (evaluationGroupsCount - 1);
		List<Long> userGroup = userGroups.get(actualUserGroupId);
		Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testAndTrainDatasetForPart = createTestAndTrainDatasetForPart(actualEvaluationGroupId, userGroup, lastGroup);
		
		if (actualUserGroupId == userGroupsCount - 1) {
			actualUserGroupId = 0;
			actualEvaluationGroupId++;
		} else {
			actualUserGroupId++;
		}
		returnedGroupsCount++;
		return testAndTrainDatasetForPart;
	}

	public Map<Long, Set<Long>> getAlreadySelectedItems() {
		return alreadySelectedItems;
	}

	public List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset() throws TasteException {
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> result = new ArrayList<Pair<FastByIDMap<PreferenceArray>,FastByIDMap<PreferenceArray>>>();
		while (this.hasNext()) {
			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> next = this.next();
			result.add(next);
		}
		return result;
	}
	
}
