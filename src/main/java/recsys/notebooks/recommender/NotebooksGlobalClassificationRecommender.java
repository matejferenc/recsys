package recsys.notebooks.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUserModel;
import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class NotebooksGlobalClassificationRecommender extends NotebooksClassificationRecommender {

	protected Instances globalTrainingSet;
	protected Classifier globalClassifier;

	public NotebooksGlobalClassificationRecommender(DataModel dataModel, NotebooksUserModel userModel, NotebooksDataModel notebooksDataModel) throws Exception {
		super(dataModel, userModel, notebooksDataModel);
		trainGlobalModel();
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		try {
			Double globalResult = getGlobalResult(userID, itemID);

			return (double) globalResult;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}
}
