package recsys.movielens.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.movielens.model.builder.UserModelBuilder;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.UserModel;
import recsys.movielens.recommender.MovieLensContentBasedRecommender;

public class MovieLensContentBasedRecommenderBuilder implements RecommenderBuilder {

	private final MovieLensEnrichedModel movieLensEnrichedModel;

	public MovieLensContentBasedRecommenderBuilder(MovieLensEnrichedModel movieLensEnrichedModel) {
		this.movieLensEnrichedModel = movieLensEnrichedModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, movieLensEnrichedModel);
		UserModel userModel = userModelBuilder.build();
		return new MovieLensContentBasedRecommender(dataModel, userModel, movieLensEnrichedModel);
	}

	@Override
	public String getName() {
		return "Movie Lens Content Based Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

	@Override
	public String getShortName() {
		return "mlcb";
	}

}
