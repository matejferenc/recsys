package recsys.evaluator.abstr;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.evaluator.RMSRecommenderFairEvaluator;
import recsys.evaluator.TauRecommenderFairListEvaluator;
import recsys.evaluator.WeightedRMSRecommenderFairEvaluator;

public abstract class AbstractEvaluator {
	
	public static final double trainingPercentage = 0.7;
	public static final double evaluationPercentage = 0.3;
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

	protected String listToString(List<Double> evaluate) {
		String s = "";
		NumberFormat formatter = new DecimalFormat("#0.000");
		for (Double double1 : evaluate) {
			s += formatter.format(double1) + "\t";
		}
		return s;
	}
	
	protected void evaluateRecommenders(DataModel dataModel, List<RecommenderBuilder> builders, List<String> argsList) throws MissingArgumentException, TasteException {
		StringBuilder sb = new StringBuilder();
		IncludeEvaluator evaluator = IncludeEvaluator.fromList(argsList);
		int repeats = Repeats.fromList(argsList);

		sb.append("minimum possible preference: " + dataModel.getMinPreference());
		sb.append("\n");
		sb.append("maximum possible preference: " + dataModel.getMaxPreference());
		sb.append("\n");

		sb.append("Training percentage: " + trainingPercentage);
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

			int totalEstimated = 0;
			int totalNotEstimated = 0;
			List<Double> evaluated = new ArrayList<>();

			for (int i = 0; i < repeats; i++) {
				AbstractRecommenderFairEvaluator e = createEvaluator(evaluator);
				
				double evaluate = e.evaluate(builder, dataModel, trainingPercentage, evaluationPercentage);
				evaluated.add(evaluate);
				
				totalEstimated += e.getEstimateCounter().intValue();
				totalNotEstimated += e.getNoEstimateCounter().intValue();
			}
			Date end = new Date();

			sb.append(builder.getShortName());
			sb.append("\t");
			sb.append(totalEstimated);
			sb.append("\t");
			sb.append(totalNotEstimated);
			sb.append("\t");
			sb.append((end.getTime() - start.getTime()) / 1000);
			sb.append("\t");
			sb.append(formatter.format(average(evaluated)));
			sb.append("\t");
			sb.append(formatter.format(deviation(evaluated)));
			sb.append("\t\t");
			sb.append(listToString(evaluated));
			sb.append("\n");
			
			builder.freeReferences();
		}

		System.out.println(sb.toString());
	}

	private AbstractRecommenderFairEvaluator createEvaluator(IncludeEvaluator evaluator) throws MissingArgumentException {
		AbstractRecommenderFairEvaluator e;
		if (evaluator == IncludeEvaluator.WEIGHTED_RMSE) {
			e = new WeightedRMSRecommenderFairEvaluator(5, 1);
		} else if (evaluator == IncludeEvaluator.RMSE) {
			e = new RMSRecommenderFairEvaluator();
		} else if (evaluator == IncludeEvaluator.TAU) {
			e = new TauRecommenderFairListEvaluator();
		} else {
			throw new MissingArgumentException("Metrics missing. Choose one from [" + IncludeEvaluator.getAllNames() + "].");
		}
		return e;
	}

}
