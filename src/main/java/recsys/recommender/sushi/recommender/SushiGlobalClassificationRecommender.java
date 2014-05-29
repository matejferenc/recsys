package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public abstract class SushiGlobalClassificationRecommender extends SushiClassificationRecommender {

	protected Instances globalTrainingSet;
	protected Classifier globalClassifier;

	public SushiGlobalClassificationRecommender(DataModel dataModel, UserModel userModel, SushiDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
		trainGlobalModel();
	}

	protected void trainGlobalModel() throws Exception {
		int trainingSetSize = 0;
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			trainingSetSize += preferencesFromUser.length();
		}

		globalTrainingSet = new Instances("a", attributes, trainingSetSize);
		// Set class index
		globalTrainingSet.setClassIndex(5);

		userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			fillTrainingSet(preferencesFromUser, globalTrainingSet);
		}

		globalClassifier = createClassifier();

		globalClassifier.buildClassifier(globalTrainingSet);
	}

	public abstract Classifier createClassifier();

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			Instance globalTestInstance = fillTestSet(itemID, globalTrainingSet);
			double globalResult = getModelResult(globalTestInstance, globalClassifier);

			return (float) globalResult;
//			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
//			Classifier localClassifier = createClassifier();
//			Instances localTrainingSet = trainLocalModel(preferencesFromUser, localClassifier);

//			Instance localTestInstance = fillTestSet(itemID, localTrainingSet);
//			double localResult = getModelResult(localTestInstance, localClassifier);

//			return (float) (globalResult + localResult) / 2;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}
}
