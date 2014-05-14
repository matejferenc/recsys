package recsys.movieLensHetrecContentBased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.hetrecMovielens.ContentBasedMovieGenreRecommender;
import org.apache.mahout.cf.taste.impl.recommender.hetrecMovielens.GenresDataModel;
import org.apache.mahout.cf.taste.impl.recommender.movielens.OneSetSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.movielens.SetSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

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
		Recommender recommender = new ContentBasedMovieGenreRecommender(dataModel, genresModel, setSimilarity);
		return recommender;
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
