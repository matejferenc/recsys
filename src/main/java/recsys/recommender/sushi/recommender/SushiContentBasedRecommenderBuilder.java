package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;

public class SushiContentBasedRecommenderBuilder implements RecommenderBuilder {

	private final SushiDataModel sushiDataModel;
	private final UserModel userModel;

	public SushiContentBasedRecommenderBuilder(SushiDataModel sushiDataModel, UserModel userModel) {
		this.sushiDataModel = sushiDataModel;
		this.userModel = userModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, sushiDataModel, userModel);
		UserModel userModel = userModelBuilder.build();
		return new SushiContentBasedRecommender(dataModel, userModel, sushiDataModel);
	}

	@Override
	public String getName() {
		return "Sushi Content Based Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

}