package recsys.evaluator;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.model.Preference;

import recsys.evaluator.abstr.AbstractDifferenceRecommenderCrossEvaluator;

public final class WeightedRMSRecommenderCrossEvaluator extends AbstractDifferenceRecommenderCrossEvaluator {

	private RunningAverage average;
	private Double numberOfDistinctPreferenceValues;
	private Double preferenceValueCorrection;

	public WeightedRMSRecommenderCrossEvaluator(Double numberOfDistinctPreferenceValues, Double preferenceValueCorrection) {
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
	protected Double computeFinalEvaluation() {
		return (double) Math.sqrt(average.getAverage());
	}

	@Override
	public String toString() {
		return "RMSRecommenderEvaluator";
	}

}
