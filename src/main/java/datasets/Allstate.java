package datasets;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import allstate.evaluator.AllstateEvaluator;
import dataModel.AllstateDataModel;
import dataModel.Record;

public class Allstate {

	private Properties prop;

	public static void main(String[] args) throws Exception {
		new Allstate().run();
	}

	public Allstate() throws Exception {
		prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
	}

	public AllstateStatsParams run() throws Exception {
		long startTime = new Date().getTime();
		AllstateStatsParams params = execute();
		long endTime = new Date().getTime();
		int s = (int) ((endTime - startTime) / 1000);
		params.seconds = s;
		System.out.println("cas behu: " + s + "s");
		return params;
	}

	public AllstateStatsParams execute() throws Exception {
		String path = prop.getProperty("allstate-train-csv");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");

		AllstateStatsParams params = execute(model);
		params.title = "Allstate";
		return params;
	}

	private AllstateStatsParams execute(AllstateDataModel model) {
		AllstateStatsParams params = new AllstateStatsParams();

		params.userCount = model.dataset.size();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			params.totalRecords += entry.getValue().size();
		}

		params.aHistogram = createHistogramForColumn(model, 16);
		params.bHistogram = createHistogramForColumn(model, 17);
		params.cHistogram = createHistogramForColumn(model, 18);
		params.dHistogram = createHistogramForColumn(model, 19);
		params.eHistogram = createHistogramForColumn(model, 20);
		params.fHistogram = createHistogramForColumn(model, 21);
		params.gHistogram = createHistogramForColumn(model, 22);

		params.aSoldHistogram = createSoldHistogramForColumn(model, 16);
		params.bSoldHistogram = createSoldHistogramForColumn(model, 17);
		params.cSoldHistogram = createSoldHistogramForColumn(model, 18);
		params.dSoldHistogram = createSoldHistogramForColumn(model, 19);
		params.eSoldHistogram = createSoldHistogramForColumn(model, 20);
		params.fSoldHistogram = createSoldHistogramForColumn(model, 21);
		params.gSoldHistogram = createSoldHistogramForColumn(model, 22);

		params.changeHistogram = createChangeHistogramForColumns(model, 16, 17, 18, 19, 20, 21, 22);

		params.transactionLengthHistogram = createTransactionLengthHistogram(model);
		params.transactionLengthSimHistogram = createTransactionLengthSimHistogram(model);

		return params;
	}

	public AllstateStatsParams runOnTestSet() throws Exception {
		long startTime = new Date().getTime();
		AllstateStatsParams params = executeOnTestSet();
		long endTime = new Date().getTime();
		int s = (int) ((endTime - startTime) / 1000);
		params.seconds = s;
		System.out.println("cas behu: " + s + "s");
		return params;
	}

	public AllstateStatsParams executeOnTestSet() throws Exception {
		String path = prop.getProperty("allstate-test-csv");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");

		AllstateStatsParams params = executeOnTestSet(model);
		params.title = "Allstate";
		return params;
	}

	private AllstateStatsParams executeOnTestSet(AllstateDataModel model) {
		AllstateStatsParams params = new AllstateStatsParams();

		params.userCount = model.dataset.size();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			params.totalRecords += entry.getValue().size();
		}

		params.aHistogram = createHistogramForColumn(model, 16);
		params.bHistogram = createHistogramForColumn(model, 17);
		params.cHistogram = createHistogramForColumn(model, 18);
		params.dHistogram = createHistogramForColumn(model, 19);
		params.eHistogram = createHistogramForColumn(model, 20);
		params.fHistogram = createHistogramForColumn(model, 21);
		params.gHistogram = createHistogramForColumn(model, 22);

		params.changeHistogram = createChangeHistogramForColumns(model, 16, 17, 18, 19, 20, 21, 22);

		params.transactionLengthHistogram = createTransactionLengthHistogram(model);

		return params;
	}
	
	
	public AllstateStatsParams runSim() throws Exception {
		long startTime = new Date().getTime();
		AllstateStatsParams params = executeSim();
		long endTime = new Date().getTime();
		int s = (int) ((endTime - startTime) / 1000);
		params.seconds = s;
		System.out.println("cas behu: " + s + "s");
		return params;
	}

	public AllstateStatsParams executeSim() throws Exception {
		String path = prop.getProperty("allstate-train-csv");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");
		
		Map<Long, Record> testResults = new HashMap<>();;
		AllstateEvaluator.filterOutSoldRecords(model, testResults);
		AllstateEvaluator.shortenTestSetTransactions(model);

		AllstateStatsParams params = executeSim(model);
		params.title = "Allstate";
		return params;
	}

	private AllstateStatsParams executeSim(AllstateDataModel model) {
		AllstateStatsParams params = new AllstateStatsParams();

		params.userCount = model.dataset.size();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			params.totalRecords += entry.getValue().size();
		}

		params.aHistogram = createHistogramForColumn(model, 16);
		params.bHistogram = createHistogramForColumn(model, 17);
		params.cHistogram = createHistogramForColumn(model, 18);
		params.dHistogram = createHistogramForColumn(model, 19);
		params.eHistogram = createHistogramForColumn(model, 20);
		params.fHistogram = createHistogramForColumn(model, 21);
		params.gHistogram = createHistogramForColumn(model, 22);

		params.changeHistogram = createChangeHistogramForColumns(model, 16, 17, 18, 19, 20, 21, 22);

		params.transactionLengthHistogram = createTransactionLengthHistogram(model);

		return params;
	}


	private TreeMap<Integer, Double> createTransactionLengthHistogram(AllstateDataModel model) {
		TreeMap<Integer, Double> histogram = new TreeMap<Integer, Double>();
		histogram.put(1, 0.0);
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			Integer transactionLength = records.size();
			if (histogram.containsKey(transactionLength)) {
				Double actualCount = histogram.get(transactionLength);
				histogram.put(transactionLength, actualCount + 1);
			} else {
				histogram.put(transactionLength, 1.0);
			}
		}
		for (Entry<Integer, Double> entry : histogram.entrySet()) {
			entry.setValue(entry.getValue() / (float) model.dataset.size());
		}
		return histogram;
	}

	private TreeMap<Integer, Double> createTransactionLengthSimHistogram(AllstateDataModel model) {
		model = model.shallowCopy();
		model = AllstateEvaluator.filterOutSoldRecords(model, new HashMap<Long, Record>());
		TreeMap<Integer, Double> histogram = new TreeMap<Integer, Double>();
		histogram.put(1, 0.0);
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			Integer transactionLength = records.size();
			if (histogram.containsKey(transactionLength)) {
				Double actualCount = histogram.get(transactionLength);
				histogram.put(transactionLength, actualCount + 1);
			} else {
				histogram.put(transactionLength, 1.0);
			}
		}
		// simulacia odstranenia casti historie:
		for (Entry<Integer, Double> entry : histogram.entrySet()) {
			Double count = entry.getValue();
			Integer transactionLength = entry.getKey();
			for (int i = 1; i < transactionLength; i++) {
				Double factor = getRatioToMoveToSlot(transactionLength, i);
				double debt = count * factor;
				histogram.put(i, histogram.get(i) + debt);
				histogram.put(transactionLength, histogram.get(transactionLength) - debt);
			}

		}
		for (Entry<Integer, Double> entry : histogram.entrySet()) {
			entry.setValue(entry.getValue() / (float) model.dataset.size());
		}
		return histogram;
	}

	public static Double getRatioToMoveToSlot(int transactionLength, int slot) {
		Double q = 1 / ((double) 3.0 * transactionLength);
		Double p = (double) transactionLength;
		Double a = 2 * q / (1 - p);
		Double b = 1 / p + q - 2 * q / (1 - p);
		Double factor = a * slot + b;
		return factor;
	}

	private TreeMap<Integer, Float> createChangeHistogramForColumns(AllstateDataModel model, Integer... columns) {
		TreeMap<Integer, Float> histogram = new TreeMap<Integer, Float>();
		for (int i = 0; i < columns.length; i++) {
			int changes = 0;
			int recordCount = model.dataset.size();
			for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
				List<Record> records = entry.getValue();
				Record first = records.get(0);
				String lastValue = null;
				for (Record record : records) {
					String currentValue = (String) record.get(columns[i]);
					if (record != first) {
						if (!currentValue.equals(lastValue)) {
							changes++;
							break;
						}
					}
					lastValue = currentValue;
				}
			}
			Float percentChanges = changes / (float) recordCount;
			histogram.put(columns[i], percentChanges);
		}
		return histogram;
	}

	private TreeMap<String, Integer> createHistogramForColumn(AllstateDataModel model, int column) {
		TreeMap<String, Integer> histogram = new TreeMap<String, Integer>();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			for (Record record : records) {
				String parameterA = (String) record.get(column);
				if (histogram.containsKey(parameterA)) {
					Integer actualCount = histogram.get(parameterA);
					histogram.put(parameterA, actualCount + 1);
				} else {
					histogram.put(parameterA, 1);
				}
			}
		}
		return histogram;
	}

	private TreeMap<String, Integer> createSoldHistogramForColumn(AllstateDataModel model, int column) {
		TreeMap<String, Integer> histogram = new TreeMap<String, Integer>();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			for (Record record : records) {
				// len pre predane poistenia
				if (record.get(1).toString().equals("1")) {
					String parameterA = (String) record.get(column);
					if (histogram.containsKey(parameterA)) {
						Integer actualCount = histogram.get(parameterA);
						histogram.put(parameterA, actualCount + 1);
					} else {
						histogram.put(parameterA, 1);
					}
				}
			}
		}
		return histogram;
	}

}
