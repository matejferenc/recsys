package allstate.evaluator;

import java.util.TreeMap;

public class EvaluatorResults {

	TreeMap<Integer, Double> lastRecordHistogram = new TreeMap<>();
	double lastRecordSuccessRatio;
	public TreeMap<Integer, Double> lastRecordParametersNotGuessedHistogram = createEmptyNotGuessedHistogram();

	TreeMap<Integer, Double> mostFrequentHistogram = new TreeMap<>();
	double mostFrequentSuccessRatio;
	public TreeMap<Integer, Double> mostFrequentParametersNotGuessedHistogram = createEmptyNotGuessedHistogram();

	TreeMap<Integer, Double> firstRecordHistogram = new TreeMap<>();
	double firstRecordSuccessRatio;
	public TreeMap<Integer, Double> firstRecordParametersNotGuessedHistogram = createEmptyNotGuessedHistogram();

	TreeMap<Integer, Double> randomForestHistogram = new TreeMap<>();
	double randomForestSuccessRatio;
	public TreeMap<Integer, Double> randomForestParametersNotGuessedHistogram = createEmptyNotGuessedHistogram();


	private TreeMap<Integer, Double> createEmptyNotGuessedHistogram() {
		TreeMap<Integer, Double> histogram = new TreeMap<>();
		for (int i = 16; i <= 22; i++) {
			histogram.put(i, 0.0);
		}
		return histogram;
	}

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

	public TreeMap<Integer, Double> getLastRecordParametersNotGuessedHistogram() {
		return lastRecordParametersNotGuessedHistogram;
	}

	public TreeMap<Integer, Double> getMostFrequentParametersNotGuessedHistogram() {
		return mostFrequentParametersNotGuessedHistogram;
	}

	public TreeMap<Integer, Double> getFirstRecordParametersNotGuessedHistogram() {
		return firstRecordParametersNotGuessedHistogram;
	}

	public TreeMap<Integer, Double> getRandomForestHistogram() {
		return randomForestHistogram;
	}

	public double getRandomForestSuccessRatio() {
		return randomForestSuccessRatio;
	}

	public TreeMap<Integer, Double> getRandomForestParametersNotGuessedHistogram() {
		return randomForestParametersNotGuessedHistogram;
	}
}
