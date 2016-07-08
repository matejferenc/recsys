package recsys.evaluator;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.model.Preference;

import recsys.evaluator.abstr.AbstractDifferenceRecommenderCrossEvaluator;

/**
 * <p>
 * A {@link org.apache.mahout.cf.taste.eval.RecommenderEvaluator} which computes the "root mean squared"
 * difference between predicted and actual ratings for users. This is the square root of the average of this
 * difference, squared.
 * </p>
 */
public final class RMSRecommenderCrossEvaluator extends AbstractDifferenceRecommenderCrossEvaluator {
  
  private RunningAverage average;
  
  @Override
  protected void reset() {
    average = new FullRunningAverage();
  }
  
  @Override
  protected void processOneEstimate(Double estimatedPreference, Preference realPref) {
    Double diff = realPref.getValue() - estimatedPreference;
    average.addDatum(diff * diff);
  }
  
  @Override
  protected Double computeFinalEvaluation() {
    return (double) Math.sqrt(average.getAverage());
  }
  
  @Override
  public String toString() {
    return "RMSRecommenderCrossEvaluator";
  }
  
}
