package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.builder.UserNeighborhoodBuilder;
import recsys.similarity.builder.UserSimilarityBuilder;

public class UserBasedRecommenderBuilder implements RecommenderBuilder {

	private UserSimilarityBuilder userSimilarityBuilder;
	private UserNeighborhoodBuilder neighborhoodBuilder;

	
	private String neighborhoodName;
	private String userSimilarityName;
	private String userSimilarityShortName;
	private String neighborhoodShortName;

	public UserBasedRecommenderBuilder(UserSimilarityBuilder userSimilarityBuilder, UserNeighborhoodBuilder neighborhoodBuilder) {
		this.userSimilarityBuilder = userSimilarityBuilder;
		this.neighborhoodBuilder = neighborhoodBuilder;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserSimilarity userSimilarity = userSimilarityBuilder.build(dataModel);
		UserNeighborhood neighborhood = neighborhoodBuilder.build(userSimilarity, dataModel);
		
		neighborhoodName = neighborhood.getName();
		neighborhoodShortName = neighborhood.getShortName();
		userSimilarityName = userSimilarity.getName();
		userSimilarityShortName = userSimilarity.getShortName();

		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
		return recommender;
	}

	@Override
	public String getName() {
		return "User based recommender builder" + " with user similarity: " + userSimilarityName + " with neighborhood: " + neighborhoodName;
	}
	
	@Override
	public String getShortName() {
		return "UB" + userSimilarityShortName + neighborhoodShortName;
	}

	@Override
	public void freeReferences() {
	}

}
