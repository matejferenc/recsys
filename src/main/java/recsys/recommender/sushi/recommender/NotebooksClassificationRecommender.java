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

import recsys.recommender.notebooks.Notebook;
import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUser;
import recsys.recommender.notebooks.NotebooksUserModel;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import com.google.common.base.Preconditions;

public abstract class NotebooksClassificationRecommender implements Recommender {

	protected final DataModel dataModel;
	protected final NotebooksDataModel sushiDataModel;
	protected NotebooksUserModel userModel;

	protected Instances globalTrainingSet;
	protected Classifier globalClassifier;

	private static final Logger log = LoggerFactory.getLogger(NotebooksClassificationRecommender.class);
	protected FastVector attributes;
	private Attribute hddAttribute;
	private Attribute displayAttribute;
	private Attribute priceAttribute;
	private Attribute ramAttribute;
	private Attribute producerAttribute;
	private Attribute ratingAttribute;

	public NotebooksClassificationRecommender(DataModel dataModel, NotebooksUserModel userModel, NotebooksDataModel notebooksDataModel) throws Exception {
		this.dataModel = dataModel;
		this.userModel = userModel;
		this.sushiDataModel = notebooksDataModel;
		attributes = createAttributes();
		attributes.addElement(ratingAttribute);
	}

	protected double getModelResult(Instance testInstance, Classifier classifier) throws Exception {
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

	protected void trainGlobalModel() throws Exception {
		int trainingSetSize = 0;
		LongPrimitiveIterator userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			trainingSetSize += preferencesFromUser.length();
		}

		globalTrainingSet = new Instances("a", attributes, trainingSetSize);
		globalTrainingSet.setClass(ratingAttribute);
		// Set class index
		globalTrainingSet.setClassIndex(getAttributeCount() - 1);

		userIDs = dataModel.getUserIDs();
		while (userIDs.hasNext()) {
			Long userID = userIDs.next();
			NotebooksUser user = userModel.get(userID.intValue());
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
			fillTrainingSet(preferencesFromUser, globalTrainingSet, user);
		}

		globalClassifier = createClassifier();

		globalClassifier.buildClassifier(globalTrainingSet);
	}

	public abstract Classifier createClassifier();

	protected Instances trainLocalModel(PreferenceArray preferencesFromUser, Classifier localClassifier, NotebooksUser user) throws TasteException, Exception {
		Instances localTrainingSet = new Instances("a", attributes, preferencesFromUser.length());
		// Set class index
		localTrainingSet.setClass(ratingAttribute);
		localTrainingSet.setClassIndex(getAttributeCount() - 1);
		fillTrainingSet(preferencesFromUser, localTrainingSet, user);
		localClassifier.buildClassifier(localTrainingSet);
		return localTrainingSet;
	}

	protected Instance fillTestSet(long itemID, Instances trainingSet, NotebooksUser user) {
		Notebook sushiPiece = sushiDataModel.getNotebook((int) itemID);

		// Create the instance
		Instance instance = new SparseInstance(getAttributeCount() - 1);
		setAttributeValues(sushiPiece, instance, user);

		instance.setDataset(trainingSet);
		return instance;
	}

	public int getAttributeCount() {
		// in this basic model we have 6 attributes
		return 6;
	}

	protected void setAttributeValues(Notebook notebook, Instance instance, NotebooksUser user) {
		instance.setValue(hddAttribute, (double)notebook.getHdd());
		instance.setValue(displayAttribute, (double)notebook.getDisplay());
		instance.setValue(priceAttribute, (double)notebook.getPrice());
		instance.setValue(producerAttribute, notebook.getProducer());
		instance.setValue(ramAttribute, (double)notebook.getRam());
	}

	private void setClassValue(Instance instance, Preference preference) {
		instance.setValue(ratingAttribute, (double)preference.getValue());
	}

	protected void fillTrainingSet(PreferenceArray preferencesFromUser, Instances trainingSet, NotebooksUser user) throws TasteException {
		for (Preference preference : preferencesFromUser) {
			long itemID = preference.getItemID();
			Notebook notebook = sushiDataModel.getNotebook((int) itemID);

			// Create the instance
			Instance instance = new SparseInstance(getAttributeCount());
			setAttributeValues(notebook, instance, user);
			setClassValue(instance, preference);

			// add the instance
			trainingSet.add(instance);
		}
	}

	protected FastVector createAttributes() {
		hddAttribute = new Attribute("hdd");

		displayAttribute = new Attribute("display");
		priceAttribute = new Attribute("price");
		
		FastVector producer = new FastVector(8);
		producer.addElement("ACER");
		producer.addElement("ASUS");
		producer.addElement("FUJITSU");
		producer.addElement("HP");
		producer.addElement("LENOVO");
		producer.addElement("MSI");
		producer.addElement("SONY");
		producer.addElement("TOSHIBA");
		producerAttribute = new Attribute("producer", producer);

		ramAttribute = new Attribute("ram");

		ratingAttribute = new Attribute("rating");

		// Declare the feature vector
		FastVector attributes = new FastVector(5);
		attributes.addElement(hddAttribute);
		attributes.addElement(displayAttribute);
		attributes.addElement(priceAttribute);
		attributes.addElement(producerAttribute);
		attributes.addElement(ramAttribute);
		// attributes.addElement(ratingAttribute);
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

	protected double getGlobalResult(long userID, long itemID) throws Exception {
		NotebooksUser user = userModel.get((int) userID);
		Instance globalTestInstance = fillTestSet(itemID, globalTrainingSet, user);
		double globalResult = getModelResult(globalTestInstance, globalClassifier);
		return globalResult;
	}

	protected double getLocalResult(long userID, long itemID) throws Exception {
		NotebooksUser user = userModel.get((int) userID);
		PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
		Classifier localClassifier = createClassifier();
		Instances localTrainingSet = trainLocalModel(preferencesFromUser, localClassifier, user);

		Instance localTestInstance = fillTestSet(itemID, localTrainingSet, user);
		double localResult = getModelResult(localTestInstance, localClassifier);
		return localResult;
	}

}
