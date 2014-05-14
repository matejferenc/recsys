package allstate.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import allstate.datasets.Allstate;
import allstate.model.AllstateDataModel;
import allstate.model.Record;
import allstate.recommender.AllstateRecommender;

public class AllstateEvaluator {

	private AllstateRecommender recommender;
	private AllstateDataModel model;

	public AllstateEvaluator(AllstateRecommender recommender, AllstateDataModel model) {
		this.recommender = recommender;
		this.model = model;
	}

	public double evaluate(double testSetSizeRatio, TreeMap<Long, Integer> notGuessedCounts, TreeMap<Integer, Double> parametersNotGuessedHistogram) {
		Map<Long, Record> testResults = new HashMap<>();
		AllstateDataModel testSet = createRandomTestSet(model, testSetSizeRatio, testResults);
		Map<Long, List<Integer>> recommended = recommender.recommend(testSet);
		return countSuccess(recommended, testResults, notGuessedCounts, parametersNotGuessedHistogram) / (double) recommended.size();
	}

	private float countSuccess(Map<Long, List<Integer>> recommended, Map<Long, Record> testResults, TreeMap<Long, Integer> notGuessedCounts,
			TreeMap<Integer, Double> parametersNotGuessedHistogram) {
		assert recommended.size() == testResults.size();
		int successCount = 0;
		for (Entry<Long, List<Integer>> entry : recommended.entrySet()) {
			Long userID = entry.getKey();
			Record record = testResults.get(userID);
			List<Integer> predicted = entry.getValue();
			List<Integer> actual = transformResult(record);
			int diffCount = 0;
			if ((diffCount = differenceCount(predicted, actual, parametersNotGuessedHistogram)) == 0) {
				successCount++;
			}
			notGuessedCounts.put(userID, diffCount);
		}
		return successCount;
	}

	private int differenceCount(List<Integer> predicted, List<Integer> actual, TreeMap<Integer, Double> parametersNotGuessedHistogram) {
		assert predicted.size() == actual.size() && actual.size() == 7;
		int differenceCount = 0;
		for (int i = 0; i < predicted.size(); i++) {
			if (!predicted.get(i).equals(actual.get(i))) {
				differenceCount++;
				int parameterNumber = 16 + i;
				parametersNotGuessedHistogram.put(parameterNumber, parametersNotGuessedHistogram.get(parameterNumber) + 1);
			}
		}
		return differenceCount;
	}

	private List<Integer> transformResult(Record record) {
		List<Integer> result = new ArrayList<>();
		result.add(Integer.parseInt(record.get(16)));
		result.add(Integer.parseInt(record.get(17)));
		result.add(Integer.parseInt(record.get(18)));
		result.add(Integer.parseInt(record.get(19)));
		result.add(Integer.parseInt(record.get(20)));
		result.add(Integer.parseInt(record.get(21)));
		result.add(Integer.parseInt(record.get(22)));
		return result;
	}

	/**
	 * Returns test set. Alters given set to be a train set. Test set and train
	 * set contain all users when combined. Test set contains shortened history
	 * of records.
	 * 
	 * @param model
	 * @param ratio
	 *            of test set size to input model size
	 * @return
	 */
	private AllstateDataModel createRandomTestSet(AllstateDataModel model, double ratio, Map<Long, Record> testResults) {
		Map<Long, List<Record>> testDataset = new HashMap<Long, List<Record>>();
		Iterator<Map.Entry<Long, List<Record>>> it = model.dataset.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, List<Record>> entry = it.next();
			double random = Math.random();
			// ak je < dam do test set, inak necham v train set
			if (random < ratio) {
				testDataset.put(entry.getKey(), entry.getValue());
				it.remove();
			}
		}
		AllstateDataModel testModel = new AllstateDataModel(testDataset);
		filterOutSoldRecords(testModel, testResults);
		shortenTestSetTransactions(testModel);
		return testModel;
	}

	public static AllstateDataModel shortenTestSetTransactions(AllstateDataModel model) {
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			int transactionLength = records.size();
			// vylosujem nahodne cislo a hladam slot kam spadnem
			double random = Math.random();
			double accSlotsRatio = 0;
			for (int slot = 2; slot < transactionLength; slot++) {
				Double ratioToMoveToSlot = Allstate.getProbabilityToMoveToSlot(transactionLength, slot);
				accSlotsRatio += ratioToMoveToSlot;
				// prvy slot, kam spadnem
				if (random < accSlotsRatio) {
					removeRecords(records, transactionLength - slot);
					break;
				}
			}
		}
		return model;
	}

	public static AllstateDataModel extractFirstRecordOnly(AllstateDataModel model) {
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			int transactionLength = records.size();
			// odstranim vsetky okrem prveho zaznamu
			removeRecords(records, transactionLength - 1);
		}
		return model;
	}

	/**
	 * Removes last n records from given list.
	 * 
	 * @param records
	 * @param n
	 */
	private static void removeRecords(List<Record> records, int n) {
		int maxIndex = records.size() - 1;
		for (int i = 0; i < n; i++) {
			records.remove(maxIndex - i);
		}
	}

	public static AllstateDataModel filterOutSoldRecords(AllstateDataModel model, Map<Long, Record> soldRecords) {
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			// odstranime posledny prvok - predany
			int lastIndex = records.size() - 1;
			soldRecords.put(entry.getKey(), records.get(lastIndex));
			records.remove(lastIndex);
			if (records.size() == 0) {
				System.out.print("no records left: " + entry.getKey());
			}
		}
		return model;
	}

	public static List<Long> getRandomTrainUserIds(AllstateDataModel model, double ratio, Random r, List<Long> testUserIds) {
		List<Long> ids = new ArrayList<>();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			Long id = entry.getKey();
			if (r.nextDouble() < ratio) {
				ids.add(id);
			} else {
				testUserIds.add(id);
			}
		}
		return ids;
	}

}
