package recsys.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.IntPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.RandomUtils;

import com.google.common.base.Preconditions;

public class DatasetSplitter {
	
	private final int totalGroups;
	
	private int returnedGroupsCount;
	
	private final Random random;

	private final DataModel dataModel;

	public DatasetSplitter(DataModel dataModel, double testingPercentage, double evaluationPercentage) throws TasteException {
		this.dataModel = dataModel;
		Preconditions.checkArgument(testingPercentage >= 0.0 && testingPercentage <= 1.0, "Invalid testingPercentage: " + testingPercentage + ". Must be: 0.0 <= testingPercentage <= 1.0");
		int userGroupsCount = (int) Math.floor(1 / (testingPercentage));
		int evaluationGroupsCount = (int) Math.floor(1 / (evaluationPercentage));
		totalGroups = userGroupsCount * evaluationGroupsCount;
		returnedGroupsCount = 0;
		random = RandomUtils.getRandom(0L);
		List<List<Integer>> userGroups = splitUsers(testingPercentage);
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

	public boolean hasNext() {
		return returnedGroupsCount < totalGroups;
	}
	
	public Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> next() {
		
		returnedGroupsCount++;
	}
	
}
