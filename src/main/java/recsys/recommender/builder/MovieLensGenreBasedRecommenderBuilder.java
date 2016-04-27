package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.movielens.model.GenresDataModel;
import recsys.movielens.recommender.ContentBasedMovieGenreRecommender;
import recsys.movielens.similarity.OneSetSimilarity;
import recsys.movielens.similarity.SetSimilarity;

public class MovieLensGenreBasedRecommenderBuilder implements RecommenderBuilder {

	private GenresDataModel genresModel;

	public MovieLensGenreBasedRecommenderBuilder(GenresDataModel genresModel) {
		this.genresModel = genresModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
//		SetSimilarity setSimilarity = new SimpleSetSimilarity();
//		SetSimilarity setSimilarity = new SquareRootSetSimilarity();
		SetSimilarity setSimilarity = new OneSetSimilarity();
		Recommender recommender = new ContentBasedMovieGenreRecommender(dataModel, genresModel, setSimilarity);
		return recommender;
	}

	@Override
	public String getName() {
		return "MovieLens Genre based recommender builder";
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getShortName() {
		return "MLGB";
	}

}
