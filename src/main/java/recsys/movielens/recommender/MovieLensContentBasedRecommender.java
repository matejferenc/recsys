package recsys.movielens.recommender;

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

import recsys.model.SetPreference;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.User;
import recsys.movielens.model.movielens.UserModel;

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
	public List<RecommendedItem> recommend(Integer userID, int howMany) throws TasteException {
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany, IDRescorer rescorer) throws TasteException {
		return null;
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		User user = userModel.get(userID);
		Double genresRating = calculateGenresRating(user, itemID);
		Double directorsRating = calculateDirectorsRating(user, itemID);
		Double actorsRating = calculateActorsRating(user, itemID);
		Double actressesRating = calculateActressesRating(user, itemID);
		Double keywordsRating = calculateKeywordsRating(user, itemID);
		
		int nonZeroRatingCount = getNonZeroRatingCount(genresRating, directorsRating, actorsRating, actressesRating);
		return (double) ((genresRating + directorsRating + actorsRating + actressesRating) / nonZeroRatingCount);
		
	}

	private int getNonZeroRatingCount(Double genresRating, Double directorsRating, Double actorsRating, Double actressesRating) {
		return isNonZero(genresRating) + isNonZero(directorsRating) + isNonZero(actorsRating) + isNonZero(actressesRating);
	}

	private int isNonZero(Double rating) {
		return Math.abs(rating) < 0.001 ? 0 : 1;
	}
	
	private Double calculateKeywordsRating(User user, int itemID) {
		Set<Integer> itemImdbKeywords = movieLensMovieModel.getItemImdbKeywords(itemID);
		SetPreference keywordsPreferences = user.getKeywordsPreferences();
		return calculatePreference(itemImdbKeywords, keywordsPreferences);
	}

	private Double calculateActressesRating(User user, int itemID) {
		Set<Integer> itemImdbActresses = movieLensMovieModel.getItemImdbActresses(itemID);
		SetPreference actressPreferences = user.getActressPreferences();
		return calculatePreference(itemImdbActresses, actressPreferences);
	}

	private Double calculateActorsRating(User user, int itemID) {
		Set<Integer> itemImdbActors = movieLensMovieModel.getItemImdbActors(itemID);
		SetPreference actorPreferences = user.getActorPreferences();
		return calculatePreference(itemImdbActors, actorPreferences);
	}

	private Double calculateDirectorsRating(User user, int itemID) {
		Set<Integer> itemImdbDirectors = movieLensMovieModel.getItemImdbDirectors(itemID);
		SetPreference directorPreferences = user.getDirectorPreferences();
		return calculatePreference(itemImdbDirectors, directorPreferences);
	}

	private Double calculateGenresRating(User user, int itemID) {
		Set<Integer> itemImdbGenres = movieLensMovieModel.getItemImdbGenres(itemID);
		SetPreference genrePreferences = user.getGenrePreferences();
		return calculatePreference(itemImdbGenres, genrePreferences);
	}

	private Double calculatePreference(Set<Integer> itemImdbGenres, SetPreference genrePreferences) {
		Set<Integer> allPropertyIds = genrePreferences.getAllPropertyIds();
		Set<Integer> commonPropertyIds = getCommonPropertyIds(itemImdbGenres, allPropertyIds);
		Double totalPreference = 0d;
		for (Integer propertyId : commonPropertyIds) {
			Double propertyPreference = genrePreferences.getPropertyAverage(propertyId);
			totalPreference += propertyPreference;
		}
		if (commonPropertyIds.size() == 0) {
			return 0d;
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
	public void setPreference(Integer userID, Integer itemID, Double value) throws TasteException {
		Preconditions.checkArgument(!Double.isNaN(value), "NaN value");
		log.debug("Setting preference for user {}, item {}", userID, itemID);
		dataModel.setPreference(userID, itemID, value);
	}

	@Override
	public void removePreference(Integer userID, Integer itemID) throws TasteException {
		log.debug("Remove preference for user '{}', item '{}'", userID, itemID);
		dataModel.removePreference(userID, itemID);
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}

}
