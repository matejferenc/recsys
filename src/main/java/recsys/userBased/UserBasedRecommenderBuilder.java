package recsys.userBased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBasedRecommenderBuilder implements RecommenderBuilder {

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
//		UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
		UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(dataModel);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, userSimilarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
		return recommender;
//		Recommender cachingRecommender = new CachingRecommender(recommender);
//		return cachingRecommender;
	}

}
