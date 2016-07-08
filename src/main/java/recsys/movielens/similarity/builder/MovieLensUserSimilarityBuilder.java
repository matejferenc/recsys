package recsys.movielens.similarity.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.movielens.model.builder.UserModelBuilder;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.UserModel;
import recsys.movielens.similarity.MovielensUserSimilarity;
import recsys.movielens.similarity.MovielensUserSimilarityFunction;
import recsys.similarity.builder.UserSimilarityBuilder;

public class MovieLensUserSimilarityBuilder implements UserSimilarityBuilder {
	
	private final MovieLensEnrichedModel movieLensEnrichedModel;
	
	private final MovielensUserSimilarityFunction movielensUserSimilarityFunction;

	public MovieLensUserSimilarityBuilder(MovieLensEnrichedModel movieLensEnrichedModel, MovielensUserSimilarityFunction movielensUserSimilarityFunction) {
		this.movieLensEnrichedModel = movieLensEnrichedModel;
		this.movielensUserSimilarityFunction = movielensUserSimilarityFunction;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, movieLensEnrichedModel);
		UserModel userModel = userModelBuilder.build();
		return new CachingUserSimilarity(new MovielensUserSimilarity(userModel, movielensUserSimilarityFunction), Integer.MAX_VALUE);
//		return new MovielensUserSimilarity(userModel, movielensUserSimilarityFunction);
	}

}
