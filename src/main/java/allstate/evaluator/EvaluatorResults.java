package allstate.evaluator;

import java.util.TreeMap;

public class EvaluatorResults {

	TreeMap<Integer, Double> lastRecordHistogram = new TreeMap<>();
	double lastRecordSuccessRatio;

	TreeMap<Integer, Double> mostFrequentHistogram = new TreeMap<>();
	double mostFrequentSuccessRatio;

	TreeMap<Integer, Double> firstRecordHistogram = new TreeMap<>();
	double firstRecordSuccessRatio;

	public TreeMap<Integer, Double> getLastRecordHistogram() {
		return lastRecordHistogram;
	}

	public double getLastRecordSuccessRatio() {
		return lastRecordSuccessRatio;
	}

	public TreeMap<Integer, Double> getMostFrequentHistogram() {
		return mostFrequentHistogram;
	}

	public double getMostFrequentSuccessRatio() {
		return mostFrequentSuccessRatio;
	}

	public TreeMap<Integer, Double> getFirstRecordHistogram() {
		return firstRecordHistogram;
	}

	public double getFirstRecordSuccessRatio() {
		return firstRecordSuccessRatio;
	}
}
