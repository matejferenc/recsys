package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;

public abstract class SushiLocalClassificationRecommender extends SushiClassificationRecommender{

	public SushiLocalClassificationRecommender(DataModel dataModel, UserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
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
