package recsys.evaluator.abstr;

import static org.junit.Assert.assertEquals;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.junit.Test;

import recsys.evaluator.DatasetSplitterTest;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.notebooks.dataset.NotebooksDataset;
import recsys.recommender.builder.UserBasedRecommenderBuilder;
import recsys.similarity.builder.EuclideanDistanceUserSimilarityBuilder;

public class OldAbstractRecommenderFairEvaluatorTest {

	private double trainingPercentage = 0.6666;
	private double evaluationPercentage = 0.25;

	@Test
	public void test() throws Exception {
		OldAbstractRecommenderFairEvaluator evaluator = new OldAbstractRecommenderFairEvaluator() {
			
			@Override
			protected void reset() {}
			
			@Override
			protected double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			protected double computeFinalEvaluation() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			protected void logPrefsStatistics(FastByIDMap<PreferenceArray> trainingPrefs, FastByIDMap<PreferenceArray> testPrefs) {
				assertEquals(27, trainingPrefs.size());
				assertEquals(27, testPrefs.size());
				DatasetSplitterTest.assertEmptyIntersection(trainingPrefs, testPrefs);
			}
		};
		
		EuclideanDistanceUserSimilarityBuilder euclideanDistanceUserSimilarityBuilder = new EuclideanDistanceUserSimilarityBuilder();
		UserBasedRecommenderBuilder recommenderBuilder = new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(2));
		DataModel dataModel = new NotebooksDataset().build();
		evaluator.evaluate(recommenderBuilder, dataModel, trainingPercentage, evaluationPercentage);
	}
}
