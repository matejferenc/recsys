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

	
	String neighborhoodName;
	String userSimilarityName;

	public UserBasedRecommenderBuilder(UserSimilarityBuilder userSimilarityBuilder, UserNeighborhoodBuilder neighborhoodBuilder) {
		this.userSimilarityBuilder = userSimilarityBuilder;
		this.neighborhoodBuilder = neighborhoodBuilder;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {

		// UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel);

		UserSimilarity userSimilarity = userSimilarityBuilder.build(dataModel);
		UserNeighborhood neighborhood = neighborhoodBuilder.build(userSimilarity, dataModel);
		
		neighborhoodName = neighborhood.getName();
		userSimilarityName = userSimilarity.getName();

		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
		return recommender;
	}

	@Override
	public String getName() {
		return "User based recommender builder" + " with user similarity: " + userSimilarityName + " with neighborhood: " + neighborhoodName;
	}

	@Override
	public void freeReferences() {
	}

}
