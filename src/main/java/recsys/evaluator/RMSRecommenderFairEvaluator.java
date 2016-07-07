package recsys.evaluator;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;

import recsys.evaluator.abstr.AbstractDifferenceRecommenderFairEvaluator;

/**
 * <p>
 * A {@link org.apache.mahout.cf.taste.eval.RecommenderEvaluator} which computes the "root mean squared" difference between predicted and actual ratings for users.
 * This is the square root of the average of this difference, squared.
 * </p>
 */
public class RMSRecommenderFairEvaluator extends AbstractDifferenceRecommenderFairEvaluator {

	private RunningAverage average;
	
	public RMSRecommenderFairEvaluator(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	protected void reset() {
		average = new FullRunningAverage();
	}

	@Override
	protected void processOneEstimate(float estimatedPreference, Preference realPref) {
		double diff = realPref.getValue() - estimatedPreference;
		average.addDatum(diff * diff);
	}

	@Override
	protected double computeFinalEvaluation() {
		return Math.sqrt(average.getAverage());
	}

	@Override
	public String toString() {
		return "RMSRecommenderFairEvaluator";
	}

}
