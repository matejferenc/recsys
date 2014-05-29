package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public abstract class SushiLocalClassificationRecommender extends SushiClassificationRecommender{

	public SushiLocalClassificationRecommender(DataModel dataModel, UserModel userModel, SushiDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			Classifier localClassifier = createClassifier();
			Instances localTrainingSet = trainLocalModel(preferencesFromUser, localClassifier);
			
			Instance localTestInstance = fillTestSet(itemID, localTrainingSet);
			double localResult = getModelResult(localTestInstance, localClassifier);
			
			return (float) localResult;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	public abstract Classifier createClassifier();

}
