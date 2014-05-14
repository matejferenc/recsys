package recsys.movieLensHetrecContentBased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.hetrecMovielens.ContentBasedMovieGenreRecommender;
import recsys.recommender.movielens.OneSetSimilarity;
import recsys.recommender.movielens.SetSimilarity;
import recsys.recommender.movielens.model.imdb.ImdbGenresDataModel;

public class HetrecContentBasedRecommenderBuilder implements RecommenderBuilder {

	private ImdbGenresDataModel genresModel;

	public HetrecContentBasedRecommenderBuilder(ImdbGenresDataModel genresModel) {
		this.genresModel = genresModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
//		SetSimilarity setSimilarity = new SimpleSetSimilarity();
//		SetSimilarity setSimilarity = new SquareRootSetSimilarity();
		SetSimilarity setSimilarity = new OneSetSimilarity();
//		Recommender recommender = new ContentBasedMovieGenreRecommender(dataModel, genresModel, setSimilarity);
//		return recommender;
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub
		
	}

}
