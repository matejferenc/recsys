package recsys.recommender.sushi.recommender;

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

import recsys.recommender.model.SetPreference;
import recsys.recommender.sushi.model.User;
import recsys.recommender.sushi.model.UserModel;

public class SushiUserSimilarity implements UserSimilarity {

	private static final int MAX_DIFFERENCE = 4;
	private final UserModel userModel;

	private Map<Pair<Long, Long>, Double> cache;

	public SushiUserSimilarity(UserModel userModel) {
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
		double styleSimilarity = calculateStyleSimilarity(user1, user2);
		double majorGroupSimilarity = calculateMajorGroupSimilarity(user1, user2);
		double minorGroupSimilarity = calculateMinorGroupSimilarity(user1, user2);
		double oilinessSimilarity = calculateOilinessSimilarity(user1, user2);
		double priceSimilarity = calculatePriceSimilarity(user1, user2);
		// every partial similarity has the same weight: 1
		// we need to divide by 3 (total weight)
		double userSimilarity = (styleSimilarity + majorGroupSimilarity + minorGroupSimilarity + oilinessSimilarity + priceSimilarity) / 5;

		// double userSimilarity = (genresSimilarity);
		// double userSimilarity = (genresSimilarity + directoresSimilarity) / 2;
		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		double transformedUserSimilarity = userSimilarity * 2 - 1;
		return transformedUserSimilarity;
	}

	private double calculatePriceSimilarity(User user1, User user2) {
		// oiliness has values from interval [0,4]
		int MAX_PRICE = 5;
		double preferred1 = user1.getPricePreferences().preferredValue();
		double preferred2 = user2.getPricePreferences().preferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / MAX_PRICE;
	}

	/**
	 * 
	 * @param user1
	 * @param user2
	 * @return number from interval [0,1]
	 */
	private double calculateOilinessSimilarity(User user1, User user2) {
		// oiliness has values from interval [0,4]
		int MAX_OILINESS = 4;
		double preferred1 = user1.getOilinessPreferences().preferredValue();
		double preferred2 = user2.getOilinessPreferences().preferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / MAX_OILINESS;
	}

	private double calculateStyleSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getStylePreferences(), user2.getStylePreferences());
	}

	private double calculateMajorGroupSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getMajorGroupPreferences(), user2.getMajorGroupPreferences());
	}

	private double calculateMinorGroupSimilarity(User user1, User user2) {
		return calculatePropertySetSimilarity(user1.getMinorGroupPreferences(), user2.getMinorGroupPreferences());
	}

	private double calculatePropertySetSimilarity(SetPreference set1, SetPreference set2) {
		Set<Integer> commonPropertyIds = getCommonPropertyIds(set1.getAllPropertyIds(), set2.getAllPropertyIds());
		List<Double> user1Preferences = new ArrayList<>();
		List<Double> user2Preferences = new ArrayList<>();
		for (Integer propertyId : commonPropertyIds) {
			user1Preferences.add(set1.getPropertyPreference(propertyId));
			user2Preferences.add(set2.getPropertyPreference(propertyId));
		}
		return calculateCommonPropertiesSimilarity(user1Preferences, user2Preferences);
	}

	/**
	 * 
	 * @param user1Preferences
	 * @param user2Preferences
	 * @return number from interval [0,1]
	 */
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

}
