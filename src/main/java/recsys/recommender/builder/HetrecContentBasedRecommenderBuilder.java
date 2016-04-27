package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.movielens.model.imdb.ImdbGenresDataModel;
import recsys.movielens.similarity.OneSetSimilarity;
import recsys.movielens.similarity.SetSimilarity;
import recsys.recommender.hetrecMovielens.ContentBasedMovieGenreRecommender;

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
		return "Hetrec Content Based";
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getShortName() {
		return "HetCB";
	}

}
