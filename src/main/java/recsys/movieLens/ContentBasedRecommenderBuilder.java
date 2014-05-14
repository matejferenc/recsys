package recsys.movieLens;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.movielens.ContentBasedMovieGenreRecommender;
import recsys.recommender.movielens.GenresDataModel;
import recsys.recommender.movielens.OneSetSimilarity;
import recsys.recommender.movielens.SetSimilarity;

public class ContentBasedRecommenderBuilder implements RecommenderBuilder {

	private GenresDataModel genresModel;

	public ContentBasedRecommenderBuilder(GenresDataModel genresModel) {
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
		return "Content based recommender builder";
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub
		
	}

}
