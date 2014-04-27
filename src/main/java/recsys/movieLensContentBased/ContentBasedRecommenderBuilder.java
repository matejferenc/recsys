package recsys.movieLensContentBased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.movielens.ContentBasedMovieGenreRecommender;
import org.apache.mahout.cf.taste.impl.recommender.movielens.GenresDataModel;
import org.apache.mahout.cf.taste.impl.recommender.movielens.OneSetSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.movielens.SetSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.movielens.SimpleSetSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.movielens.SquareRootSetSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

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

}
