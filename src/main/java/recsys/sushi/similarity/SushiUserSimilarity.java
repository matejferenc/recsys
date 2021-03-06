package recsys.sushi.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.model.SetPreference;
import recsys.sushi.evaluator.IncludeProperties;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUser;
import recsys.sushi.model.SushiUserModel;

public class SushiUserSimilarity implements UserSimilarity {

	private static final int MAX_DIFFERENCE = 4;
	private final SushiUserModel userModel;

	private EnumSet<IncludeProperties> includeProperties;

	public SushiUserSimilarity(SushiUserModel userModel, EnumSet<IncludeProperties> includeProperties) {
		this.userModel = userModel;
		this.includeProperties = includeProperties;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double userSimilarity(Integer userID1, Integer userID2) throws TasteException {
		return computeSimilarity((int) userID1, (int) userID2);
	}

	private Double computeSimilarity(Integer userID1, Integer userID2) {
		SushiUser user1 = userModel.get(userID1);
		SushiUser user2 = userModel.get(userID2);
		
		Double userSimilarity = (includeProperties.contains(IncludeProperties.STYLE) ? calculateStyleSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.MAJOR) ? calculateMajorGroupSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.MINOR) ? calculateMinorGroupSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.OILINESS) ? calculateOilinessSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.PRICE) ? calculatePriceSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.GENDER) ? calculateGenderSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.AGE) ? calculateAgeSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.REGION15) ? calculateRegion15Similarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.REGION_CURRENT) ? calculateRegionCurrentSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.PREFECTURE15) ? calculatePrefecture15Similarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.PREFECTURE_CURRENT) ? calculatePrefectureCurrentSimilarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.EAST_WEST15) ? calculateEastWest15Similarity(user1, user2) : 0) +
				(includeProperties.contains(IncludeProperties.EAST_WEST_CURRENT) ? calculateEastWestCurrentSimilarity(user1, user2) : 0);
				
		userSimilarity /= includeProperties.size();

		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		Double transformedUserSimilarity = userSimilarity * 2 - 1;
		return transformedUserSimilarity;
	}

	private Double calculateEastWestCurrentSimilarity(SushiUser user1, SushiUser user2) {
		return (double) (user1.getEastWestIDCurrent() == user2.getEastWestIDCurrent() ? 1 : 0);
	}

	private Double calculateEastWest15Similarity(SushiUser user1, SushiUser user2) {
		return (double) (user1.getEastWestIDUntil15() == user2.getEastWestIDUntil15() ? 1 : 0);
	}

	private Double calculateRegion15Similarity(SushiUser user1, SushiUser user2) {
		return (double) (user1.getRegionIDUntil15() == user2.getRegionIDUntil15() ? 1 : 0);
	}

	private Double calculateRegionCurrentSimilarity(SushiUser user1, SushiUser user2) {
		return (double) (user1.getRegionIDCurrent() == user2.getRegionIDCurrent() ? 1 : 0);
	}

	private Double calculatePrefecture15Similarity(SushiUser user1, SushiUser user2) {
		return (double) (user1.getPrefectureIDUntil15() == user2.getPrefectureIDUntil15() ? 1 : 0);
	}

	private Double calculatePrefectureCurrentSimilarity(SushiUser user1, SushiUser user2) {
		return (double) (user1.getPrefectureIDCurrent() == user2.getPrefectureIDCurrent() ? 1 : 0);
	}

	private Double calculateAgeSimilarity(SushiUser user1, SushiUser user2) {
		int MAX_AGE = 5;
		return (double) (1 - (Math.abs(user1.getAge() - user2.getAge())) / MAX_AGE);
	}

	private Double calculateGenderSimilarity(SushiUser user1, SushiUser user2) {
		return (double) (1 - Math.abs(user1.getGender() - user2.getGender()));
	}

	private Double calculatePriceSimilarity(SushiUser user1, SushiUser user2) {
		Double preferred1 = user1.getPricePreferences().getPreferredValue();
		Double preferred2 = user2.getPricePreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / SushiItemDataModel.MAX_PRICE;
	}

	private Double calculateOilinessSimilarity(SushiUser user1, SushiUser user2) {
		Double preferred1 = user1.getOilinessPreferences().getPreferredValue();
		Double preferred2 = user2.getOilinessPreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / SushiItemDataModel.MAX_OILINESS;
	}

	private Double calculateStyleSimilarity(SushiUser user1, SushiUser user2) {
		return calculatePropertySetSimilarity(user1.getStylePreferences(), user2.getStylePreferences());
	}

	private Double calculateMajorGroupSimilarity(SushiUser user1, SushiUser user2) {
		return calculatePropertySetSimilarity(user1.getMajorGroupPreferences(), user2.getMajorGroupPreferences());
	}

	private Double calculateMinorGroupSimilarity(SushiUser user1, SushiUser user2) {
		return calculatePropertySetSimilarity(user1.getMinorGroupPreferences(), user2.getMinorGroupPreferences());
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
		return "Sushi User Similarity";
	}
	
	@Override
	public String getShortName() {
		return "S";
	}

}
