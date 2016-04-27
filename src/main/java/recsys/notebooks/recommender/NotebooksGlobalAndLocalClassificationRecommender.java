package recsys.notebooks.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUserModel;

public abstract class NotebooksGlobalAndLocalClassificationRecommender extends NotebooksClassificationRecommender {

	public NotebooksGlobalAndLocalClassificationRecommender(DataModel dataModel, NotebooksUserModel userModel, NotebooksDataModel notebooksDataModel) throws Exception {
		super(dataModel, userModel, notebooksDataModel);
		trainGlobalModel();
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			double globalResult = getGlobalResult(userID, itemID);

			double localResult = getLocalResult(userID, itemID);

			return (float) (globalResult + localResult) / 2;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}
}
