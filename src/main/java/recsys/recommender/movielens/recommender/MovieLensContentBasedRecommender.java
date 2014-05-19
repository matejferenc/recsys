package recsys.recommender.movielens.recommender;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.recommender.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.recommender.movielens.model.movielens.SetPreference;
import recsys.recommender.movielens.model.movielens.User;
import recsys.recommender.movielens.model.movielens.UserModel;

import com.google.common.base.Preconditions;

public class MovieLensContentBasedRecommender implements Recommender {

	private final DataModel dataModel;
	private final UserModel userModel;
	private final MovieLensEnrichedModel movieLensMovieModel;

	private static final Logger log = LoggerFactory.getLogger(MovieLensContentBasedRecommender.class);

	public MovieLensContentBasedRecommender(DataModel dataModel, UserModel userModel, MovieLensEnrichedModel movieLensEnrichedModel) {
		this.dataModel = dataModel;
		this.userModel = userModel;
		movieLensMovieModel = movieLensEnrichedModel;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
		return null;
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		User user = userModel.get(userID);
		double genresRating = calculateGenresRating(user, itemID);
		double directorsRating = calculateDirectorsRating(user, itemID);
		double actorsRating = calculateActorsRating(user, itemID);
		double actressesRating = calculateActressesRating(user, itemID);
		
//		int nonZeroRatingCount = getNonZeroRatingCount(genresRating, 0, 0, 0);
//		return (float) (genresRating / nonZeroRatingCount);
		
//		int nonZeroRatingCount = getNonZeroRatingCount(0, directorsRating, 0, 0);
//		return (float) (directorsRating / nonZeroRatingCount);
		
//		int nonZeroRatingCount = getNonZeroRatingCount(genresRating, directorsRating, actorsRating, actressesRating);
//		return (float) ((genresRating + directorsRating + actorsRating + actressesRating) / nonZeroRatingCount);
		
//		int nonZeroRatingCount = getNonZeroRatingCount(genresRating, directorsRating, 0, 0);
//		return (float) ((genresRating + directorsRating) / nonZeroRatingCount);
		
//		int nonZeroRatingCount = getNonZeroRatingCount(0, 0, actorsRating, actressesRating);
//		return (float) ((actorsRating + actressesRating) / nonZeroRatingCount);

		int nonZeroRatingCount = getNonZeroRatingCount(genresRating, directorsRating, actorsRating, 0);
		return (float) ((genresRating + directorsRating + actorsRating) / nonZeroRatingCount);
		
//		int nonZeroRatingCount = getNonZeroRatingCount(genresRating, directorsRating, 0, actressesRating);
//		return (float) ((genresRating + directorsRating + actressesRating) / nonZeroRatingCount);
	}

	private int getNonZeroRatingCount(double genresRating, double directorsRating, double actorsRating, double actressesRating) {
		return isNonZero(genresRating) + isNonZero(directorsRating) + isNonZero(actorsRating) + isNonZero(actressesRating);
	}

	private int isNonZero(double rating) {
		return Math.abs(rating) < 0.001 ? 0 : 1;
	}

	private double calculateActressesRating(User user, long itemID) {
		Set<Integer> itemImdbActresses = movieLensMovieModel.getItemImdbActresses(itemID);
		SetPreference actressPreferences = user.getActressPreferences();
		return calculatePreference(itemImdbActresses, actressPreferences);
	}

	private double calculateActorsRating(User user, long itemID) {
		Set<Integer> itemImdbActors = movieLensMovieModel.getItemImdbActors(itemID);
		SetPreference actorPreferences = user.getActorPreferences();
		return calculatePreference(itemImdbActors, actorPreferences);
	}

	private double calculateDirectorsRating(User user, long itemID) {
		Set<Integer> itemImdbDirectors = movieLensMovieModel.getItemImdbDirectors(itemID);
		SetPreference directorPreferences = user.getDirectorPreferences();
		return calculatePreference(itemImdbDirectors, directorPreferences);
	}

	private double calculateGenresRating(User user, long itemID) {
		Set<Integer> itemImdbGenres = movieLensMovieModel.getItemImdbGenres(itemID);
		SetPreference genrePreferences = user.getGenrePreferences();
		return calculatePreference(itemImdbGenres, genrePreferences);
	}

	private double calculatePreference(Set<Integer> itemImdbGenres, SetPreference genrePreferences) {
		Set<Integer> allPropertyIds = genrePreferences.getAllPropertyIds();
		Set<Integer> commonPropertyIds = getCommonPropertyIds(itemImdbGenres, allPropertyIds);
		double totalPreference = 0;
		for (Integer propertyId : commonPropertyIds) {
			double propertyPreference = genrePreferences.getPropertyPreference(propertyId);
			totalPreference += propertyPreference;
		}
		if (commonPropertyIds.size() == 0) {
			return 0;
		} else {
			return totalPreference / commonPropertyIds.size();
		}
	}

	private Set<Integer> getCommonPropertyIds(Set<Integer> allPropertyIds1, Set<Integer> allPropertyIds2) {
		Set<Integer> common = new HashSet<>();
		for (Integer propertyId : allPropertyIds1) {
			if (allPropertyIds2.contains(propertyId)) {
				common.add(propertyId);
			}
		}
		return common;
	}

	@Override
	public void setPreference(long userID, long itemID, float value) throws TasteException {
		Preconditions.checkArgument(!Float.isNaN(value), "NaN value");
		log.debug("Setting preference for user {}, item {}", userID, itemID);
		dataModel.setPreference(userID, itemID, value);
	}

	@Override
	public void removePreference(long userID, long itemID) throws TasteException {
		log.debug("Remove preference for user '{}', item '{}'", userID, itemID);
		dataModel.removePreference(userID, itemID);
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}

}
