package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUserModel;
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
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			double globalResult = getGlobalResult(userID, itemID);

			return (float) globalResult;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}
}
