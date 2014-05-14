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

		// UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel);

		userSimilarity = userSimilarityBuilder.build(dataModel);
		neighborhood = neighborhoodBuilder.build(userSimilarity, dataModel);

		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
		return recommender;
	}

	@Override
	public String getName() {
		return "User based recommender builder" + " with user similarity: " + userSimilarity.getName() + " with neighborhood: " + neighborhood.getName();
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub

		neighborhood = null;
		userSimilarity = null;
	}

}
