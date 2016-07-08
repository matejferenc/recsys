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

	private GenresDataModel genresModel;
	private SetSimilarity setSimilarity;

	public ContentBasedMovieGenreRecommender(DataModel dataModel, GenresDataModel genresModel, SetSimilarity setSimilarity) {
		super(dataModel);
		this.genresModel = genresModel;
		this.setSimilarity = setSimilarity;
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany, IDRescorer rescorer) throws TasteException {
		throw new NotImplementedException("not implemented yet - recommend(userID, howMany, rescorer)");
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		Set<String> itemGenres = genresModel.getGenres(itemID);
		DataModel model = getDataModel();
		FastIDSet itemIDsFromUser = model.getItemIDsFromUser(userID);
		List<Integer> itemsWithAtLeastOneSameGenre = new ArrayList<Integer>();
		List<Double> similarities = getItemsWithAtLeastOneSameGenre(itemIDsFromUser, itemGenres, itemsWithAtLeastOneSameGenre);
		Double averageRating = calculateAverageRating(itemsWithAtLeastOneSameGenre, userID, similarities);
		return averageRating;
	}

	private Double calculateAverageRating(List<Integer> itemsWithAtLeastOneSameGenre, Integer userID, List<Double> similarities) {
		Double sumOfRatings = 0d;
		Double sumOfSimilarities = 0d;
		int i = 0;
		for (Integer itemID : itemsWithAtLeastOneSameGenre) {
			try {
				Double similarity = similarities.get(i);
				Double preferenceValue = getDataModel().getPreferenceValue(userID, itemID);
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
	private List<Double> getItemsWithAtLeastOneSameGenre(FastIDSet itemIDsFromUser, Set<String> itemGenres, List<Integer> itemsWithSameGenre) throws TasteException {
		List<Double> itemsSimilarity = new ArrayList<Double>();
		for (Integer itemIDFromUser : itemIDsFromUser) {
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
		// TODO Auto-generated method stub

	}

}
