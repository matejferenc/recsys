package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;

public class SushiUserSimilarityBuilder implements UserSimilarityBuilder {

	private final SushiDataModel sushiDataModel;
	private final UserModel userModel;
	
	public SushiUserSimilarityBuilder(SushiDataModel sushiDataModel, UserModel userModel) {
		this.sushiDataModel = sushiDataModel;
		this.userModel = userModel;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, sushiDataModel, userModel);
		UserModel userModel = userModelBuilder.build();
		return new SushiUserSimilarity(userModel);
	}

}
