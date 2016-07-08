package recsys.sushi.similarity;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.model.SetPreference;
import recsys.sushi.evaluator.IncludeProperties;
import recsys.sushi.model.SushiUser;
import recsys.sushi.model.SushiUserModel;

public class SushiUserWeightsSimilarity implements UserSimilarity {

	private static final int maxRating = 4;

	private final SushiUserModel userModel;

	private static final Double maxVariance = 4d;

	private EnumSet<IncludeProperties> includeProperties;


	public SushiUserWeightsSimilarity(SushiUserModel userModel, EnumSet<IncludeProperties> includeProperties) {
		this.userModel = userModel;
		this.includeProperties = includeProperties;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double userSimilarity(Integer userID1, Integer userID2) throws TasteException {
		return computeSimilarity((int)userID1, (int)userID2);
	}

	private Double computeSimilarity(int userID1, int userID2) {
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

	private Double calculatePriceSimilarity(SushiUser user1, SushiUser user2) {
		Double variance1 = user1.getPricePreferences().getVariance();
		Double variance2 = user2.getPricePreferences().getVariance();
		return calculateSimilarityFromWeights(variance1, variance2);
	}

	private Double calculateOilinessSimilarity(SushiUser user1, SushiUser user2) {
		Double variance1 = user1.getOilinessPreferences().getVariance();
		Double variance2 = user2.getOilinessPreferences().getVariance();
		return calculateSimilarityFromWeights(variance1, variance2);
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
		Double weight1 = calculatePropertySetWeight(set1);
		Double weight2 = calculatePropertySetWeight(set2);
		return calculateSimilarityFromWeights(weight1, weight2);
	}

	private Double calculateSimilarityFromWeights(Double weight1, Double weight2) {
		Double diff = Math.abs(weight1 - weight2) / maxVariance;
		return 1 / (1 + diff);
	}

	private Double calculatePropertySetWeight(SetPreference set) {
		Set<Integer> allPropertyIds = set.getAllPropertyIds();
		Double accumulatedVariance = 0d;
		for (Integer propertyId : allPropertyIds) {
			Double propertyVariance = set.getPropertyVariance(propertyId) * set.getPropertyAverage(propertyId);
			accumulatedVariance += propertyVariance;
		}
		return accumulatedVariance / (allPropertyIds.size() * maxRating);
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
