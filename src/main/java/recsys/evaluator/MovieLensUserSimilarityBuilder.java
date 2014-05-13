package recsys.evaluator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.recommender.movielens.model.movielens.MovieLensUserSimilarity;
import recsys.recommender.movielens.model.movielens.UserModel;
import recsys.recommender.movielens.model.movielens.UserModelBuilder;
import recsys.recommender.movielens.model.shared.MovieLensEnrichedModel;

public class MovieLensUserSimilarityBuilder implements UserSimilarityBuilder {
	
	private MovieLensEnrichedModel movieLensMovieModel;

	public MovieLensUserSimilarityBuilder(MovieLensEnrichedModel movieLensEnrichedModel) {
		this.movieLensMovieModel = movieLensEnrichedModel;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, movieLensMovieModel);
		UserModel userModel = userModelBuilder.build();
		return new MovieLensUserSimilarity(userModel);
	}

}
