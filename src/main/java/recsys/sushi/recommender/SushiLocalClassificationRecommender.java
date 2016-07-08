package recsys.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;
import weka.classifiers.Classifier;

public abstract class SushiLocalClassificationRecommender extends SushiClassificationRecommender {

	public SushiLocalClassificationRecommender(DataModel dataModel, SushiUserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		try {
			Double localResult = getLocalResult(userID, itemID);

			return (double) localResult;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	public abstract Classifier createClassifier();

}
