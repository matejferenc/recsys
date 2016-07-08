package recsys.sushi.recommender;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.IntPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiPiece;
import recsys.sushi.model.SushiUser;
import recsys.sushi.model.SushiUserModel;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.base.Preconditions;

@SuppressWarnings("deprecation")
public abstract class SushiClassificationRecommender implements Recommender {

	protected final DataModel dataModel;
	protected final SushiItemDataModel sushiDataModel;
	protected SushiUserModel userModel;

	protected Instances globalTrainingSet;
	protected Classifier globalClassifier;

	private static final Logger log = LoggerFactory.getLogger(SushiClassificationRecommender.class);
	@SuppressWarnings({ "rawtypes" })
	protected FastVector attributes;
	private Attribute styleAttribute;
	private Attribute majorGroupAttribute;
	private Attribute minorGroupAttribute;
	private Attribute priceAttribute;
	private Attribute oilinessAttribute;
	protected Attribute ratingAttribute;

	@SuppressWarnings({ "unchecked" })
	public SushiClassificationRecommender(DataModel dataModel, SushiUserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		this.dataModel = dataModel;
		this.userModel = userModel;
		this.sushiDataModel = sushiDataModel;
		attributes = createAttributes();
		attributes.addElement(ratingAttribute);
	}

	protected Double getModelResult(Instance testInstance, Classifier classifier) throws Exception {
		double[] classificationResult = classifier.distributionForInstance(testInstance);

		double sumOfEstimates = 0f;
		double sumOfProbabilities = 0f;
		for (int i = 0; i < classificationResult.length; i++) {
			sumOfEstimates += classificationResult[i] * i;
			sumOfProbabilities += classificationResult[i];
		}
		Double resultEstimate = (double) (sumOfEstimates / sumOfProbabilities);
		return resultEstimate;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany) throws TasteException {
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany, IDRescorer rescorer) throws TasteException {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void trainGlobalModel() throws Exception {
		int trainingSetSize = 0;
		IntPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Integer userID = userIDs.next();
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			trainingSetSize += preferencesFromUser.length();
		}

		globalTrainingSet = new Instances("a", attributes, trainingSetSize);
		// Set class index
		globalTrainingSet.setClassIndex(getAttributeCount() - 1);

		userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Integer userID = userIDs.next();
			SushiUser user = userModel.get(userID.intValue());
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			fillTrainingSet(preferencesFromUser, globalTrainingSet, user);
		}

		globalClassifier = createClassifier();

		globalClassifier.buildClassifier(globalTrainingSet);
	}

	public abstract Classifier createClassifier();

	@SuppressWarnings("unchecked")
	protected Instances trainLocalModel(PreferenceArray preferencesFromUser, Classifier localClassifier, SushiUser user) throws TasteException, Exception {
		Instances localTrainingSet = new Instances("a", attributes, preferencesFromUser.length());
		// Set class index
		localTrainingSet.setClassIndex(getAttributeCount() - 1);
		fillTrainingSet(preferencesFromUser, localTrainingSet, user);
		localClassifier.buildClassifier(localTrainingSet);
		return localTrainingSet;
	}

	protected Instance fillTestSet(Integer itemID, Instances trainingSet, SushiUser user) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece((int) itemID);

		// Create the instance
		Instance instance = new DenseInstance(getAttributeCount() - 1);
		setAttributeValues(sushiPiece, instance, user);

		instance.setDataset(trainingSet);
		return instance;
	}

	public int getAttributeCount() {
		// in this basic model we have 6 attributes
		return 6;
	}

	protected void setAttributeValues(SushiPiece sushiPiece, Instance instance, SushiUser user) {
		instance.setValue(styleAttribute, sushiPiece.getStyle());
		instance.setValue(majorGroupAttribute, sushiPiece.getMajorGroup());
		instance.setValue(minorGroupAttribute, sushiPiece.getMinorGroup());
		instance.setValue(oilinessAttribute, sushiPiece.getOiliness());
		instance.setValue(priceAttribute, sushiPiece.getPrice());
	}

	private void setClassValue(Instance instance, Preference preference) {
		instance.setValue(ratingAttribute, preference.getValue());
	}

	protected void fillTrainingSet(PreferenceArray preferencesFromUser, Instances trainingSet, SushiUser user) throws TasteException {
		for (Preference preference : preferencesFromUser) {
			Integer itemID = preference.getItemID();
			SushiPiece sushiPiece = sushiDataModel.getSushiPiece((int) itemID);

			// Create the instance
			Instance instance = new DenseInstance(getAttributeCount());
			setAttributeValues(sushiPiece, instance, user);
			setClassValue(instance, preference);

			// add the instance
			trainingSet.add(instance);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected FastVector createAttributes() {
		// Declare a nominal attribute aInteger with its values
		FastVector style = new FastVector(2);
		style.addElement("0");
		style.addElement("1");
		styleAttribute = new Attribute("style", style);

		// Declare a nominal attribute aInteger with its values
		FastVector majorGroup = new FastVector(2);
		majorGroup.addElement("0");
		majorGroup.addElement("1");
		majorGroupAttribute = new Attribute("majorGroup", majorGroup);

		// Declare a nominal attribute aInteger with its values
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

		// Declare the class attribute aInteger with its values
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
		// attributes.addElement(ratingAttribute);
		return attributes;
	}

	@Override
	public void setPreference(Integer userID, Integer itemID, Double value) throws TasteException {
		Preconditions.checkArgument(!Double.isNaN(value), "NaN value");
		log.debug("Setting preference for user {}, item {}", userID, itemID);
		dataModel.setPreference(userID, itemID, value);
	}

	@Override
	public void removePreference(Integer userID, Integer itemID) throws TasteException {
		log.debug("Remove preference for user '{}', item '{}'", userID, itemID);
		dataModel.removePreference(userID, itemID);
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}

	protected Double getGlobalResult(Integer userID, Integer itemID) throws Exception {
		SushiUser user = userModel.get((int) userID);
		Instance globalTestInstance = fillTestSet(itemID, globalTrainingSet, user);
		Double globalResult = getModelResult(globalTestInstance, globalClassifier);
		return globalResult;
	}

	protected Double getLocalResult(Integer userID, Integer itemID) throws Exception {
		SushiUser user = userModel.get((int) userID);
		PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
		Classifier localClassifier = createClassifier();
		Instances localTrainingSet = trainLocalModel(preferencesFromUser, localClassifier, user);

		Instance localTestInstance = fillTestSet(itemID, localTrainingSet, user);
		Double localResult = getModelResult(localTestInstance, localClassifier);
		return localResult;
	}

}
