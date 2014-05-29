package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

public class SushiGlobalNaiveBayesRecommenderBuilder implements RecommenderBuilder {

	private final SushiDataModel sushiDataModel;

	public SushiGlobalNaiveBayesRecommenderBuilder(SushiDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, sushiDataModel);
		UserModel userModel = userModelBuilder.build();
		try {
			return new SushiGlobalClassificationRecommender(dataModel, userModel, sushiDataModel){

				@Override
				public Classifier createClassifier() {
					return new NaiveBayes();
//					return new J48();
//					return new RandomTree();
//					return new Logistic();
				}
				
			};
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	public String getName() {
		return "Sushi Global Naive Bayes Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

}
