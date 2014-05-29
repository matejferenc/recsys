package recsys.recommender.sushi.recommender;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public abstract class SushiCombinedGlobalClassificationRecommender extends SushiClassificationRecommender {

	protected Instances globalTrainingSet;
	protected List<Classifier> globalClassifiers;

	public SushiCombinedGlobalClassificationRecommender(DataModel dataModel, UserModel userModel, SushiDataModel sushiDataModel) throws Exception {
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

		globalClassifiers = createClassifiers();

		for (Classifier classifier : globalClassifiers) {
			classifier.buildClassifier(globalTrainingSet);
		}
	}

	public abstract List<Classifier> createClassifiers();

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		List<Double> globalResults = new ArrayList<Double>();
		List<Double> localResults = new ArrayList<Double>();
		try {
			Instance globalTestInstance = fillTestSet(itemID, globalTrainingSet);
			for (Classifier classifier : globalClassifiers) {
				double globalResult = getModelResult(globalTestInstance, classifier);
				globalResults.add(globalResult);
			}

			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);

			List<Classifier> localClassifier = createClassifiers();
			for (Classifier classifier : localClassifier) {
				Instances localTrainingSet = trainLocalModel(preferencesFromUser, classifier);
				Instance localTestInstance = fillTestSet(itemID, localTrainingSet);
				double localResult = getModelResult(localTestInstance, classifier);
				localResults.add(localResult);
			}

			double sumOfResults = 0;
			for (Double globalResult : globalResults) {
				sumOfResults += globalResult;
			}
			for (Double localResult : localResults) {
				sumOfResults += localResult;
			}

			return (float) sumOfResults / (globalResults.size() + localResults.size());
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}
}
