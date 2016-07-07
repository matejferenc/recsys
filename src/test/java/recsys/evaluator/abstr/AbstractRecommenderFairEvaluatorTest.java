package recsys.evaluator.abstr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.MockIterator;
import org.apache.mahout.common.Pair;
import org.junit.Test;
import org.mockito.Mockito;

import recsys.evaluator.RMSRecommenderFairEvaluator;
import recsys.movielens.dataset.Movielens1MDataset;
import recsys.notebooks.dataset.NotebooksDataset;
import recsys.sushi.dataset.SushiDataset;
import static org.junit.Assert.*;

public class AbstractRecommenderFairEvaluatorTest {
	
	@Test
	public void sampleTest() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		RMSRecommenderFairEvaluator evaluator = new RMSRecommenderFairEvaluator(dataModel);
		List<Long> userIDs = evaluator.getUserIDs();
		Set<Long> alreadySelectedUsers = new HashSet<Long>();
		List<Long> sample1 = evaluator.createSample(userIDs, 36, alreadySelectedUsers);
		List<Long> sample2 = evaluator.createSample(userIDs, 36, alreadySelectedUsers);
		List<Long> sample3 = evaluator.createSample(userIDs, 36, alreadySelectedUsers);
		assertEquals(36, sample1.size());
		assertEquals(36, sample2.size());
		assertEquals(36, sample3.size());
	}

	@Test
	public void testSplittingUsers1() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		RMSRecommenderFairEvaluator evaluator = new RMSRecommenderFairEvaluator(dataModel);
		for (int i = 0; i < 2; i++) {
			List<List<Long>> userGroups = evaluator.splitUsers(0.3333);
			assertEquals(3, userGroups.size());
			assertArrayEquals(new Long[] {65L, 1L, 2L, 68L, 5L, 70L, 72L, 8L, 9L, 73L, 74L, 76L, 13L, 79L, 18L, 22L, 87L, 24L, 89L, 25L, 92L, 97L, 98L, 99L, 42L, 43L, 108L, 46L, 51L, 52L, 54L, 55L, 56L, 59L, 61L, 63L}, userGroups.get(0).toArray());
			assertArrayEquals(new Long[] {64L, 66L, 67L, 4L, 71L, 10L, 11L, 12L, 14L, 16L, 83L, 19L, 21L, 86L, 23L, 88L, 26L, 29L, 31L, 32L, 96L, 35L, 100L, 36L, 101L, 38L, 39L, 104L, 105L, 45L, 47L, 50L, 57L, 58L, 60L, 62L}, userGroups.get(1).toArray());
			assertArrayEquals(new Long[] {3L, 69L, 6L, 7L, 75L, 77L, 78L, 15L, 80L, 81L, 17L, 82L, 84L, 20L, 85L, 90L, 27L, 91L, 28L, 93L, 30L, 94L, 95L, 33L, 34L, 37L, 102L, 103L, 40L, 41L, 106L, 107L, 44L, 48L, 49L, 53L}, userGroups.get(2).toArray());
		}
	}
	
	@Test
	public void testSplittingNotebooksDataset1() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		RMSRecommenderFairEvaluator evaluator = new RMSRecommenderFairEvaluator(dataModel);
		double evaluationPercentage = 0.25;
		Pair<GenericUserPreferenceArray, GenericUserPreferenceArray> splitOneUsersPrefs = evaluator.splitOneUsersPrefs(evaluationPercentage, 1L, 0, false);
		int trainSize = splitOneUsersPrefs.getFirst().length();
		int testSize = splitOneUsersPrefs.getSecond().length();
		assertEquals(150, trainSize);
		assertEquals(50, testSize);
	}
	
	@Test
	public void testSplittingSampleDataset() throws Exception {
		DataModel dataModel = createSampleDataModel();
		Set<Pair<Long, Long>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(18, testDataset.size());
	}

	private DataModel createSampleDataModel() {
		FastByIDMap<PreferenceArray> userData = new FastByIDMap<PreferenceArray>();
		List<Preference> prefs = new ArrayList<Preference>();
		for (int i = 0; i < 3; i++) {
			prefs.clear();
			for (int j = 0; j < 6; j++) {
				prefs.add(new GenericPreference(i, j, 0.1f));
			}
			userData.put(i, new GenericUserPreferenceArray(prefs));
		}
		DataModel dataModel = new GenericDataModel(userData);
		return dataModel;
	}
	
	@Test
	public void testSplittingNotebooksDataset() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		Set<Pair<Long, Long>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(21600, testDataset.size());
	}
	
	@Test
	public void testSplittingIsCalled4Times() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		RMSRecommenderFairEvaluator evaluator = new RMSRecommenderFairEvaluator(dataModel);
		RMSRecommenderFairEvaluator spyEvaluator = Mockito.spy(evaluator);
		double testingPercentage = 0.3333;
		double evaluationPercentage = 0.25;
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = spyEvaluator.splitDatset(testingPercentage, evaluationPercentage);
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			Mockito.verify(spyEvaluator, Mockito.times(3)).splitOneUsersPrefs(Mockito.eq(evaluationPercentage), Mockito.eq(userID), Mockito.anyInt(), Mockito.eq(false));
			Mockito.verify(spyEvaluator, Mockito.times(1)).splitOneUsersPrefs(Mockito.eq(evaluationPercentage), Mockito.eq(userID), Mockito.anyInt(), Mockito.eq(true));
		}
	}
	
	@Test
	public void testAllItemsAreSelectedForTesting() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		RMSRecommenderFairEvaluator evaluator = new RMSRecommenderFairEvaluator(dataModel);
		double testingPercentage = 0.3333;
		double evaluationPercentage = 0.25;
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = evaluator.splitDatset(testingPercentage, evaluationPercentage);
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		Map<Long, Set<Long>> alreadySelectedItems = evaluator.getAlreadySelectedItems();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			Set<Long> alreadySelectedItemsForUser = alreadySelectedItems.get(userID);
			int expectedPreferencesCount = dataModel.getPreferencesFromUser(userID).length();
			assertEquals(expectedPreferencesCount, alreadySelectedItemsForUser.size());
		}
	}
	
	@Test
	public void testSplittingSushiDataset() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		Set<Pair<Long, Long>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(50000, testDataset.size());
	}
	
	@Test
	public void testSplittingMovielensDataset() throws Exception {
		DataModel dataModel = new Movielens1MDataset().build();
		Set<Pair<Long, Long>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(1000209, testDataset.size());
	}
	
	private void assertSplitEquals(List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> dataset1, List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> dataset2) {
		for (int i = 0; i < dataset1.size(); i++) {
			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testTrain1InPart = dataset1.get(i);
			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> testTrain2InPart = dataset2.get(i);
			assertEquals(testTrain1InPart.getFirst().size(), testTrain2InPart.getFirst().size());
			assertEquals(testTrain1InPart.getSecond().size(), testTrain2InPart.getSecond().size());
			LongPrimitiveIterator iterator1 = testTrain1InPart.getFirst().keySetIterator();
			while (iterator1.hasNext()) {
				Long userID = iterator1.next();
				PreferenceArray preferenceArray1 = testTrain1InPart.getFirst().get(userID);
				PreferenceArray preferenceArray2 = testTrain2InPart.getFirst().get(userID);
				assertPreferenceArrayEquals(preferenceArray1, preferenceArray2);
			}
		}
	}

	private void assertPreferenceArrayEquals(PreferenceArray preferenceArray1, PreferenceArray preferenceArray2) {
		long[] iDs1 = preferenceArray1.getIDs();
		long[] iDs2 = preferenceArray2.getIDs();
		assertEquals(iDs1.length, iDs2.length);
		for (int i = 0; i < iDs1.length; i++) {
			for (int j = 0; j < iDs2.length; j++) {
				if (iDs2[j] == iDs1[i]) {
					assertTrue(preferenceArray1.hasPrefWithItemID(iDs1[i]));
					assertTrue(preferenceArray2.hasPrefWithItemID(iDs1[i]));
				}
			}
		}
	}

	private Set<Pair<Long, Long>> assertTestDatasetCorrectlyCreated(DataModel dataModel) throws TasteException {
		RMSRecommenderFairEvaluator evaluator = new RMSRecommenderFairEvaluator(dataModel);
		double testingPercentage = 0.3333;
		double evaluationPercentage = 0.25;
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = evaluator.splitDatset(testingPercentage, evaluationPercentage);
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatsetCheck = evaluator.splitDatset(testingPercentage, evaluationPercentage);
		assertSplitEquals(splitDatset, splitDatsetCheck);
		assertEquals(12, splitDatset.size());
		Set<Pair<Long, Long>> testDataset = new HashSet<Pair<Long,Long>>();
		for (Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> part : splitDatset) {
			FastByIDMap<PreferenceArray> testPrefs = part.getSecond();
			LongPrimitiveIterator iterator = testPrefs.keySetIterator();
			while (iterator.hasNext()) {
				Long userID = iterator.next();
				PreferenceArray testPreferencesForUser = testPrefs.get(userID);
				for (Preference preference : testPreferencesForUser) {
					Pair<Long, Long> testItem = new Pair<Long, Long>(preference.getUserID(), preference.getItemID());
					assertFalse(testDataset.contains(testItem));
					if (testDataset.contains(testItem)) {
						System.err.println("already contains: " + testItem.toString());
					}
					testDataset.add(testItem);
				}
			}
		}
		
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
			while (itemIDs.hasNext()) {
				Long itemID = itemIDs.next();
				Float preferenceValue = dataModel.getPreferenceValue(userID, itemID);
				if (preferenceValue != null) {
					if (!testDataset.contains(new Pair<Long, Long>(userID, itemID))) {
						System.out.println("is in dataModel but not in testDataset");
					}
				} else {
					if (testDataset.contains(new Pair<Long, Long>(userID, itemID))) {
						System.err.println("is in testDataset but not in dataModel");
					}
				}
				
			}
		}
		return testDataset;
	}
}
