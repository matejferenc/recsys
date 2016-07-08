package recsys.evaluator;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;

import recsys.evaluator.abstr.AbstractDifferenceRecommenderFairEvaluator;

public final class WeightedRMSRecommenderFairEvaluator extends AbstractDifferenceRecommenderFairEvaluator {

	private RunningAverage average;
	private Integer numberOfDistinctPreferenceValues;
	private Integer preferenceValueCorrection;

	public WeightedRMSRecommenderFairEvaluator(DataModel dataModel, Integer numberOfDistinctPreferenceValues, Integer preferenceValueCorrection) {
		super(dataModel);
		this.numberOfDistinctPreferenceValues = numberOfDistinctPreferenceValues;
		this.preferenceValueCorrection = preferenceValueCorrection;
	}

	@Override
	protected void reset() {
		average = new FullRunningAverage();
	}

	@Override
	protected void processOneEstimate(Double estimatedPreference, Preference realPref) {
		Double realPrefValue = realPref.getValue();
		Double weight = (realPrefValue + preferenceValueCorrection) / numberOfDistinctPreferenceValues;
		Double diff = weight * (realPrefValue - estimatedPreference);
		average.addDatum(diff * diff);
	}

	@Override
	protected double computeFinalEvaluation() {
		return Math.sqrt(average.getAverage());
	}

	@Override
	public String toString() {
		return "WeightedRMSRecommenderFairEvaluator";
	}

}
