package recsys.sushi.similarity.builder;

import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.similarity.builder.UserSimilarityBuilder;
import recsys.sushi.evaluator.IncludeProperties;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;
import recsys.sushi.model.builder.SushiUserModelBuilder;
import recsys.sushi.similarity.SushiUserWeightedSimilarity;

public class SushiUserWeightedSimilarityBuilder implements UserSimilarityBuilder {

	private final SushiItemDataModel sushiDataModel;
	private EnumSet<IncludeProperties> includeProperties;
	
	public SushiUserWeightedSimilarityBuilder(SushiItemDataModel sushiDataModel, EnumSet<IncludeProperties> includeProperties) {
		this.sushiDataModel = sushiDataModel;
		this.includeProperties = includeProperties;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		SushiUserModelBuilder userModelBuilder = new SushiUserModelBuilder(dataModel, sushiDataModel);
		SushiUserModel userModel = userModelBuilder.build();
		return new SushiUserWeightedSimilarity(userModel, includeProperties);
	}

}
