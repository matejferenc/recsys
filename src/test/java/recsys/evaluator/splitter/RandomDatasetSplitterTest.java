package recsys.evaluator.splitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.apache.mahout.common.Pair;
import org.junit.Test;

import recsys.movielens.dataset.Movielens1MDataset;
import recsys.notebooks.dataset.NotebooksDataset;
import recsys.sushi.dataset.SushiDataset;

public class RandomDatasetSplitterTest {
	
	private static final int PARTS = 5;

	@Test
	public void splittingNotebooksDataset2()  throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		RandomDatasetSplitter splitter = new RandomDatasetSplitter(dataModel, PARTS);
		while (splitter.hasNext()) {
			Pair<FastByIDMap<PreferenceArray>,FastByIDMap<PreferenceArray>> pair = splitter.next();
			assertEquals(36, pair.getFirst().size());
			assertEquals(36, pair.getSecond().size());
			assertEmptyIntersection(pair.getFirst(), pair.getSecond());
		}
	}
	
	public static void assertEmptyIntersection(FastByIDMap<PreferenceArray> first, FastByIDMap<PreferenceArray> second) {
		LongPrimitiveIterator keySetIterator = second.keySetIterator();
		while (keySetIterator.hasNext()) {
			Long userID = keySetIterator.next();
			PreferenceArray trainingPreferences = first.get(userID);
			PreferenceArray testingPreferences = second.get(userID);
			assertEmptyIntersection(testingPreferences.getIDs(), trainingPreferences.getIDs());
		}
	}

	static void assertEmptyIntersection(long[] iDs, long[] iDs2) {
		for (int i = 0; i < iDs.length; i++) {
			for (int j = 0; j < iDs2.length; j++) {
				assertFalse(iDs[i] == iDs2[j]);
			}
		}
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
	public void testSplittingSushiDataset() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		Set<Pair<Long, Long>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(50000, testDataset.size());
	}
	
	@Test
	public void splittingSushiDataset2()  throws Exception {
		DataModel dataModel = new SushiDataset().build();
		RandomDatasetSplitter splitter = new RandomDatasetSplitter(dataModel, PARTS);
		while (splitter.hasNext()) {
			Pair<FastByIDMap<PreferenceArray>,FastByIDMap<PreferenceArray>> pair = splitter.next();
			assertEquals(5000, pair.getFirst().size());
			assertTrue(5000 == pair.getSecond().size() || 1666 == pair.getSecond().size());
			assertEmptyIntersection(pair.getFirst(), pair.getSecond());
		}
	}
	
	@Test
	public void testSplittingMovielensDataset() throws Exception {
		DataModel dataModel = new Movielens1MDataset().build();
		Set<Pair<Long, Long>> testDataset = assertTestDatasetCorrectlyCreated(dataModel);
		assertEquals(1000209, testDataset.size());
	}
	
	@Test
	public void splittingMovielensDataset2()  throws Exception {
		DataModel dataModel = new Movielens1MDataset().build();
		RandomDatasetSplitter splitter = new RandomDatasetSplitter(dataModel, PARTS);
		while (splitter.hasNext()) {
			Pair<FastByIDMap<PreferenceArray>,FastByIDMap<PreferenceArray>> pair = splitter.next();
			assertTrue(6040 == pair.getFirst().size() || 2012 == pair.getFirst().size());
			assertTrue(6040 == pair.getSecond().size() || 2012 == pair.getSecond().size());
			assertEmptyIntersection(pair.getFirst(), pair.getSecond());
		}
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
		RandomDatasetSplitter splitter = new RandomDatasetSplitter(dataModel, PARTS);
		RandomDatasetSplitter splitter1 = new RandomDatasetSplitter(dataModel, PARTS);
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatset = splitter.splitDatset();
		List<Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>>> splitDatsetCheck = splitter1.splitDatset();
		assertSplitEquals(splitDatset, splitDatsetCheck);
		assertEquals(5, splitDatset.size());
		Set<Pair<Long, Long>> testDataset = new HashSet<Pair<Long, Long>>();
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
