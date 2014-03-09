package allstate.datasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.mahout.common.Pair;

import allstate.model.AllstateDataModel;
import allstate.model.Record;
import datasets.AllstateStatsParams;

public abstract class Allstate {

	protected Properties prop;

	public static Map<Integer, Integer> mostFrequentParameterValues = new HashMap<>();
	static {
		mostFrequentParameterValues.put(16, 1);
		mostFrequentParameterValues.put(17, 0);
		mostFrequentParameterValues.put(18, 3);
		mostFrequentParameterValues.put(19, 3);
		mostFrequentParameterValues.put(20, 0);
		mostFrequentParameterValues.put(21, 2);
		mostFrequentParameterValues.put(22, 2);
	}

	public Allstate() {
		prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	abstract AllstateStatsParams run() throws Exception;

	protected TreeMap<Integer, Double> createTransactionLengthHistogram(AllstateDataModel model) {
		TreeMap<Integer, Double> histogram = new TreeMap<Integer, Double>();
		histogram.put(1, 0.0);
		histogram.put(2, 0.0);
		histogram.put(3, 0.0);
		histogram.put(4, 0.0);
		histogram.put(5, 0.0);
		histogram.put(6, 0.0);
		histogram.put(7, 0.0);
		histogram.put(8, 0.0);
		histogram.put(9, 0.0);
		histogram.put(10, 0.0);
		histogram.put(11, 0.0);
		histogram.put(12, 0.0);
		histogram.put(13, 0.0);
		histogram.put(14, 0.0);
		histogram.put(15, 0.0);
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

	public static Double getRatioToMoveToSlot(int transactionLength, int slot) {
		Double q = 1 / ((double) 0.7 * transactionLength);
		Double p = (double) transactionLength;
		Double a = 2 * q / (1 - p);
		Double b = 1 / p + q - 2 * q / (1 - p);
		Double factor = a * slot + b;
		return factor;
	}

	protected TreeMap<Integer, Float> createChangeHistogramForColumns(AllstateDataModel model, Integer... columns) {
		TreeMap<Integer, Float> histogram = new TreeMap<Integer, Float>();
		for (int i = 0; i < columns.length; i++) {
			int changes = 0;
			int recordCount = countTotalRecords(model);
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

	protected TreeMap<Integer, Float> createLastDifferentHistogramForColumns(AllstateDataModel model, Integer... columns) {
		TreeMap<Integer, Float> histogram = new TreeMap<Integer, Float>();
		for (int i = 0; i < columns.length; i++) {
			int lastDifferentCount = 0;
			int userCount = model.dataset.size();
			outer: for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
				List<Record> records = entry.getValue();
				Record last = records.get(records.size() - 1);
				for (Record record : records) {
					String currentValue = (String) record.get(columns[i]);
					if (record != last) {
						if (currentValue.equals(last.get(columns[i]))) {
							continue outer;
						}
					}
				}
				lastDifferentCount++;
			}
			Float percentChanges = lastDifferentCount / (float) userCount;
			histogram.put(columns[i], percentChanges);
		}
		return histogram;
	}

	protected TreeMap<String, Integer> createHistogramForColumn(AllstateDataModel model, int column, TreeMap<Integer, TreeMap<String, Integer>> histograms) {
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
		histograms.put(column, histogram);
		return histogram;
	}

	protected TreeMap<String, Integer> createSoldHistogramForColumn(AllstateDataModel model, int column) {
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

	protected int countTotalRecords(AllstateDataModel model) {
		int totalRecords = 0;
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			totalRecords += entry.getValue().size();
		}
		return totalRecords;
	}

	/**
	 * 
	 * @param model
	 * @param a_gHistograms
	 * @param i
	 * @param j jeden z cielovych parametrov - cislo od 16 do 22
	 * @return
	 */
	protected IntraclassHistogram createIntraclassHistogram(AllstateDataModel model, TreeMap<Integer, TreeMap<String, Integer>> a_gHistograms, int i, int j) {
		IntraclassHistogram histogram = new IntraclassHistogram();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			List<Record> records = entry.getValue();
			for (Record record : records) {
				String classIString = record.get(i);
				if (classIString == null)
					classIString = "-3"; // highcharts neberie null hodnoty. -3 predstavuje null hodnotu
				Integer classI = Integer.parseInt(classIString);
				String classJString = record.get(j);
				if (classJString == null)
					classJString = "-3"; // highcharts neberie null hodnoty. -3 predstavuje null hodnotu
				Integer classJ = Integer.parseInt(classJString);
				Pair<Integer, Integer> pair = new Pair<>(classI, classJ);
				if (histogram.containsKey(pair)) {
					histogram.put(pair, histogram.get(pair) + 1);
				} else {
					histogram.put(pair, 1);
				}
			}
		}
		for (Entry<Pair<Integer, Integer>, Integer> entry : histogram.entrySet()) {
			Pair<Integer,Integer> key = entry.getKey();
			TreeMap<String, Integer> jHistogram = a_gHistograms.get(j);
			Integer totalCountForClass = jHistogram.get(key.getSecond() + "");
			entry.setValue(entry.getValue() * 100 / totalCountForClass);
		}
		
		histogram.setFirstParameterNumber(i);
		histogram.setSecondParameterNumber(j);
		return histogram;
	}

	protected Collection<? extends IntraclassHistogram> createIntraclassHistograms(AllstateDataModel model, int property, TreeMap<Integer, TreeMap<String, Integer>> a_gHistograms, Integer... columns) {
		List<IntraclassHistogram> intraclassHistograms = new ArrayList<>();
		for (int i = 0; i < columns.length; i++) {
			intraclassHistograms.add(createIntraclassHistogram(model, a_gHistograms, property, columns[i]));
		}
		return intraclassHistograms;
	}

}
