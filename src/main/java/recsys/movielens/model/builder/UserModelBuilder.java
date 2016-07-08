package recsys.movielens.model.builder;

import java.util.Collection;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.IntPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.model.ItemPreference;
import recsys.model.SetPreference;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.User;
import recsys.movielens.model.movielens.UserModel;

public class UserModelBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(UserModelBuilder.class);

	private final DataModel ratingsDataModel;

	private final MovieLensEnrichedModel movieLensEnrichedModel;

	public UserModelBuilder(DataModel ratingsDataModel, MovieLensEnrichedModel movieLensEnrichedModel) throws TasteException {
		this.ratingsDataModel = ratingsDataModel;
		this.movieLensEnrichedModel = movieLensEnrichedModel;
	}

	public UserModel build() throws TasteException {
		UserModel userModel = new UserModel();
		IntPrimitiveIterator userIDs = ratingsDataModel.getUserIDs();
		// cycle all users
		int preferencesProcessed = 0;
		while (userIDs.hasNext()) {
			Integer userID = userIDs.next();
			User user = userModel.getOrCreate(userID);
			PreferenceArray preferencesFromUser = ratingsDataModel.getPreferencesFromUser(userID);
			// cycle user's preferences
			for (Preference preference : preferencesFromUser) {
				Double p = preference.getValue();
				Integer itemID = preference.getItemID();
				buildImdbGenresPreferences(user, itemID, p);
				buildImdbDirectorsPreferences(user, itemID, p);
				buildImdbActorsPreferences(user, itemID, p);
				buildImdbActressesPreferences(user, itemID, p);
//				buildImdbKeywordsPreferences(user, itemID, p);
				if (preferencesProcessed % 10000 == 0) {
					log.info("processed: " + preferencesProcessed + " preferences");
				}
				preferencesProcessed++;
			}
		}
		freeReferences(userModel);
		return userModel;
	}

	private void freeReferences(UserModel userModel) throws TasteException {
		IntPrimitiveIterator userIDs = ratingsDataModel.getUserIDs();
		while (userIDs.hasNext()) {
			int userID = userIDs.next();
			User user = userModel.get(userID);
			freeReferencesForPreferences(user.getGenrePreferences());
			freeReferencesForPreferences(user.getDirectorPreferences());
			freeReferencesForPreferences(user.getActorPreferences());
			freeReferencesForPreferences(user.getActressPreferences());
			freeReferencesForPreferences(user.getKeywordsPreferences());
		}
	}

	private void freeReferencesForPreferences(SetPreference preferences) {
		Collection<ItemPreference> values = preferences.getAllPropertyValues();
		for (ItemPreference itemPreference : values) {
			itemPreference.freeReferences();
		}
	}

	private void buildImdbGenresPreferences(User user, int itemID, Double p) {
		Set<Integer> itemImdbGenres = movieLensEnrichedModel.getItemImdbGenres(itemID);
		for (Integer genreId : itemImdbGenres) {
			user.getGenrePreferences().addPropertyPreference(genreId, p);
		}
	}

	private void buildImdbDirectorsPreferences(User user, int itemID, Double p) {
		Set<Integer> itemImdbDirectors = movieLensEnrichedModel.getItemImdbDirectors(itemID);
		for (Integer directorId : itemImdbDirectors) {
			user.getDirectorPreferences().addPropertyPreference(directorId, p);
		}
	}

	private void buildImdbActorsPreferences(User user, int itemID, Double p) {
		Set<Integer> itemImdbActors = movieLensEnrichedModel.getItemImdbActors(itemID);
		for (Integer actorId : itemImdbActors) {
			user.getActorPreferences().addPropertyPreference(actorId, p);
		}
	}

	private void buildImdbActressesPreferences(User user, int itemID, Double p) {
		Set<Integer> itemImdbActresses = movieLensEnrichedModel.getItemImdbActresses(itemID);
		for (Integer actressId : itemImdbActresses) {
			user.getActressPreferences().addPropertyPreference(actressId, p);
		}
	}
	
	private void buildImdbKeywordsPreferences(User user, int itemID, Double p) {
		Set<Integer> itemImdbKeywords = movieLensEnrichedModel.getItemImdbKeywords(itemID);
		for (Integer keywordId : itemImdbKeywords) {
			user.getKeywordsPreferences().addPropertyPreference(keywordId, p);
		}
	}
}
