package recsys.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;
import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class SushiGlobalClassificationRecommender extends SushiClassificationRecommender {

	protected Instances globalTrainingSet;
	protected Classifier globalClassifier;

	public SushiGlobalClassificationRecommender(DataModel dataModel, SushiUserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
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
