package recsys.sushi.similarity.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.similarity.builder.UserSimilarityBuilder;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;
import recsys.sushi.model.builder.SushiUserModelBuilder;
import recsys.sushi.similarity.SushiUserSimilarityFunction;
import recsys.sushi.similarity.SushiUserWeightsSimilarity;

public class SushiUserWeightsSimilarityBuilder implements UserSimilarityBuilder {

	private final SushiItemDataModel sushiDataModel;
	private final SushiUserSimilarityFunction sushiUserSimilarityFunction;
	
	public SushiUserWeightsSimilarityBuilder(SushiItemDataModel sushiDataModel, SushiUserSimilarityFunction sushiUserSimilarityFunction) {
		this.sushiDataModel = sushiDataModel;
		this.sushiUserSimilarityFunction = sushiUserSimilarityFunction;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		SushiUserModelBuilder userModelBuilder = new SushiUserModelBuilder(dataModel, sushiDataModel);
		SushiUserModel userModel = userModelBuilder.build();
		return new SushiUserWeightsSimilarity(userModel, sushiUserSimilarityFunction);
	}

}
