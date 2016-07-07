package recsys.evaluator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;

/**
 * <p>
 * Implementations of this interface evaluate the quality of a {@link org.apache.mahout.cf.taste.recommender.Recommender}'s recommendations.
 * </p>
 */
public interface RecommenderFairEvaluator {

	double evaluate(RecommenderBuilder recommenderBuilder, FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs) throws TasteException;
}
