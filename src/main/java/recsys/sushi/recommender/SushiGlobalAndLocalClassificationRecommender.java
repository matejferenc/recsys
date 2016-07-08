package recsys.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;

public abstract class SushiGlobalAndLocalClassificationRecommender extends SushiClassificationRecommender {

	public SushiGlobalAndLocalClassificationRecommender(DataModel dataModel, SushiUserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
		trainGlobalModel();
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		try {
			Double globalResult = getGlobalResult(userID, itemID);

			Double localResult = getLocalResult(userID, itemID);

			return (double) (globalResult + localResult) / 2;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}
}
