package allstate.evaluator;

import java.util.Map.Entry;
import java.util.TreeMap;

import allstate.model.AllstateDataModel;
import allstate.model.AllstateModelCreator;
import allstate.recommender.AllstateRecommender;
import allstate.recommender.FirstRecordRecommender;
import allstate.recommender.LastRecordRecommender;
import allstate.recommender.MostFrequentValueRecommender;

public class AllstateRecommenderEvaluator {
	
	private static AllstateDataModel model;
	

	public static void main(String[] args) throws Exception {
		
		AllstateRecommender recommender = new LastRecordRecommender();
//		AllstateRecommender recommender = new MostFrequentValueRecommender();
//		AllstateRecommender recommender = new FirstRecordRecommender();
		
		AllstateEvaluator evaluator = new AllstateEvaluator(recommender, model);
		
		TreeMap<Long, Integer> notGuessedCount = new TreeMap<>();
		double successRatio = evaluator.evaluate(0.3, notGuessedCount);
		System.out.println(successRatio);
	}
	
	public EvaluatorResults run() throws Exception {
		model = new AllstateModelCreator().createModel();
		EvaluatorResults results = new EvaluatorResults();
		results.lastRecordSuccessRatio = evaluateAndCreateHistogram(results.lastRecordHistogram, new LastRecordRecommender());
		results.mostFrequentSuccessRatio = evaluateAndCreateHistogram(results.mostFrequentHistogram, new MostFrequentValueRecommender());
		results.firstRecordSuccessRatio = evaluateAndCreateHistogram(results.firstRecordHistogram, new FirstRecordRecommender());
		return results;
	}

	private double evaluateAndCreateHistogram(TreeMap<Integer, Double> histogram, AllstateRecommender recommender) {
		AllstateEvaluator evaluator = new AllstateEvaluator(recommender, model);
		TreeMap<Long, Integer> notGuessedCounts = new TreeMap<>();
		double successRatio = evaluator.evaluate(0.3, notGuessedCounts);
		createErrorHistogram(histogram, notGuessedCounts);
		return successRatio;
	}

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
