package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;

public class SushiUserWeightsSimilarityBuilder implements UserSimilarityBuilder {

	private final SushiDataModel sushiDataModel;
	
	public SushiUserWeightsSimilarityBuilder(SushiDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, sushiDataModel);
		UserModel userModel = userModelBuilder.build();
		return new SushiUserWeightsSimilarity(userModel);
	}

}