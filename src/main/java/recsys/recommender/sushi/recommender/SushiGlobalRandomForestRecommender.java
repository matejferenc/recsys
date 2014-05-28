package recsys.recommender.sushi.recommender;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.recommender.sushi.SushiPiece;
import recsys.recommender.sushi.model.SushiDataModel;
import recsys.recommender.sushi.model.UserModel;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.base.Preconditions;

public class SushiGlobalRandomForestRecommender implements Recommender {

	private final DataModel dataModel;
	private final UserModel userModel;
	private final SushiDataModel sushiDataModel;

	private static final Logger log = LoggerFactory.getLogger(SushiGlobalRandomForestRecommender.class);
	private FastVector attributes;
	private Attribute styleAttribute;
	private Attribute majorGroupAttribute;
	private Attribute minorGroupAttribute;
	private Attribute priceAttribute;
	private Attribute oilinessAttribute;
	private Attribute ratingAttribute;
	private Instances globalTrainingSet;
	private Classifier globalClassifier;

	public SushiGlobalRandomForestRecommender(DataModel dataModel, UserModel userModel, SushiDataModel sushiDataModel) throws Exception {
		this.dataModel = dataModel;
		this.userModel = userModel;
		this.sushiDataModel = sushiDataModel;
		attributes = createAttributes();
		trainGlobalModel();
	}

	private void trainGlobalModel() throws Exception {
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

		globalClassifier = new RandomForest();

		globalClassifier.buildClassifier(globalTrainingSet);
	}

	private double getModelResult(Instance testInstance, Classifier classifier) throws Exception {
		double[] classificationResult = classifier.distributionForInstance(testInstance);

		double sumOfEstimates = 0;
		double sumOfProbabilities = 0;
		for (int i = 0; i < classificationResult.length; i++) {
			sumOfEstimates += classificationResult[i] * i;
			sumOfProbabilities += classificationResult[i];
		}
		double resultEstimate = sumOfEstimates / sumOfProbabilities;
		return resultEstimate;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
		return null;
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			Instance globalTestInstance = fillTestSet(itemID, globalTrainingSet);
			double globalResult = getModelResult(globalTestInstance, globalClassifier);
			
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			Classifier localClassifier = new RandomForest();
			Instances localTrainingSet = trainLocalModel(preferencesFromUser, localClassifier);
			
			Instance localTestInstance = fillTestSet(itemID, localTrainingSet);
			double localResult = getModelResult(localTestInstance, localClassifier);
			
			return (float) (globalResult + localResult) / 2;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	private Instances trainLocalModel(PreferenceArray preferencesFromUser, Classifier localClassifier) throws TasteException, Exception {
		Instances localTrainingSet = new Instances("a", attributes, preferencesFromUser.length());
		// Set class index
		localTrainingSet.setClassIndex(5);
		fillTrainingSet(preferencesFromUser, localTrainingSet);
		localClassifier.buildClassifier(localTrainingSet);
		return localTrainingSet;
	}

	private Instance fillTestSet(long itemID, Instances trainingSet) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece((int) itemID);

		// Create the instance
		Instance instance = new Instance(5);
		instance.setValue(styleAttribute, sushiPiece.getStyle());
		instance.setValue(majorGroupAttribute, sushiPiece.getMajorGroup());
		instance.setValue(minorGroupAttribute, sushiPiece.getMinorGroup());
		instance.setValue(oilinessAttribute, sushiPiece.getOiliness());
		instance.setValue(priceAttribute, sushiPiece.getPrice());

		instance.setDataset(trainingSet);
		return instance;
	}

	private void fillTrainingSet(PreferenceArray preferencesFromUser, Instances trainingSet) throws TasteException {
		for (Preference preference : preferencesFromUser) {
			long itemID = preference.getItemID();
			SushiPiece sushiPiece = sushiDataModel.getSushiPiece((int) itemID);

			// Create the instance
			Instance instance = new Instance(6);
			instance.setValue(styleAttribute, sushiPiece.getStyle());
			instance.setValue(majorGroupAttribute, sushiPiece.getMajorGroup());
			instance.setValue(minorGroupAttribute, sushiPiece.getMinorGroup());
			instance.setValue(oilinessAttribute, sushiPiece.getOiliness());
			instance.setValue(priceAttribute, sushiPiece.getPrice());
			instance.setValue(ratingAttribute, preference.getValue());

			// add the instance
			trainingSet.add(instance);
		}
	}

	private FastVector createAttributes() {
		// Declare a nominal attribute along with its values
		FastVector style = new FastVector(2);
		style.addElement("0");
		style.addElement("1");
		styleAttribute = new Attribute("style", style);

		// Declare a nominal attribute along with its values
		FastVector majorGroup = new FastVector(2);
		majorGroup.addElement("0");
		majorGroup.addElement("1");
		majorGroupAttribute = new Attribute("majorGroup", majorGroup);

		// Declare a nominal attribute along with its values
		FastVector minorGroup = new FastVector(2);
		minorGroup.addElement("0");
		minorGroup.addElement("1");
		minorGroup.addElement("2");
		minorGroup.addElement("3");
		minorGroup.addElement("4");
		minorGroup.addElement("5");
		minorGroup.addElement("6");
		minorGroup.addElement("7");
		minorGroup.addElement("8");
		minorGroup.addElement("9");
		minorGroup.addElement("10");
		minorGroup.addElement("11");
		minorGroupAttribute = new Attribute("minorGroup", minorGroup);

		priceAttribute = new Attribute("price");
		oilinessAttribute = new Attribute("oiliness");

		// Declare the class attribute along with its values
		FastVector rating = new FastVector(2);
		rating.addElement("0");
		rating.addElement("1");
		rating.addElement("2");
		rating.addElement("3");
		rating.addElement("4");
		ratingAttribute = new Attribute("rating", rating);

		// Declare the feature vector
		FastVector attributes = new FastVector(4);
		attributes.addElement(styleAttribute);
		attributes.addElement(majorGroupAttribute);
		attributes.addElement(minorGroupAttribute);
		attributes.addElement(priceAttribute);
		attributes.addElement(oilinessAttribute);
		attributes.addElement(ratingAttribute);
		return attributes;
	}

	@Override
	public void setPreference(long userID, long itemID, float value) throws TasteException {
		Preconditions.checkArgument(!Float.isNaN(value), "NaN value");
		log.debug("Setting preference for user {}, item {}", userID, itemID);
		dataModel.setPreference(userID, itemID, value);
	}

	@Override
	public void removePreference(long userID, long itemID) throws TasteException {
		log.debug("Remove preference for user '{}', item '{}'", userID, itemID);
		dataModel.removePreference(userID, itemID);
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}

}
