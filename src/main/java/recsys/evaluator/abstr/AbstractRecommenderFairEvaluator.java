package recsys.evaluator.abstr;

import java.util.concurrent.atomic.AtomicInteger;

import recsys.evaluator.RecommenderFairEvaluator;

public abstract class AbstractRecommenderFairEvaluator implements RecommenderFairEvaluator {

	protected AtomicInteger noEstimateCounter = new AtomicInteger();
	protected AtomicInteger estimateCounter = new AtomicInteger();

	public AtomicInteger getNoEstimateCounter() {
		return noEstimateCounter;
	}

	public AtomicInteger getEstimateCounter() {
		return estimateCounter;
	}
}
