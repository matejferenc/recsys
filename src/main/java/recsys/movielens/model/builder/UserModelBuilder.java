package recsys.movielens.model.builder;

import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.User;
import recsys.movielens.model.movielens.UserModel;

public class UserModelBuilder {

	private final DataModel ratingsDataModel;

	private final MovieLensEnrichedModel movieLensMovieModel;

	public UserModelBuilder(DataModel ratingsDataModel, MovieLensEnrichedModel movieLensEnrichedModel) throws TasteException {
		this.ratingsDataModel = ratingsDataModel;
		this.movieLensMovieModel = movieLensEnrichedModel;
	}

	public UserModel build() throws TasteException {
		UserModel userModel = new UserModel();
		LongPrimitiveIterator userIDs = ratingsDataModel.getUserIDs();
		// cycle all users
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			User user = userModel.getOrCreate(userID);
			PreferenceArray preferencesFromUser = ratingsDataModel.getPreferencesFromUser(userID);
			// cycle user's preferences
			for (Preference preference : preferencesFromUser) {
				double p = preference.getValue();
				long itemID = preference.getItemID();
				buildImdbGenresPreferences(user, itemID, p);
				buildImdbDirectorsPreferences(user, itemID, p);
				buildImdbActorsPreferences(user, itemID, p);
				buildImdbActressesPreferences(user, itemID, p);
			}
		}
		return userModel;
	}

	private void buildImdbGenresPreferences(User user, long itemID, double p) {
		Set<Integer> itemImdbGenres = movieLensMovieModel.getItemImdbGenres(itemID);
		for (Integer genreId : itemImdbGenres) {
			user.getGenrePreferences().addPropertyPreference(genreId, p);
		}
	}

	private void buildImdbDirectorsPreferences(User user, long itemID, double p) {
		Set<Integer> itemImdbDirectors = movieLensMovieModel.getItemImdbDirectors(itemID);
		for (Integer genreId : itemImdbDirectors) {
			user.getDirectorPreferences().addPropertyPreference(genreId, p);
		}
	}

	private void buildImdbActorsPreferences(User user, long itemID, double p) {
		Set<Integer> itemImdbActors = movieLensMovieModel.getItemImdbActors(itemID);
		for (Integer genreId : itemImdbActors) {
			user.getActorPreferences().addPropertyPreference(genreId, p);
		}
	}

	private void buildImdbActressesPreferences(User user, long itemID, double p) {
		Set<Integer> itemImdbActresses = movieLensMovieModel.getItemImdbActresses(itemID);
		for (Integer genreId : itemImdbActresses) {
			user.getActressPreferences().addPropertyPreference(genreId, p);
		}
	}
}
