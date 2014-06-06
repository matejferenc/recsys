package recsys.evaluator;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.Preference;

public final class WeightedRMSRecommenderEvaluator extends AbstractDifferenceRecommenderEvaluator {

	private RunningAverage average;
	private float numberOfDistinctPreferenceValues;
	private float preferenceValueCorrection;

	public WeightedRMSRecommenderEvaluator(float numberOfDistinctPreferenceValues, float preferenceValueCorrection) {
		this.numberOfDistinctPreferenceValues = numberOfDistinctPreferenceValues;
		this.preferenceValueCorrection = preferenceValueCorrection;
	}

	@Override
	protected void reset() {
		average = new FullRunningAverage();
	}

	@Override
	protected void processOneEstimate(float estimatedPreference, Preference realPref) {
		float realPrefValue = realPref.getValue();
		float weight = (realPrefValue + preferenceValueCorrection) / numberOfDistinctPreferenceValues;
		double diff = weight * (realPrefValue - estimatedPreference);
		average.addDatum(diff * diff);
	}

	@Override
	protected double computeFinalEvaluation() {
		return Math.sqrt(average.getAverage());
	}

	@Override
	public String toString() {
		return "RMSRecommenderEvaluator";
	}

}
