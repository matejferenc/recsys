package recsys.recommender.movielens.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.recommender.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.recommender.movielens.model.movielens.UserModel;

public class MovieLensUserSimilarityBuilder implements UserSimilarityBuilder {
	
	private MovieLensEnrichedModel movieLensEnrichedModel;

	public MovieLensUserSimilarityBuilder(MovieLensEnrichedModel movieLensEnrichedModel) {
		this.movieLensEnrichedModel = movieLensEnrichedModel;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, movieLensEnrichedModel);
		UserModel userModel = userModelBuilder.build();
		return new MovieLensUserSimilarity(userModel);
	}

}
