package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;

public abstract class SushiGlobalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final SushiDataModel sushiDataModel;

	public SushiGlobalClassificationRecommenderBuilder(SushiDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}
	
	abstract public Classifier createClassifier();

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, sushiDataModel);
		UserModel userModel = userModelBuilder.build();
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
		return "Sushi Global Random Forest Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

}
