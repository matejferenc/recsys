package recsys.sushi.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;
import recsys.sushi.model.builder.SushiUserModelBuilder;
import recsys.sushi.recommender.SushiGlobalClassificationRecommender;
import weka.classifiers.Classifier;

public abstract class SushiGlobalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final SushiItemDataModel sushiDataModel;

	public SushiGlobalClassificationRecommenderBuilder(SushiItemDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}
	
	abstract public Classifier createClassifier();

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		SushiUserModelBuilder userModelBuilder = new SushiUserModelBuilder(dataModel, sushiDataModel);
		SushiUserModel userModel = userModelBuilder.build();
		try {
			return new SushiGlobalClassificationRecommender(dataModel, userModel, sushiDataModel){

				@Override
				public Classifier createClassifier() {
					return SushiGlobalClassificationRecommenderBuilder.this.createClassifier();
				}
				
			};
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	public String getName() {
		return "Sushi Global Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "g_" + createClassifier().getClass().getSimpleName();
	}

	@Override
	public void freeReferences() {
	}

}
