package recsys.recommender.sushi.recommender;

import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.SushiUserModel;

public class SushiUserWeightedSimilarityBuilder implements UserSimilarityBuilder {

	private final SushiItemDataModel sushiDataModel;
	private EnumSet<Include> includeProperties;
	
	public SushiUserWeightedSimilarityBuilder(SushiItemDataModel sushiDataModel, EnumSet<Include> includeProperties) {
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
