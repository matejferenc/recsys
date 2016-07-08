package recsys.evaluator.abstr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.IntPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;
import org.junit.Test;
import org.mockito.Mockito;

import recsys.evaluator.DatasetSplitter;
import recsys.movielens.dataset.Movielens1MDataset;
import recsys.notebooks.dataset.NotebooksDataset;
import recsys.sushi.dataset.SushiDataset;

public class DatasetSplitterTest {
	
	@Test
	public void sampleTest() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		DatasetSplitter splitter = new DatasetSplitter(dataModel, 0.3333d, 0.25d);
		List<Integer> userIDs = splitter.getUserIDs();
		Set<Integer> alreadySelectedUsers = new HashSet<Integer>();
		List<Integer> sample1 = splitter.createSample(userIDs, 36, alreadySelectedUsers);
		List<Integer> sample2 = splitter.createSample(userIDs, 36, alreadySelectedUsers);
		List<Integer> sample3 = splitter.createSample(userIDs, 36, alreadySelectedUsers);
		assertEquals(36, sample1.size());
		assertEquals(36, sample2.size());
		assertEquals(36, sample3.size());
	}

	@Test
	public void testSplittingUsers1() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		DatasetSplitter splitter = new DatasetSplitter(dataModel, 0.3333, 0.25);
		for (int i = 0; i < 2; i++) {
			List<List<Integer>> userGroups = splitter.splitUsers(0.3333);
			assertEquals(3, userGroups.size());
			assertArrayEquals(new Integer[] {65, 1, 2, 68, 5, 70, 72, 8, 9, 73, 74, 76, 13, 79, 18, 22, 87, 24, 89, 25, 92, 97, 98, 99, 42, 43, 108, 46, 51, 52, 54, 55, 56, 59, 61, 63}, userGroups.get(0).toArray());
			assertArrayEquals(new Integer[] {64, 66, 67, 4, 71, 10, 11, 12, 14, 16, 83, 19, 21, 86, 23, 88, 26, 29, 31, 32, 96, 35, 100, 36, 101, 38, 39, 104, 105, 45, 47, 50, 57, 58, 60, 62}, userGroups.get(1).toArray());
			assertArrayEquals(new Integer[] {3, 69, 6, 7, 75, 77, 78, 15, 80, 81, 17, 82, 84, 20, 85, 90, 27, 91, 28, 93, 30, 94, 95, 33, 34, 37, 102, 103, 40, 41, 106, 107, 44, 48, 49, 53}, userGroups.get(2).toArray());
		}
	}
	
	@Test
	public void testSplittingNotebooksDataset1() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		DatasetSplitter splitter = new DatasetSplitter(dataModel, 0.3333, 0.25);
		Pair<GenericUserPreferenceArray, GenericUserPreferenceArray> splitOneUsersPrefs = splitter.splitOneUsersPrefs(1, 0, false);
		int trainSize = splitOneUsersPrefs.getFirst().length();
		int testSize = splitOneUsersPrefs.getSecond().length();
		assertEquals(150, trainSize);
		assertEquals(50, testSize);
	}
	
	@Test
	public void testSplittingSampleDataset() throws Exception {
		DataModel dataModel = createSampleDataModel();
		Set<Pair<Integer, Integer>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(18, testDataset.size());
	}

	private DataModel createSampleDataModel() {
		FastByIDMap<PreferenceArray> userData = new FastByIDMap<PreferenceArray>();
		List<Preference> prefs = new ArrayList<Preference>();
		for (int i = 0; i < 3; i++) {
			prefs.clear();
			for (int j = 0; j < 6; j++) {
				prefs.add(new GenericPreference(i, j, 0.1));
			}
			userData.put(i, new GenericUserPreferenceArray(prefs));
		}
		DataModel dataModel = new GenericDataModel(userData);
		return dataModel;
	}
	
	@Test
	public void testSplittingNotebooksDataset() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		Set<Pair<Integer, Integer>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(21600, testDataset.size());
	}
	
	@Test
	public void testSplittingIsCalled4Times() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		Double testingPercentage = 0.3333;
		Double evaluationPercentage = 0.25;
		DatasetSplitter splitter = new DatasetSplitter(dataModel, testingPercentage, evaluationPercentage);
		DatasetSplitter spySplitter = Mockito.spy(splitter);
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = spySplitter.splitDatset();
		IntPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Integer userID = userIDs.next();
			Mockito.verify(spySplitter, Mockito.times(3)).splitOneUsersPrefs(Mockito.eq(userID), Mockito.anyInt(), Mockito.eq(false));
			Mockito.verify(spySplitter, Mockito.times(1)).splitOneUsersPrefs(Mockito.eq(userID), Mockito.anyInt(), Mockito.eq(true));
		}
	}
	
	@Test
	public void testAllItemsAreSelectedForTesting() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		Double testingPercentage = 0.3333;
		Double evaluationPercentage = 0.25;
		DatasetSplitter evaluator = new DatasetSplitter(dataModel, testingPercentage, evaluationPercentage);
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = evaluator.splitDatset();
		IntPrimitiveIterator userIDs = dataModel.getUserIDs();
		Map<Integer, Set<Integer>> alreadySelectedItems = evaluator.getAlreadySelectedItems();
		while (userIDs.hasNext()) {
			Integer userID = userIDs.next();
			Set<Integer> alreadySelectedItemsForUser = alreadySelectedItems.get(userID);
			int expectedPreferencesCount = dataModel.getPreferencesFromUser(userID).length();
			assertEquals(expectedPreferencesCount, alreadySelectedItemsForUser.size());
		}
	}
	
	@Test
	public void testSplittingSushiDataset() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		Set<Pair<Integer, Integer>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(50000, testDataset.size());
	}
	
	@Test
	public void testSplittingMovielensDataset() throws Exception {
		DataModel dataModel = new Movielens1MDataset().build();
		Set<Pair<Integer, Integer>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(1000209, testDataset.size());
	}
	
	private void assertSplitEquals(List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> dataset1, List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> dataset2) {
		for (int i = 0; i < dataset1.size(); i++) {
			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testTrain1InPart = dataset1.get(i);
			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testTrain2InPart = dataset2.get(i);
			assertEquals(testTrain1InPart.getFirst().size(), testTrain2InPart.getFirst().size());
			assertEquals(testTrain1InPart.getSecond().size(), testTrain2InPart.getSecond().size());
			IntPrimitiveIterator iterator1 = testTrain1InPart.getFirst().keySetIterator();
			while (iterator1.hasNext()) {
				Integer userID = iterator1.next();
				PreferenceArray preferenceArray1 = testTrain1InPart.getFirst().get(userID);
				PreferenceArray preferenceArray2 = testTrain2InPart.getFirst().get(userID);
				assertPreferenceArrayEquals(preferenceArray1, preferenceArray2);
			}
		}
	}

	private void assertPreferenceArrayEquals(PreferenceArray preferenceArray1, PreferenceArray preferenceArray2) {
		Integer[] iDs1 = preferenceArray1.getIDs();
		Integer[] iDs2 = preferenceArray2.getIDs();
		assertEquals(iDs1.length, iDs2.length);
		for (int i = 0; i < iDs1.length; i++) {
			for (int j = 0; j < iDs2.length; j++) {
				if (iDs2[j].equals(iDs1[i])) {
					assertTrue(preferenceArray1.hasPrefWithItemID(iDs1[i]));
					assertTrue(preferenceArray2.hasPrefWithItemID(iDs1[i]));
				}
			}
		}
	}

	private Set<Pair<Integer, Integer>> assertTestDatasetCorrectlyCreated(DataModel dataModel) throws TasteException {
		Double testingPercentage = 0.3333;
		Double evaluationPercentage = 0.25;
		DatasetSplitter splitter = new DatasetSplitter(dataModel, testingPercentage, evaluationPercentage);
		DatasetSplitter splitter1 = new DatasetSplitter(dataModel, testingPercentage, evaluationPercentage);
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = splitter.splitDatset();
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatsetCheck = splitter1.splitDatset();
		assertSplitEquals(splitDatset, splitDatsetCheck);
		assertEquals(12, splitDatset.size());
		Set<Pair<Integer, Integer>> testDataset = new HashSet<Pair<Integer, Integer>>();
		for (Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> part : splitDatset) {
			FastByIDMap<PreferenceArray> testPrefs = part.getSecond();
			IntPrimitiveIterator iterator = testPrefs.keySetIterator();
			while (iterator.hasNext()) {
				Integer userID = iterator.next();
				PreferenceArray testPreferencesForUser = testPrefs.get(userID);
				for (Preference preference : testPreferencesForUser) {
					Pair<Integer, Integer> testItem = new Pair<Integer, Integer>(preference.getUserID(), preference.getItemID());
					assertFalse(testDataset.contains(testItem));
					if (testDataset.contains(testItem)) {
						System.err.println("already contains: " + testItem.toString());
					}
					testDataset.add(testItem);
				}
			}
		}
		
		IntPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Integer userID = userIDs.next();
			IntPrimitiveIterator itemIDs = dataModel.getItemIDs();
			while (itemIDs.hasNext()) {
				Integer itemID = itemIDs.next();
				Double preferenceValue = dataModel.getPreferenceValue(userID, itemID);
				if (preferenceValue != null) {
					if (!testDataset.contains(new Pair<Integer, Integer>(userID, itemID))) {
						System.out.println("is in dataModel but not in testDataset");
					}
				} else {
					if (testDataset.contains(new Pair<Integer, Integer>(userID, itemID))) {
						System.err.println("is in testDataset but not in dataModel");
					}
				}
				
			}
		}
		return testDataset;
	}
}
