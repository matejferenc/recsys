package recsys.recommender.movielens.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.recommender.movielens.model.movielens.UserModel;

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

}
