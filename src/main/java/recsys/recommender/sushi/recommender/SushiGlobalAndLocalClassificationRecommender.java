package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.UserModel;

public abstract class SushiGlobalAndLocalClassificationRecommender extends SushiClassificationRecommender {

	public SushiGlobalAndLocalClassificationRecommender(DataModel dataModel, UserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
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
