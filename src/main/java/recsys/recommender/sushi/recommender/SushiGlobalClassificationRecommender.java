package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class SushiGlobalClassificationRecommender extends SushiClassificationRecommender {

	protected Instances globalTrainingSet;
	protected Classifier globalClassifier;

	public SushiGlobalClassificationRecommender(DataModel dataModel, UserModel userModel, SushiDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
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
