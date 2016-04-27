package recsys.sushi.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiUserModel;
import recsys.sushi.model.builder.SushiUserModelBuilder;
import recsys.sushi.recommender.SushiLocalClassificationRecommender;
import weka.classifiers.Classifier;

public abstract class SushiLocalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final SushiItemDataModel sushiDataModel;

	public SushiLocalClassificationRecommenderBuilder(SushiItemDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}
	
	abstract public Classifier createClassifier();

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		SushiUserModelBuilder userModelBuilder = new SushiUserModelBuilder(dataModel, sushiDataModel);
		SushiUserModel userModel = userModelBuilder.build();
		try {
			return new SushiLocalClassificationRecommender(dataModel, userModel, sushiDataModel){

				@Override
				public Classifier createClassifier() {
					return SushiLocalClassificationRecommenderBuilder.this.createClassifier();
				}
				
			};
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	public String getName() {
		return "Sushi Local Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "l_" + createClassifier().getClass().getSimpleName();
	}

	@Override
	public void freeReferences() {
	}

}
