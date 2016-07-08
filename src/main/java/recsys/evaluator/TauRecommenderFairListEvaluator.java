package recsys.evaluator;

import java.util.List;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.evaluator.abstr.AbstractDifferenceRecommenderFairListEvaluator;

public final class TauRecommenderFairListEvaluator extends AbstractDifferenceRecommenderFairListEvaluator {

	public TauRecommenderFairListEvaluator(DataModel dataModel) {
		super(dataModel);
	}

	private RunningAverage average;

	@Override
	protected void reset() {
		average = new FullRunningAverage();
	}

	@Override
	protected double computeFinalEvaluation() {
		return average.getAverage();
	}

	@Override
	public String toString() {
		return "TauRecommenderFairListEvaluator";
	}

	@Override
	protected void processOneEstimateList(List<Float> estimated, List<Float> real) {
		int concordant = 0;
		int discordant = 0;
		int n = estimated.size();
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				if (estimated.get(i) < estimated.get(j) && real.get(i) <= real.get(j)
						|| estimated.get(i) > estimated.get(j) && real.get(i) >= real.get(j)) {
					concordant++;
				} else {
					discordant++;
				}
			}
		}

		double tau = (concordant - discordant) / ((double)n * (n - 1) / 2);
		average.addDatum(tau);
	}

}