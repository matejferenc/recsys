package recsys.notebooks.similarity;

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
import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUser;
import recsys.notebooks.model.NotebooksUserModel;

public class NotebooksUserSimilarity implements UserSimilarity {

	private static final int MAX_RATING_DIFFERENCE = 1;
	private final NotebooksUserModel notebooksUserModel;
	private EnumSet<Include> include;
	
	public enum Include {
		HDD ("h"),
		DISPLAY ("d"),
		PRICE("p"),
		MANUFACTURER("m"),
		RAM("r");
		
		private String shortName;

		private Include(String shortName) {
			this.shortName = shortName;
		}
		
		public String getShortName(){
			return shortName;
		}
	}

	public NotebooksUserSimilarity(NotebooksUserModel notebooksUserModel, EnumSet<Include> include) {
		this.notebooksUserModel = notebooksUserModel;
		this.include = include;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double userSimilarity(Integer userID1, Integer userID2) throws TasteException {
		double similarity = computeSimilarity((int) userID1, (int) userID2);
		return similarity;

	}

	private double computeSimilarity(int userID1, int userID2) {
		NotebooksUser user1 = notebooksUserModel.get(userID1);
		NotebooksUser user2 = notebooksUserModel.get(userID2);
		
		double userSimilarity = (include.contains(Include.HDD) ? calculateHddSimilarity(user1, user2) : 0)
				+ (include.contains(Include.DISPLAY) ? calculateDisplaySimilarity(user1, user2) : 0)
				+ (include.contains(Include.MANUFACTURER) ? calculateManufacturerSimilarity(user1, user2) : 0)
				+ (include.contains(Include.PRICE) ? calculatePriceSimilarity(user1, user2) : 0)
				+(include.contains(Include.RAM) ? calculateRamSimilarity(user1, user2) : 0);
		
		userSimilarity /= include.size();

		// correction for Taste framework (interface says the return value should be between -1 and +1,
		// yet the computed similarity is between 0 and +1)
		double transformedUserSimilarity = userSimilarity * 2 - 1;
		// Double transformedUserSimilarity = userSimilarity;
		return transformedUserSimilarity;
	}

	private double calculatePriceSimilarity(NotebooksUser user1, NotebooksUser user2) {
		double preferred1 = user1.getPricePreferences().getPreferredValue();
		double preferred2 = user2.getPricePreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_PRICE;
	}

	/**
	 * 
	 * @param user1
	 * @param user2
	 * @return number from interval [0,1]
	 */
	private Double calculateRamSimilarity(NotebooksUser user1, NotebooksUser user2) {
		Double preferred1 = user1.getRamPreferences().getPreferredValue();
		Double preferred2 = user2.getRamPreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_RAM;
	}

	private Double calculateHddSimilarity(NotebooksUser user1, NotebooksUser user2) {
		Double preferred1 = user1.getHddPreferences().getPreferredValue();
		Double preferred2 = user2.getHddPreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_HDD;
	}

	private Double calculateDisplaySimilarity(NotebooksUser user1, NotebooksUser user2) {
		Double preferred1 = user1.getDisplayPreferences().getPreferredValue();
		Double preferred2 = user2.getDisplayPreferences().getPreferredValue();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_DISPLAY;
	}

	private Double calculateManufacturerSimilarity(NotebooksUser user1, NotebooksUser user2) {
		return calculatePropertySetSimilarity(user1.getManufacturerPreferences(), user2.getManufacturerPreferences());
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

	/**
	 * 
	 * @param user1Preferences
	 * @param user2Preferences
	 * @return number from interval [0,1]
	 */
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
			return 1 - nominator / (user1Preferences.size() * MAX_RATING_DIFFERENCE);
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
		return "Notebooks User Similarity";
	}
	
	@Override
	public String getShortName() {
		String result = "S";
		for (Include i: include) {
			result += i.shortName;
		}
		return result;
	}

}
