package recsys.recommender.hetrecMovielens;

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

import recsys.movielens.similarity.SetSimilarity;

public class ContentBasedMovieGenreRecommender extends AbstractRecommender {

	private GenresDataModel styleModel;
	private SetSimilarity setSimilarity;

	public ContentBasedMovieGenreRecommender(DataModel dataModel, GenresDataModel styleModel, SetSimilarity setSimilarity) {
		super(dataModel);
		this.styleModel = styleModel;
		this.setSimilarity = setSimilarity;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
		throw new NotImplementedException("not implemented yet - recommend(userID, howMany, rescorer)");
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		Set<String> itemGenres = styleModel.getGenres(itemID);
		DataModel model = getDataModel();
		FastIDSet itemIDsFromUser = model.getItemIDsFromUser(userID);
		List<Long> itemsWithAtLeastOneSameGenre = new ArrayList<Long>();
		List<Float> similarities = getItemsWithAtLeastOneSameGenre(itemIDsFromUser, itemGenres, itemsWithAtLeastOneSameGenre);
		float averageRating = calculateAverageRating(itemsWithAtLeastOneSameGenre, userID, similarities);
		return averageRating;
	}

	private float calculateAverageRating(List<Long> itemsWithAtLeastOneSameGenre, Long userID, List<Float> similarities) {
		Float sumOfRatings = 0f;
		Float sumOfSimilarities = 0f;
		int i = 0;
		for (Long itemID : itemsWithAtLeastOneSameGenre) {
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

	/**
	 * 
	 * @param itemIDsFromUser
	 * @param itemGenres
	 * @param itemsWithSameGenre
	 *            output List of items
	 * @return
	 * @throws TasteException
	 */
	private List<Float> getItemsWithAtLeastOneSameGenre(FastIDSet itemIDsFromUser, Set<String> itemGenres, List<Long> itemsWithSameGenre) throws TasteException {
		List<Float> itemsSimilarity = new ArrayList<Float>();
		for (Long itemIDFromUser : itemIDsFromUser) {
			Set<String> style = styleModel.getGenres(itemIDFromUser);
			if (styleModel.intersects(itemGenres, style)) {
				itemsWithSameGenre.add(itemIDFromUser);
				itemsSimilarity.add(setSimilarity.getSimilarity(itemGenres, style));
			}
		}
		return itemsSimilarity;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		// TODO Auto-generated method stub

	}

}
