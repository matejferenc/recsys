package recsys.movielens.similarity.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.similarity.MovieLensItemSimilarity;
import recsys.similarity.builder.ItemSimilarityBuilder;

public class MovieLensItemSimilarityBuilder implements ItemSimilarityBuilder {

	private MovieLensEnrichedModel movieLensEnrichedModel;

	public MovieLensItemSimilarityBuilder(MovieLensEnrichedModel movieLensEnrichedModel) {
		this.movieLensEnrichedModel = movieLensEnrichedModel;
	}
	
	@Override
	public ItemSimilarity build(DataModel dataModel) throws TasteException {
		return new MovieLensItemSimilarity(movieLensEnrichedModel);
	}

}
