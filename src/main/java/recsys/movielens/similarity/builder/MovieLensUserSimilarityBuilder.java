package recsys.movielens.similarity.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.movielens.model.builder.UserModelBuilder;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.UserModel;
import recsys.movielens.similarity.MovieLensUserSimilarity;
import recsys.similarity.builder.UserSimilarityBuilder;

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
