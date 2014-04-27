package recsys.userBased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.UserNeighborhoodBuilder;
import recsys.evaluator.UserSimilarityBuilder;

public class UserBasedRecommenderBuilder implements RecommenderBuilder {

	private UserSimilarityBuilder userSimilarityBuilder;
	private UserNeighborhoodBuilder neighborhoodBuilder;
	
	UserNeighborhood neighborhood;
	UserSimilarity userSimilarity;
	
	public UserBasedRecommenderBuilder(UserSimilarityBuilder userSimilarityBuilder, UserNeighborhoodBuilder neighborhoodBuilder) {
		this.userSimilarityBuilder = userSimilarityBuilder;
		this.neighborhoodBuilder = neighborhoodBuilder;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
//		UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
//		UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, userSimilarity, dataModel);//0.994
//		UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, userSimilarity, dataModel);//1.007
//		UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, userSimilarity, dataModel);//1.046
//		UserNeighborhood neighborhood = new NearestNUserNeighborhood(20, userSimilarity, dataModel);//0.993
		 
//		 UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel); 
		
		userSimilarity = userSimilarityBuilder.build(dataModel);
		neighborhood = neighborhoodBuilder.build(userSimilarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
		return recommender;
	}

	@Override
	public String getName() {
		return "User based recommender builder" + " with user similarity: " + userSimilarity.getName() + " with neighborhood: " + neighborhood.getName();
	}

}
