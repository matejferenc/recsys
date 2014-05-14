package allstate.evaluator;

import java.util.Map.Entry;
import java.util.TreeMap;

import allstate.model.AllstateDataModel;
import allstate.model.AllstateModelCreator;
import allstate.recommender.AllstateRecommender;
import allstate.recommender.FirstRecordRecommender;
import allstate.recommender.LastRecordRecommender;
import allstate.recommender.MostFrequentValueRecommender;
import allstate.recommender.RandomForestRecommender;

public class AllstateRecommenderEvaluator {
	
	
	public EvaluatorResults run() throws Exception {
		EvaluatorResults results = new EvaluatorResults();
		results.randomForestSuccessRatio = evaluateAndCreateHistogram(results.randomForestHistogram, results.randomForestParametersNotGuessedHistogram, new RandomForestRecommender());
		results.lastRecordSuccessRatio = evaluateAndCreateHistogram(results.lastRecordHistogram, results.lastRecordParametersNotGuessedHistogram, new LastRecordRecommender());
		results.mostFrequentSuccessRatio = evaluateAndCreateHistogram(results.mostFrequentHistogram, results.mostFrequentParametersNotGuessedHistogram, new MostFrequentValueRecommender());
		results.firstRecordSuccessRatio = evaluateAndCreateHistogram(results.firstRecordHistogram, results.firstRecordParametersNotGuessedHistogram, new FirstRecordRecommender());
		return results;
	}

	private double evaluateAndCreateHistogram(TreeMap<Integer, Double> notGuessedCountHistogram, TreeMap<Integer, Double> parametersNotGuessedCountHistogram, AllstateRecommender recommender) throws Exception {
		AllstateDataModel model = new AllstateModelCreator().createModel();
		AllstateEvaluator evaluator = new AllstateEvaluator(recommender, model);
		TreeMap<Long, Integer> notGuessedCounts = new TreeMap<>();
		double successRatio = evaluator.evaluate(0.3, notGuessedCounts, parametersNotGuessedCountHistogram);
		createErrorHistogram(notGuessedCountHistogram, notGuessedCounts);
		int testSampleSize = notGuessedCounts.size();
		changeHistogramToPercent(parametersNotGuessedCountHistogram, testSampleSize);
		return successRatio;
	}

	private void changeHistogramToPercent(TreeMap<Integer, Double> parametersNotGuessedCountHistogram, int testSampleSize) {
		for (Entry<Integer, Double> entry : parametersNotGuessedCountHistogram.entrySet()) {
			entry.setValue(entry.getValue() * 100 / testSampleSize);
		}
	}

	/**
	 * prepocitam absolutne pocty neuhadnutych parametrov na percenta
	 * 
	 * @param histogram percentualny (notGuessedCount:% of users)
	 * @param notGuessedCounts absolutny histogram (userID:notGuessedCount)
	 */
	private void createErrorHistogram(TreeMap<Integer, Double> histogram, TreeMap<Long, Integer> notGuessedCounts) {
		for (Entry<Long, Integer> entry : notGuessedCounts.entrySet()) {
			Integer notGuessed = entry.getValue();
			Double actualCount = histogram.get(notGuessed);
			if (actualCount != null) {
				histogram.put(notGuessed, actualCount + 1);
			} else {
				histogram.put(notGuessed, 1.0);
			}
		}
		for (Entry<Integer, Double> entry : histogram.entrySet()) {
			double newValue = entry.getValue() * 100 / (double) notGuessedCounts.size();
			entry.setValue(newValue);
		}
	}

}
