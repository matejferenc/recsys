package recsys.movielens.recommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import recsys.movielens.model.GenresDataModel;
import recsys.movielens.similarity.SetSimilarity;

public class ContentBasedMovieGenreRecommender extends AbstractRecommender {

	private GenresDataModel genresModel;
	private SetSimilarity setSimilarity;

	public ContentBasedMovieGenreRecommender(DataModel dataModel, GenresDataModel genresModel, SetSimilarity setSimilarity) {
		super(dataModel);
		this.genresModel = genresModel;
		this.setSimilarity = setSimilarity;
	}

	@Override
	public List<RecommendedItem> recommend(int userID, int howMany, IDRescorer rescorer) throws TasteException {
		throw new NotImplementedException("not implemented yet - recommend(userID, howMany, rescorer)");
	}

	@Override
	public float estimatePreference(int userID, int itemID) throws TasteException {
		Set<String> itemGenres = genresModel.getGenres(itemID);
		DataModel model = getDataModel();
		FastIDSet itemIDsFromUser = model.getItemIDsFromUser(userID);
		List<Integer> itemsWithAtLeastOneSameGenre = new ArrayList<Integer>();
		List<Float> similarities = getItemsWithAtLeastOneSameGenre(itemIDsFromUser, itemGenres, itemsWithAtLeastOneSameGenre);
		float averageRating = calculateAverageRating(itemsWithAtLeastOneSameGenre, userID, similarities);
		return averageRating;
	}

	private float calculateAverageRating(List<Integer> itemsWithAtLeastOneSameGenre, int userID, List<Float> similarities) {
		Float sumOfRatings = 0f;
		Float sumOfSimilarities = 0f;
		int i = 0;
		for (int itemID : itemsWithAtLeastOneSameGenre) {
			try {
				float similarity = similarities.get(i);
				Float preferenceValue = getDataModel().getPreferenceValue(userID, itemID);
				sumOfRatings += preferenceValue * similarity;
				sumOfSimilarities += similarity;
			} catch (TasteException e) {
				e.printStackTrace();
				throw new IllegalStateException("Could not find preference for user: " + userID + " and item: " + itemID);
			}
			i++;
		}
		return sumOfRatings / sumOfSimilarities;
	}

	private List<Float> getItemsWithAtLeastOneSameGenre(FastIDSet itemIDsFromUser, Set<String> itemGenres, List<Integer> itemsWithSameGenre) throws TasteException {
		List<Float> itemsSimilarity = new ArrayList<Float>();
		for (int itemIDFromUser : itemIDsFromUser) {
			Set<String> genres = genresModel.getGenres(itemIDFromUser);
			if (genresModel.intersects(itemGenres, genres)) {
				itemsWithSameGenre.add(itemIDFromUser);
				itemsSimilarity.add(setSimilarity.getSimilarity(itemGenres, genres));
			}
		}
		return itemsSimilarity;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

}
