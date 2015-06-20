package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUser;
import recsys.recommender.notebooks.NotebooksUserModel;

public class NotebooksUserModelBuilder {

	private final DataModel ratingsDataModel;

	private final NotebooksDataModel notebooksDataModel;

	private NotebooksUserModel notebooksUserModel;


	public NotebooksUserModelBuilder(DataModel ratingsDataModel, NotebooksDataModel notebooksDataModel) throws TasteException {
		this.ratingsDataModel = ratingsDataModel;
		this.notebooksDataModel = notebooksDataModel;
		this.notebooksUserModel = new NotebooksUserModel();
	}

	public NotebooksUserModel build() throws TasteException {
		LongPrimitiveIterator userIDs = ratingsDataModel.getUserIDs();
		// cycle all users
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			// we created userModel in constructor, so we can change it arbitrarily
			NotebooksUser user = notebooksUserModel.getOrCreate(userID.intValue());
			PreferenceArray preferencesFromUser = ratingsDataModel.getPreferencesFromUser(userID);
			// cycle user's preferences
			for (Preference preference : preferencesFromUser) {
				double p = preference.getValue();
				int itemID = (int) preference.getItemID();
				buildHddPreferences(user, itemID, p);
				buildDisplayPreferences(user, itemID, p);
				buildPricePreferences(user, itemID, p);
				buildManufacturerPreferences(user, itemID, p);
				buildRamPreferences(user, itemID, p);
			}
		}
		return notebooksUserModel;
	}

	private void buildHddPreferences(NotebooksUser user, int itemID, double p) {
		int hdd = notebooksDataModel.getNotebook(itemID).getHdd();
		user.getHddPreferences().addPreference((double) hdd, p);
	}
	
	private void buildDisplayPreferences(NotebooksUser user, int itemID, double p) {
		int display = notebooksDataModel.getNotebook(itemID).getDisplay();
		user.getDisplayPreferences().addPreference((double) display, p);
	}
	
	private void buildManufacturerPreferences(NotebooksUser user, int itemID, double p) {
		String producer = notebooksDataModel.getNotebook(itemID).getProducer();
		// we store preference as hashcode of producer
		user.getManufacturerPreferences().addPropertyPreference(producer.hashCode(), p);
	}
	
	private void buildRamPreferences(NotebooksUser user, int itemID, double p) {
		int ram = notebooksDataModel.getNotebook(itemID).getRam();
		user.getRamPreferences().addPreference((double) ram, p);
	}
	
	private void buildPricePreferences(NotebooksUser user, int itemID, double p) {
		double price = notebooksDataModel.getNotebook(itemID).getPrice();
		user.getPricePreferences().addPreference(price, p);
	}
	
}
