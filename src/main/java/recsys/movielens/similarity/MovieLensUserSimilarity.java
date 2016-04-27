package recsys.movielens.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.Pair;

import recsys.model.SetPreference;
import recsys.movielens.model.movielens.User;
import recsys.movielens.model.movielens.UserModel;

public class MovieLensUserSimilarity implements UserSimilarity {

	private static final int MAX_DIFFERENCE = 4;
	private final UserModel userModel;

	private Map<Pair<Long, Long>, Double> cache;

	public MovieLensUserSimilarity(UserModel userModel) {
		this.userModel = userModel;
		cache = new HashMap<Pair<Long, Long>, Double>();
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public synchronized double userSimilarity(long userID1, long userID2) throws TasteException {
		Pair<Long, Long> key = new Pair<Long, Long>(userID1, userID2);
		Double cached = cache.get(key);
		if (cached != null) {
			return cached;
		} else {
			double similarity = computeSimilarity(userID1, userID2);
			cache.put(key, similarity);
			return similarity;
		}

	}

	private double computeSimilarity(long userID1, long userID2) {
		User user1 = userModel.get(userID1);
		User user2 = userModel.get(userID2);
		double genresSimilarity = calculateGenresSimilarity(user1, user2);
		double directoresSimilarity = calculateDirectorsSimilarity(user1, user2);
		double actorsSimilarity = calculateActorsSimilarity(user1, user2);
		double actressesSimilarity = calculateActressesSimilarity(user1, user2);
		// every partial similarity has the same weight: 1
//		 we need to divide by 4 (total weight)
		double userSimilarity = (genresSimilarity + directoresSimilarity + actorsSimilarity + actressesSimilarity) / 4;
		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		double transformedUserSimilarity = userSimilarity * 2 - 1;
		return transformedUserSimilarity;
	}

	private double calculateGenresSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getGenrePreferences(), user2.getGenrePreferences());
	}

	private double calculateDirectorsSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getDirectorPreferences(), user2.getDirectorPreferences());
	}

	private double calculateActorsSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getActorPreferences(), user2.getActorPreferences());
	}

	private double calculateActressesSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getActressPreferences(), user2.getActressPreferences());
	}

	private double calculatePropertySetSimilarity(SetPreference set1, SetPreference set2) {
		Set<Integer> commonPropertyIds = getCommonPropertyIds(set1.getAllPropertyIds(), set2.getAllPropertyIds());
		List<Double> user1Preferences = new ArrayList<>();
		List<Double> user2Preferences = new ArrayList<>();
		for (Integer propertyId : commonPropertyIds) {
			user1Preferences.add(set1.getPropertyAverage(propertyId));
			user2Preferences.add(set2.getPropertyAverage(propertyId));
		}
		return calculateCommonPropertiesSimilarity(user1Preferences, user2Preferences);
	}

	private double calculateCommonPropertiesSimilarity(List<Double> user1Preferences, List<Double> user2Preferences) {
		double nominator = 0;
		for (int i = 0; i < user1Preferences.size(); i++) {
			double propertyPreference1 = user1Preferences.get(i);
			double propertyPreference2 = user2Preferences.get(i);
			double abs = Math.abs(propertyPreference1 - propertyPreference2);
			nominator += abs;
		}
		if (user1Preferences.size() == 0) {
			return 0;
		} else {
			return 1 - nominator / (user1Preferences.size() * MAX_DIFFERENCE);
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
	public void setPreferenceInferrer(PreferenceInferrer inferrer) {
	}

	@Override
	public String getName() {
		return "MovieLens User Similarity";
	}

	@Override
	public String getShortName() {
		return "MLUS";
	}

}
