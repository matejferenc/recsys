package recsys.notebooks.recommender;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.notebooks.model.Notebook;
import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUser;
import recsys.notebooks.model.NotebooksUserModel;
import recsys.notebooks.similarity.NotebooksUserSimilarity.Include;

public class NotebooksRecommender implements Recommender {
	
	private static final int MAX_RATING_DIFFERENCE = 1;

	private EnumSet<Include> include;
	private NotebooksUserModel userModel;
	private DataModel dataModel;
	private NotebooksDataModel notebooksDataModel;

	public NotebooksRecommender(DataModel dataModel, NotebooksUserModel userModel, NotebooksDataModel notebooksDataModel, EnumSet<Include> include) {
		this.dataModel = dataModel;
		this.userModel = userModel;
		this.notebooksDataModel = notebooksDataModel;
		this.include = include;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany) throws TasteException {
		throw new RuntimeException("unsupported");
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany, IDRescorer rescorer) throws TasteException {
		throw new RuntimeException("unsupported");
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		NotebooksUser user = userModel.get((int) userID);
		Notebook notebook = notebooksDataModel.getNotebook((int) itemID);

		Double userSimilarity = (include.contains(Include.HDD) ? calculateHddSimilarity(user, notebook) : 0) + (include.contains(Include.DISPLAY) ? calculateDisplaySimilarity(user, notebook) : 0)
				+ (include.contains(Include.MANUFACTURER) ? calculateManufacturerSimilarity(user, notebook) : 0) + (include.contains(Include.PRICE) ? calculatePriceSimilarity(user, notebook) : 0)
				+ (include.contains(Include.RAM) ? calculateRamSimilarity(user, notebook) : 0);

		userSimilarity /= include.size();

		return (double) userSimilarity;
	}

	@Override
	public void setPreference(Integer userID, Integer itemID, Double value) throws TasteException {
	}

	@Override
	public void removePreference(Integer userID, Integer itemID) throws TasteException {
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}

	private Double calculatePriceSimilarity(NotebooksUser user, Notebook notebook) {
		Double preferred1 = user.getPricePreferences().getPreferredValue();
		Double preferred2 = (double) notebook.getPrice();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_PRICE;
	}

	private Double calculateRamSimilarity(NotebooksUser user, Notebook notebook) {
		Double preferred1 = user.getRamPreferences().getPreferredValue();
		Double preferred2 = (double) notebook.getRam();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_RAM;
	}

	private Double calculateHddSimilarity(NotebooksUser user, Notebook notebook) {
		Double preferred1 = user.getHddPreferences().getPreferredValue();
		Double preferred2 = (double) notebook.getHdd();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_HDD;
	}

	private Double calculateDisplaySimilarity(NotebooksUser user, Notebook notebook) {
		Double preferred1 = user.getDisplayPreferences().getPreferredValue();
		Double preferred2 = (double) notebook.getDisplay();
		return 1 - (Math.abs(preferred1 - preferred2)) / NotebooksDataModel.MAX_DISPLAY;
	}

	private Double calculateManufacturerSimilarity(NotebooksUser user, Notebook notebook) {
		int manufacturerHashCode = notebook.hashCode();
		for (Integer propertyId: user.getManufacturerPreferences().getAllPropertyIds()) {
			if (propertyId == manufacturerHashCode) {
				return user.getManufacturerPreferences().getPropertyAverage(propertyId) / MAX_RATING_DIFFERENCE;
			}
		}
		return 0d;
	}

}
