package recsys.recommender.sushi.recommender;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class SushiCombinedGlobalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final SushiDataModel sushiDataModel;

	public SushiCombinedGlobalClassificationRecommenderBuilder(SushiDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserModelBuilder userModelBuilder = new UserModelBuilder(dataModel, sushiDataModel);
		UserModel userModel = userModelBuilder.build();
		try {
			return new SushiCombinedGlobalClassificationRecommender(dataModel, userModel, sushiDataModel){

				@Override
				public List<Classifier> createClassifiers() {
					List<Classifier> classifiers = new ArrayList<>();
//					classifiers.add(new J48());//1.186
					classifiers.add(new RandomTree());//1.165
//					classifiers.add(new Logistic());//1.236
//					classifiers.add(new NaiveBayes());//1.238
//					classifiers.add(new RandomForest());//1.143
					return classifiers;
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
