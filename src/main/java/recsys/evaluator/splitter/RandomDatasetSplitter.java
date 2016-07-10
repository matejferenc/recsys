package recsys.evaluator.splitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

public class RandomDatasetSplitter implements DataSplitter {
	
	private final int totalGroups;
	
	private int returnedGroupsCount;
	
	private final DataModel dataModel;
	
	private Set<Pair<Long, Long>> alreadySelectedPreferences;
	
	private int totalPreferences;

	public RandomDatasetSplitter(DataModel dataModel, int parts) throws TasteException {
		this.dataModel = dataModel;
		Preconditions.checkArgument(parts >= 2, "Invalid parts count: " + parts + ". Must be at least 2");
		totalGroups = parts;
		returnedGroupsCount = 0;
		calculateTotalPreferencesCount();
		alreadySelectedPreferences = new HashSet<Pair<Long,Long>>();
	}

	private void calculateTotalPreferencesCount() throws TasteException {
		totalPreferences = 0;
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			int preferencesForUser = dataModel.getPreferencesFromUser(userID).length();
			totalPreferences += preferencesForUser;
		}
	}
	
	public Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> createTestAndTrainDatasetForPart(int groupId) throws TasteException {
		boolean lastGroup = groupId == (totalGroups - 1);
		Random random = RandomUtils.getRandom(groupId);
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>();
		FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>();
		Set<Long> itemIDs = getAllItemIDs();
		Set<Long> userIDs = getAllUserIDs();
		Set<Pair<Long, Long>> randomSplit;
		if (lastGroup) {
			randomSplit = getRestOfPreferences(userIDs, itemIDs);
		} else {
			int preferencesInGroup = totalPreferences / totalGroups;
			randomSplit = getRandomPreferences(random, userIDs, itemIDs, preferencesInGroup);
		}
		alreadySelectedPreferences.addAll(randomSplit);
		
		for (long userID: userIDs) {
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			long[] itemIDsArray = preferencesFromUser.getIDs();
			List<Preference> oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
			List<Preference> oneUserTestPrefs = Lists.newArrayListWithCapacity(3);
			for (long itemID : itemIDsArray) {
				Preference newPref = new GenericPreference(userID, itemID, dataModel.getPreferenceValue(userID, itemID));
				if (randomSplit.contains(new Pair<Long, Long>(userID, itemID))) {
					oneUserTestPrefs.add(newPref);
				} else {
					oneUserTrainingPrefs.add(newPref);
				}
			}
			trainingPrefs.put(userID, new GenericUserPreferenceArray(oneUserTrainingPrefs));
			testPrefs.put(userID, new GenericUserPreferenceArray(oneUserTestPrefs));
		}
		Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testAndTrainDatasetForPart = new Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>(trainingPrefs, testPrefs);
		return testAndTrainDatasetForPart;
	}

	private Set<Long> getAllItemIDs() throws TasteException {
		Set<Long> itemIDs = new HashSet<Long>();
		LongPrimitiveIterator itemIDIterator = this.dataModel.getItemIDs();
		while (itemIDIterator.hasNext()) {
			Long itemID = itemIDIterator.next();
			itemIDs.add(itemID);
		}
		return itemIDs;
	}
	
	Set<Long> getAllUserIDs() throws TasteException {
		Set<Long> userIDs = new HashSet<Long>();
		LongPrimitiveIterator userIDIterator = this.dataModel.getUserIDs();
		while (userIDIterator.hasNext()) {
			Long userID = userIDIterator.next();
			userIDs.add(userID);
		}
		return userIDs;
	}
	
	private Set<Pair<Long, Long>> getRandomPreferences(Random random, Set<Long> userIDs, Set<Long> itemIDs, int count) throws TasteException {
		int maxUserID = Collections.max(userIDs).intValue();
		int maxItemID = Collections.max(itemIDs).intValue();
		Set<Pair<Long, Long>> result = new HashSet<Pair<Long, Long>>();
		while (result.size() != count) {
			Long userID = (long) random.nextInt(maxUserID + 1);
			if (userIDs.contains(userID)) {
				Long itemID = (long) random.nextInt(maxItemID + 1);
				if (itemIDs.contains(itemID)) {
					Float preferenceValue = dataModel.getPreferenceValue(userID, itemID);
					if (preferenceValue != null) {
						Pair<Long, Long> pair = new Pair<Long, Long>(userID, itemID);
						if (!alreadySelectedPreferences.contains(pair) && !result.contains(pair)) {
							result.add(pair);
						}
					}
				}
			}
		}
		return result;
	}
	
	private Set<Pair<Long, Long>> getRestOfPreferences(Set<Long> userIDs, Set<Long> itemIDs) throws TasteException {
		Set<Pair<Long, Long>> result = new HashSet<Pair<Long, Long>>();
		for (Long userID : userIDs) {
			for (Long itemID : itemIDs) {
				Float preferenceValue = dataModel.getPreferenceValue(userID, itemID);
				if (preferenceValue != null) {
					Pair<Long, Long> pair = new Pair<Long, Long>(userID, itemID);
					if (!alreadySelectedPreferences.contains(pair)) {
						result.add(pair);
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public boolean hasNext() {
		return returnedGroupsCount < totalGroups;
	}
	
	@Override
	public Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> next() throws TasteException {
		Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testAndTrainDatasetForPart = createTestAndTrainDatasetForPart(returnedGroupsCount);
		returnedGroupsCount++;
		return testAndTrainDatasetForPart;
	}

	List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset() throws TasteException {
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> result = new ArrayList<Pair<FastByIDMap<PreferenceArray>,FastByIDMap<PreferenceArray>>>();
		while (this.hasNext()) {
			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> next = this.next();
			result.add(next);
		}
		return result;
	}

	public int getTotalGroups() {
		return totalGroups;
	}
	
}
