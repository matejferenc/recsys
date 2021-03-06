package recsys.movielens.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.model.SetPreference;
import recsys.movielens.model.movielens.User;
import recsys.movielens.model.movielens.UserModel;

public class MovielensUserSimilarity implements UserSimilarity {

	private static final int MAX_DIFFERENCE = 4;
	
	private final UserModel userModel;
	
	private final MovielensUserSimilarityFunction movielensUserSimilarityFunction;

	public MovielensUserSimilarity(UserModel userModel, MovielensUserSimilarityFunction movielensUserSimilarityFunction) {
		this.userModel = userModel;
		this.movielensUserSimilarityFunction = movielensUserSimilarityFunction;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double userSimilarity(Integer userID1, Integer userID2) throws TasteException {
		Double similarity = computeSimilarity(userID1, userID2);
		return similarity;
	}

	private Double computeSimilarity(Integer userID1, Integer userID2) {
		User user1 = userModel.get(userID1);
		User user2 = userModel.get(userID2);
		Double genresSimilarity = calculateGenresSimilarity(user1, user2);
		Double directorsSimilarity = calculateDirectorsSimilarity(user1, user2);
		Double actorsSimilarity = calculateActorsSimilarity(user1, user2);
		Double actressesSimilarity = calculateActressesSimilarity(user1, user2);
		Double keywordsSimilarity = calculateKeywordsSimilarity(user1, user2);
		
		Double userSimilarity = movielensUserSimilarityFunction.calculateSimilarity(genresSimilarity, directorsSimilarity, actorsSimilarity, actressesSimilarity, keywordsSimilarity);
		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		Double transformedUserSimilarity = userSimilarity * 2 - 1;
		return transformedUserSimilarity;
	}

	private Double calculateGenresSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getGenrePreferences(), user2.getGenrePreferences());
	}

	private Double calculateDirectorsSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getDirectorPreferences(), user2.getDirectorPreferences());
	}

	private Double calculateActorsSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getActorPreferences(), user2.getActorPreferences());
	}

	private Double calculateActressesSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getActressPreferences(), user2.getActressPreferences());
	}
	
	private Double calculateKeywordsSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getKeywordsPreferences(), user2.getKeywordsPreferences());
	}

	private Double calculatePropertySetSimilarity(SetPreference set1, SetPreference set2) {
		Set<Integer> commonPropertyIds = getCommonPropertyIds(set1.getAllPropertyIds(), set2.getAllPropertyIds());
		List<Double> user1Preferences = new ArrayList<>();
		List<Double> user2Preferences = new ArrayList<>();
		for (Integer propertyId : commonPropertyIds) {
			user1Preferences.add(set1.getPropertyAverage(propertyId));
			user2Preferences.add(set2.getPropertyAverage(propertyId));
		}
		return calculateCommonPropertiesSimilarity(user1Preferences, user2Preferences);
	}

	private Double calculateCommonPropertiesSimilarity(List<Double> user1Preferences, List<Double> user2Preferences) {
		Double nominator = 0d;
		for (int i = 0; i < user1Preferences.size(); i++) {
			Double propertyPreference1 = user1Preferences.get(i);
			Double propertyPreference2 = user2Preferences.get(i);
			Double abs = Math.abs(propertyPreference1 - propertyPreference2);
			nominator += abs;
		}
		if (user1Preferences.size() == 0) {
			return 0d;
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
		return "Movielens User Similarity";
	}

	@Override
	public String getShortName() {
		return "MLUS";
	}

}
