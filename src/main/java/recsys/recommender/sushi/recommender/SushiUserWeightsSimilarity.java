package recsys.recommender.sushi.recommender;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.Pair;

import recsys.recommender.model.SetPreference;
import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.SushiUser;
import recsys.recommender.sushi.model.SushiUserModel;

public class SushiUserWeightsSimilarity implements UserSimilarity {

	private static final int maxRating = 4;

	private final SushiUserModel userModel;

	private Map<Pair<Long, Long>, Double> cache;

	private static final double maxVariance = 4;

	private EnumSet<Include> includeProperties;


	public SushiUserWeightsSimilarity(SushiUserModel userModel, EnumSet<Include> includeProperties) {
		this.userModel = userModel;
		this.includeProperties = includeProperties;
		cache = new HashMap<Pair<Long, Long>, Double>();
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double userSimilarity(long userID1, long userID2) throws TasteException {
//		Pair<Long, Long> key = new Pair<Long, Long>(userID1, userID2);
//		Double cached = cache.get(key);
//		Pair<Long, Long> keySwapped = new Pair<Long, Long>(userID2, userID1);
//		Double cachedSwapped = cache.get(keySwapped);
//		if (cached != null) {
//			return cached;
//		} else if (cachedSwapped != null) {
//			return cachedSwapped;
//		} else {
			double similarity = computeSimilarity((int)userID1, (int)userID2);
//			cache.put(key, similarity);
			return similarity;
//		}

	}

	private double computeSimilarity(int userID1, int userID2) {
		SushiUser user1 = userModel.get(userID1);
		SushiUser user2 = userModel.get(userID2);
		
		double userSimilarity = (includeProperties.contains(Include.STYLE) ? calculateStyleSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.MAJOR) ? calculateMajorGroupSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.MINOR) ? calculateMinorGroupSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.OILINESS) ? calculateOilinessSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.PRICE) ? calculatePriceSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.GENDER) ? calculateGenderSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.AGE) ? calculateAgeSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.REGION15) ? calculateRegion15Similarity(user1, user2) : 0) +
				(includeProperties.contains(Include.REGION_CURRENT) ? calculateRegionCurrentSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.PREFECTURE15) ? calculatePrefecture15Similarity(user1, user2) : 0) +
				(includeProperties.contains(Include.PREFECTURE_CURRENT) ? calculatePrefectureCurrentSimilarity(user1, user2) : 0) +
				(includeProperties.contains(Include.EAST_WEST15) ? calculateEastWest15Similarity(user1, user2) : 0) +
				(includeProperties.contains(Include.EAST_WEST_CURRENT) ? calculateEastWestCurrentSimilarity(user1, user2) : 0);

		userSimilarity /= includeProperties.size();
		
		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		double transformedUserSimilarity = userSimilarity * 2 - 1;
		return transformedUserSimilarity;
	}

	private double calculatePriceSimilarity(SushiUser user1, SushiUser user2) {
		double variance1 = user1.getPricePreferences().getVariance();
		double variance2 = user2.getPricePreferences().getVariance();
		return calculateSimilarityFromWeights(variance1, variance2);
	}

	private double calculateOilinessSimilarity(SushiUser user1, SushiUser user2) {
		double variance1 = user1.getOilinessPreferences().getVariance();
		double variance2 = user2.getOilinessPreferences().getVariance();
		return calculateSimilarityFromWeights(variance1, variance2);
	}

	private double calculateStyleSimilarity(SushiUser user1, SushiUser user2) {
		return calculatePropertySetSimilarity(user1.getStylePreferences(), user2.getStylePreferences());
	}

	private double calculateMajorGroupSimilarity(SushiUser user1, SushiUser user2) {
		return calculatePropertySetSimilarity(user1.getMajorGroupPreferences(), user2.getMajorGroupPreferences());
	}

	private double calculateMinorGroupSimilarity(SushiUser user1, SushiUser user2) {
		return calculatePropertySetSimilarity(user1.getMinorGroupPreferences(), user2.getMinorGroupPreferences());
	}

	private double calculatePropertySetSimilarity(SetPreference set1, SetPreference set2) {
		double weight1 = calculatePropertySetWeight(set1);
		double weight2 = calculatePropertySetWeight(set2);
		return calculateSimilarityFromWeights(weight1, weight2);
	}

	private double calculateSimilarityFromWeights(double weight1, double weight2) {
		double diff = Math.abs(weight1 - weight2) / maxVariance;
		return 1 / (1 + diff);
	}

	private double calculatePropertySetWeight(SetPreference set) {
		Set<Integer> allPropertyIds = set.getAllPropertyIds();
		double accumulatedVariance = 0;
		for (Integer propertyId : allPropertyIds) {
			double propertyVariance = set.getPropertyVariance(propertyId) * set.getPropertyAverage(propertyId);
			accumulatedVariance += propertyVariance;
		}
		return accumulatedVariance / (allPropertyIds.size() * maxRating);
	}
	
	private double calculateEastWestCurrentSimilarity(SushiUser user1, SushiUser user2) {
		return user1.getEastWestIDCurrent() == user2.getEastWestIDCurrent() ? 1 : 0;
	}

	private double calculateEastWest15Similarity(SushiUser user1, SushiUser user2) {
		return user1.getEastWestIDUntil15() == user2.getEastWestIDUntil15() ? 1 : 0;
	}

	private double calculateRegion15Similarity(SushiUser user1, SushiUser user2) {
		return user1.getRegionIDUntil15() == user2.getRegionIDUntil15() ? 1 : 0;
	}

	private double calculateRegionCurrentSimilarity(SushiUser user1, SushiUser user2) {
		return user1.getRegionIDCurrent() == user2.getRegionIDCurrent() ? 1 : 0;
	}

	private double calculatePrefecture15Similarity(SushiUser user1, SushiUser user2) {
		return user1.getPrefectureIDUntil15() == user2.getPrefectureIDUntil15() ? 1 : 0;
	}

	private double calculatePrefectureCurrentSimilarity(SushiUser user1, SushiUser user2) {
		return user1.getPrefectureIDCurrent() == user2.getPrefectureIDCurrent() ? 1 : 0;
	}

	private double calculateAgeSimilarity(SushiUser user1, SushiUser user2) {
		int MAX_AGE = 5;
		return 1 - (Math.abs(user1.getAge() - user2.getAge())) / MAX_AGE;
	}

	private double calculateGenderSimilarity(SushiUser user1, SushiUser user2) {
		return 1 - Math.abs(user1.getGender() - user2.getGender());
	}

	@Override
	public void setPreferenceInferrer(PreferenceInferrer inferrer) {
	}

	@Override
	public String getName() {
		return "Sushi User Weights Similarity";
	}

	@Override
	public String getShortName() {
		return "W";
	}

}
