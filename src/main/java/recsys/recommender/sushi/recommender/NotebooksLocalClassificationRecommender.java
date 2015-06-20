package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUserModel;
import weka.classifiers.Classifier;

public abstract class NotebooksLocalClassificationRecommender extends NotebooksClassificationRecommender{

	public NotebooksLocalClassificationRecommender(DataModel dataModel, NotebooksUserModel userModel, NotebooksDataModel notebooksDataModel) throws Exception {
		super(dataModel, userModel, notebooksDataModel);
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			double localResult = getLocalResult(userID, itemID);
			
			return (float) localResult;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	public abstract Classifier createClassifier();

}
