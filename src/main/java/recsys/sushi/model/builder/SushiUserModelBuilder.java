package recsys.sushi.model.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.sushi.dataset.SushiUserDataModelDataset;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUser;
import recsys.sushi.model.SushiUserModel;

public class SushiUserModelBuilder {

	private final DataModel ratingsDataModel;

	private final SushiItemDataModel sushiDataModel;

	private SushiUserModel userModel;


	public SushiUserModelBuilder(DataModel ratingsDataModel, SushiItemDataModel sushiDataModel) throws TasteException {
		this.ratingsDataModel = ratingsDataModel;
		this.sushiDataModel = sushiDataModel;
		try {
			userModel = new SushiUserDataModelDataset().build();
		} catch (Exception e) {
			throw new TasteException("Error while loading user model", e);
		}
	}

	public SushiUserModel build() throws TasteException {
		LongPrimitiveIterator userIDs = ratingsDataModel.getUserIDs();
		// cycle all users
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			// we created userModel in constructor, so we can change it arbitrarily
			SushiUser user = userModel.getOrCreate(userID.intValue());
			PreferenceArray preferencesFromUser = ratingsDataModel.getPreferencesFromUser(userID);
			// cycle user's preferences
			for (Preference preference : preferencesFromUser) {
				double p = preference.getValue();
				int itemID = (int) preference.getItemID();
				buildStylePreferences(user, itemID, p);
				buildMajorGroupPreferences(user, itemID, p);
				buildMinorGroupPreferences(user, itemID, p);
				buildOilinessPreferences(user, itemID, p);
				buildEatingFrequencyPreferences(user, itemID, p);
				buildPricePreferences(user, itemID, p);
				buildSellingFrequencyPreferences(user, itemID, p);
			}
		}
		return userModel;
	}

	private void buildStylePreferences(SushiUser user, int itemID, double p) {
		int style = sushiDataModel.getSushiPiece(itemID).getStyle();
		user.getStylePreferences().addPropertyPreference(style, p);
	}
	
	private void buildMajorGroupPreferences(SushiUser user, int itemID, double p) {
		int majorGroup = sushiDataModel.getSushiPiece(itemID).getMajorGroup();
		user.getMajorGroupPreferences().addPropertyPreference(majorGroup, p);
	}
	
	private void buildMinorGroupPreferences(SushiUser user, int itemID, double p) {
		int minorGroup = sushiDataModel.getSushiPiece(itemID).getMinorGroup();
		user.getMinorGroupPreferences().addPropertyPreference(minorGroup, p);
	}
	
	private void buildOilinessPreferences(SushiUser user, int itemID, double p) {
		double oiliness = sushiDataModel.getSushiPiece(itemID).getOiliness();
		user.getOilinessPreferences().addPreference(oiliness, p);
	}
	
	private void buildEatingFrequencyPreferences(SushiUser user, int itemID, double p) {
		double eatingFrequency = sushiDataModel.getSushiPiece(itemID).getEatingFrequency();
		user.getEatingFrequencyPreferences().addPreference(eatingFrequency, p);
	}
	
	private void buildPricePreferences(SushiUser user, int itemID, double p) {
		double price = sushiDataModel.getSushiPiece(itemID).getPrice();
		user.getPricePreferences().addPreference(price, p);
	}
	
	private void buildSellingFrequencyPreferences(SushiUser user, int itemID, double p) {
		double sellingFrequency = sushiDataModel.getSushiPiece(itemID).getSellingFrequency();
		user.getOilinessPreferences().addPreference(sellingFrequency, p);
	}

}
