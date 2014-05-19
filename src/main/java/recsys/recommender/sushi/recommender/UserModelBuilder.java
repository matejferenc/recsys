package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.recommender.sushi.SushiPiece;
import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.User;
import recsys.recommender.sushi.model.UserModel;

public class UserModelBuilder {

	private final DataModel ratingsDataModel;

	private final SushiDataModel sushiDataModel;

	public UserModelBuilder(DataModel ratingsDataModel, SushiDataModel sushiDataModel) throws TasteException {
		this.ratingsDataModel = ratingsDataModel;
		this.sushiDataModel = sushiDataModel;
	}

	public UserModel build() throws TasteException {
		UserModel userModel = new UserModel();
		LongPrimitiveIterator userIDs = ratingsDataModel.getUserIDs();
		// cycle all users
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			User user = userModel.getOrCreate(userID);
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

	private void buildStylePreferences(User user, int itemID, double p) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		int style = sushiPiece.getStyle();
		user.getStylePreferences().addPropertyPreference(style, p);
	}
	
	private void buildMajorGroupPreferences(User user, int itemID, double p) {
		int majorGroup = sushiDataModel.getSushiPiece(itemID).getMajorGroup();
		user.getMajorGroupPreferences().addPropertyPreference(majorGroup, p);
	}
	
	private void buildMinorGroupPreferences(User user, int itemID, double p) {
		int minorGroup = sushiDataModel.getSushiPiece(itemID).getMinorGroup();
		user.getMinorGroupPreferences().addPropertyPreference(minorGroup, p);
	}
	
	private void buildOilinessPreferences(User user, int itemID, double p) {
		double oiliness = sushiDataModel.getSushiPiece(itemID).getOiliness();
		user.getOilinessPreferences().addPreference(oiliness, p);
	}
	
	private void buildEatingFrequencyPreferences(User user, int itemID, double p) {
		double eatingFrequency = sushiDataModel.getSushiPiece(itemID).getEatingFrequency();
		user.getEatingFrequencyPreferences().addPreference(eatingFrequency, p);
	}
	
	private void buildPricePreferences(User user, int itemID, double p) {
		double price = sushiDataModel.getSushiPiece(itemID).getPrice();
		user.getPricePreferences().addPreference(price, p);
	}
	
	private void buildSellingFrequencyPreferences(User user, int itemID, double p) {
		double sellingFrequency = sushiDataModel.getSushiPiece(itemID).getSellingFrequency();
		user.getOilinessPreferences().addPreference(sellingFrequency, p);
	}

}
