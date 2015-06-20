package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.SushiUserModel;

public class SushiContentBasedRecommenderBuilder implements RecommenderBuilder {

	private final SushiItemDataModel sushiDataModel;

	public SushiContentBasedRecommenderBuilder(SushiItemDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		SushiUserModelBuilder userModelBuilder = new SushiUserModelBuilder(dataModel, sushiDataModel);
		SushiUserModel userModel = userModelBuilder.build();
		return new SushiContentBasedRecommender(dataModel, userModel, sushiDataModel);
	}

	@Override
	public String getName() {
		return "Sushi Content Based Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "FIXME Sushi Content Based Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

}
