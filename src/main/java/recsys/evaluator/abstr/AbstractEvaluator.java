package recsys.evaluator.abstr;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;

import recsys.evaluator.DatasetSplitter;
import recsys.evaluator.RMSRecommenderFairEvaluator;
import recsys.evaluator.TauRecommenderFairListEvaluator;
import recsys.evaluator.WeightedRMSRecommenderFairEvaluator;

public abstract class AbstractEvaluator {

	public static final double testingPercentage = 0.25;
	public static final double evaluationPercentage = 0.3333;
	private static final NumberFormat formatter = new DecimalFormat("#0.000");

	public abstract void evaluate() throws Exception;

	protected Double average(List<Double> evaluate) {
		Double total = 0d;
		for (Double double1 : evaluate) {
			total += double1;
		}
		return total / evaluate.size();
	}

	protected Double deviation(List<Double> evaluate) {
		Double average = average(evaluate);
		Double total = 0d;
		for (Double d : evaluate) {
			double a = d - average;
			total += a * a;
		}
		return Math.sqrt(total / evaluate.size());
	}

	protected String listOfDoublesToString(List<Double> list) {
		StringBuilder sb = new StringBuilder();
		NumberFormat formatter = new DecimalFormat("#0.000");
		for (Double double1 : list) {
			sb.append(formatter.format(double1) + "\t");
		}
		return sb.toString();
	}
	
	protected String listOfIntsToString(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
		for (Integer id : list) {
			sb.append(id + "\t");
		}
		return sb.toString();
	}

	protected double evaluateRecommenders(DataModel dataModel, List<RecommenderBuilder> builders, List<String> argsList) throws MissingArgumentException, TasteException {
		StringBuilder sb = new StringBuilder();
		IncludeMetrics metrics = IncludeMetrics.fromList(argsList);
		double totalScore = 0;

		sb.append("minimum possible preference: " + dataModel.getMinPreference());
		sb.append("\n");
		sb.append("maximum possible preference: " + dataModel.getMaxPreference());
		sb.append("\n");

		sb.append("Starting cross validation using " + Math.round(1 / (testingPercentage * evaluationPercentage)) + " groups");
		sb.append("\n");
		sb.append("Testing percentage: " + testingPercentage);
		sb.append("\n");
		sb.append("Evaluation percentage: " + evaluationPercentage);
		sb.append("\n");

		sb.append("Builder\t");
		sb.append("Estimated cases\t");
		sb.append("Non-Estimated cases\t");
		sb.append("Time[s]\t");
		sb.append("AverageScore\t");
		sb.append("Deviation\t\n");

		for (RecommenderBuilder builder : builders) {
			Date start = new Date();

			int estimated = 0;
			int notEstimated = 0;
			List<Double> evaluated = new ArrayList<>();

			AbstractRecommenderFairEvaluator evaluator = createEvaluator(dataModel, metrics);
			
			DatasetSplitter splitter = new DatasetSplitter(dataModel, testingPercentage, evaluationPercentage);

			while (splitter.hasNext()) {
				Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> pair = splitter.next();
				FastByIDMap<PreferenceArray> trainingDataset = pair.getFirst();
				FastByIDMap<PreferenceArray> testDataset = pair.getSecond();
				double score = evaluator.evaluate(builder, trainingDataset, testDataset);
				evaluated.add(score);
				totalScore += score;
			}

			estimated = evaluator.getEstimateCounter().intValue();
			notEstimated = evaluator.getNoEstimateCounter().intValue();
			Date end = new Date();

			sb.append(builder.getShortName());
			sb.append("\t");
			sb.append(estimated);
			sb.append("\t");
			sb.append(notEstimated);
			sb.append("\t");
			sb.append((end.getTime() - start.getTime()) / 1000);
			sb.append("\t");
			sb.append(formatter.format(average(evaluated)));
			sb.append("\t");
			sb.append(formatter.format(deviation(evaluated)));
			sb.append("\t\t");
			sb.append(listOfDoublesToString(evaluated));
			sb.append("\n");
			sb.append("100 most difficult users");
			sb.append("\t");
			sb.append(listOfIntsToString(getMostDifficultUsers(evaluator, 100)));
			sb.append("\n");
			sb.append("100 most difficult items");
			sb.append("\t");
			sb.append(listOfIntsToString(getMostDifficultItems(evaluator, 100)));
			sb.append("\n");
			sb.append("100 least difficult users");
			sb.append("\t");
			sb.append(listOfIntsToString(getLeastDifficultUsers(evaluator, 100)));
			sb.append("\n");
			sb.append("100 least difficult items");
			sb.append("\t");
			sb.append(listOfIntsToString(getLeastDifficultItems(evaluator, 100)));
			sb.append("\n");

			builder.freeReferences();
		}

		System.out.println(sb.toString());
		return totalScore / builders.size();
	}

	private List<Integer> getMostDifficultUsers(AbstractRecommenderFairEvaluator evaluator, int limit) {
		Map<Integer, Double> averageAbsoluteErrors = new HashMap<Integer, Double>();
		for (Integer userID : evaluator.estimatesForUsers.keySet()) {
			List<Double> estimateAbsoluteErrors = evaluator.estimatesForUsers.get(userID);
			Double averageAbsoluteError = average(estimateAbsoluteErrors);
			averageAbsoluteErrors.put(userID, averageAbsoluteError);
		}
		List<Entry<Integer,Double>> list = sortByLargest(averageAbsoluteErrors, limit);
		return limit(limit, list);
	}
	
	private List<Integer> getMostDifficultItems(AbstractRecommenderFairEvaluator evaluator, int limit) {
		Map<Integer, Double> averageAbsoluteErrors = new HashMap<Integer, Double>();
		for (Integer itemID : evaluator.estimatesForItems.keySet()) {
			List<Double> estimateAbsoluteErrors = evaluator.estimatesForItems.get(itemID);
			Double averageAbsoluteError = average(estimateAbsoluteErrors);
			averageAbsoluteErrors.put(itemID, averageAbsoluteError);
		}
		List<Entry<Integer,Double>> list = sortByLargest(averageAbsoluteErrors, limit);
		return limit(limit, list);
	}
	
	private List<Integer> getLeastDifficultUsers(AbstractRecommenderFairEvaluator evaluator, int limit) {
		Map<Integer, Double> averageAbsoluteErrors = new HashMap<Integer, Double>();
		for (Integer userID : evaluator.estimatesForUsers.keySet()) {
			List<Double> estimateAbsoluteErrors = evaluator.estimatesForUsers.get(userID);
			Double averageAbsoluteError = average(estimateAbsoluteErrors);
			averageAbsoluteErrors.put(userID, averageAbsoluteError);
		}
		List<Entry<Integer,Double>> list = sortBySmallest(averageAbsoluteErrors, limit);
		return limit(limit, list);
	}
	
	private List<Integer> getLeastDifficultItems(AbstractRecommenderFairEvaluator evaluator, int limit) {
		Map<Integer, Double> averageAbsoluteErrors = new HashMap<Integer, Double>();
		for (Integer itemID : evaluator.estimatesForItems.keySet()) {
			List<Double> estimateAbsoluteErrors = evaluator.estimatesForItems.get(itemID);
			Double averageAbsoluteError = average(estimateAbsoluteErrors);
			averageAbsoluteErrors.put(itemID, averageAbsoluteError);
		}
		List<Entry<Integer,Double>> list = sortBySmallest(averageAbsoluteErrors, limit);
		return limit(limit, list);
	}

	private List<Integer> limit(int limit, List<Entry<Integer, Double>> list) {
		List<Integer> result = new ArrayList<Integer>();
		for (Entry<Integer, Double> entry : list) {
			result.add(entry.getKey());
			if (result.size() >= limit) {
				break;
			}
		}
		return result;
	}

	private List<Entry<Integer,Double>> sortByLargest(Map<Integer, Double> averageAbsoluteErrors, int limit) {
		// Convert Map to List
		List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(averageAbsoluteErrors.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		return list;
	}
	
	private List<Entry<Integer,Double>> sortBySmallest(Map<Integer, Double> averageAbsoluteErrors, int limit) {
		// Convert Map to List
		List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(averageAbsoluteErrors.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		return list;
	}

	private AbstractRecommenderFairEvaluator createEvaluator(DataModel dataModel, IncludeMetrics evaluator) throws MissingArgumentException {
		AbstractRecommenderFairEvaluator e;
		if (evaluator == IncludeMetrics.WEIGHTED_RMSE) {
			e = new WeightedRMSRecommenderFairEvaluator(dataModel, 5, 1);
		} else if (evaluator == IncludeMetrics.RMSE) {
			e = new RMSRecommenderFairEvaluator(dataModel);
		} else if (evaluator == IncludeMetrics.TAU) {
			e = new TauRecommenderFairListEvaluator();
		} else {
			throw new MissingArgumentException("Metrics missing. Choose one from [" + IncludeMetrics.getAllNames() + "].");
		}
		return e;
	}

}
