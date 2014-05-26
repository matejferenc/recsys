package recsys.recommender.sushi.recommender;

import java.util.Collection;
import java.util.HashMap;
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

public class SushiUserWeightsSimilarity implements UserSimilarity {

	private final UserModel userModel;

	private Map<Pair<Long, Long>, Double> cache;


	public SushiUserWeightsSimilarity(UserModel userModel) {
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
		Pair<Long, Long> keySwapped = new Pair<Long, Long>(userID2, userID1);
		Double cachedSwapped = cache.get(keySwapped);
		if (cached != null) {
			return cached;
		} else if (cachedSwapped != null) {
			return cachedSwapped;
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

		// double userSimilarity = minorGroupSimilarity;

		// double userSimilarity = (styleSimilarity + majorGroupSimilarity + minorGroupSimilarity)/3;

		// double userSimilarity = (genderSimilarity + ageSimilarity + region15Similarity + regionCurrentSimilarity + prefecture15Similarity + prefectureCurrentSimilarity + eastWest15Similarity +
		// eastWestCurrentSimilarity) / 8;

		// every partial similarity has the same weight: 1
		// we need to divide by total weight
		// double userSimilarity = (styleSimilarity + majorGroupSimilarity + minorGroupSimilarity + priceSimilarity) / 4;

		double userSimilarity = (styleSimilarity + majorGroupSimilarity + minorGroupSimilarity + priceSimilarity + oilinessSimilarity) / 5;

		// double userSimilarity = (styleSimilarity + majorGroupSimilarity + minorGroupSimilarity) / 3;

		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		double transformedUserSimilarity = userSimilarity * 2 - 1;
		// double transformedUserSimilarity = userSimilarity;
		return transformedUserSimilarity;
	}

	private double calculatePriceSimilarity(User user1, User user2) {
		double variance1 = user1.getPricePreferences().getVariance();
		double variance2 = user2.getPricePreferences().getVariance();
		return calculateSimilarityFromVariances(variance1, variance2);
	}

	private double calculateOilinessSimilarity(User user1, User user2) {
		double variance1 = user1.getOilinessPreferences().getVariance();
		double variance2 = user2.getOilinessPreferences().getVariance();
		return calculateSimilarityFromVariances(variance1, variance2);
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
		double variance1 = calculatePropertySetVariance(set1);
		double variance2 = calculatePropertySetVariance(set2);
		return calculateSimilarityFromVariances(variance1, variance2);
	}

	private double calculateSimilarityFromVariances(double variance1, double variance2) {
		// maximum of variance for random variable x in [0,4]
		double maxVariance = 4;
		double diff = Math.abs(variance1 - variance2) / maxVariance;
		return 1 / (1 + diff);
	}

	private double calculatePropertySetVariance(SetPreference set) {
		Set<Integer> allPropertyIds = set.getAllPropertyIds();
		double accumulatedVariance = 0;
		for (Integer propertyId : allPropertyIds) {
			double propertyVariance = set.getPropertyVariance(propertyId);
			accumulatedVariance += propertyVariance;
		}
		return accumulatedVariance / allPropertyIds.size();
	}

	@Override
	public void setPreferenceInferrer(PreferenceInferrer inferrer) {
	}

	@Override
	public String getName() {
		return "Sushi User Weights Similarity";
	}

}
