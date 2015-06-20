package recsys.recommender.sushi.recommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
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
import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.SushiUser;
import recsys.recommender.sushi.model.SushiUserModel;

public class SushiUserSimilarity implements UserSimilarity {

	private static final int MAX_DIFFERENCE = 4;
	private final SushiUserModel userModel;

	private Map<Pair<Long, Long>, Double> cache;
	private EnumSet<Include> includeProperties;

	public SushiUserSimilarity(SushiUserModel userModel, EnumSet<Include> includeProperties) {
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
//		Double cached;
//		Double cachedSwapped;
//		synchronized (cache) {
//			cached = cache.get(key);
//			Pair<Long, Long> keySwapped = new Pair<Long, Long>(userID2, userID1);
//			cachedSwapped = cache.get(keySwapped);
//		}
//		if (cached != null) {
//			return cached;
//		} else if (cachedSwapped != null) {
//			return cachedSwapped;
//		} else {
			double similarity = computeSimilarity((int) userID1, (int) userID2);
//			synchronized (cache) {
//				cache.put(key, similarity);
//			}
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

	private double calculatePriceSimilarity(SushiUser user1, SushiUser user2) {
		double preferred1 = user1.getPricePreferences().getPreferredValue();
		double preferred2 = user2.getPricePreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / SushiItemDataModel.MAX_PRICE;
	}

	/**
	 * 
	 * @param user1
	 * @param user2
	 * @return number from interval [0,1]
	 */
	private double calculateOilinessSimilarity(SushiUser user1, SushiUser user2) {
		double preferred1 = user1.getOilinessPreferences().getPreferredValue();
		double preferred2 = user2.getOilinessPreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / SushiItemDataModel.MAX_OILINESS;
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
		Set<Integer> commonPropertyIds = getCommonPropertyIds(set1.getAllPropertyIds(), set2.getAllPropertyIds());
		List<Double> user1Preferences = new ArrayList<>();
		List<Double> user2Preferences = new ArrayList<>();
		for (Integer propertyId : commonPropertyIds) {
			user1Preferences.add(set1.getPropertyAverage(propertyId));
			user2Preferences.add(set2.getPropertyAverage(propertyId));
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
		return "Sushi User Similarity";
	}
	
	@Override
	public String getShortName() {
		return "S";
	}

}
