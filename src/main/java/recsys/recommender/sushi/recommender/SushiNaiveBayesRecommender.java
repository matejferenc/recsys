package recsys.recommender.sushi.recommender;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
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
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.base.Preconditions;

public class SushiNaiveBayesRecommender implements Recommender {

	private final DataModel dataModel;
	private final UserModel userModel;
	private final SushiDataModel sushiDataModel;

	private static final Logger log = LoggerFactory.getLogger(SushiNaiveBayesRecommender.class);
	private FastVector attributes;
	private Attribute styleAttribute;
	private Attribute majorGroupAttribute;
	private Attribute minorGroupAttribute;
	private Attribute priceAttribute;
	private Attribute oilinessAttribute;
	private Attribute ratingAttribute;

	public SushiNaiveBayesRecommender(DataModel dataModel, UserModel userModel, SushiDataModel sushiDataModel) {
		this.dataModel = dataModel;
		this.userModel = userModel;
		this.sushiDataModel = sushiDataModel;
		attributes = createAttributes();
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
			PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);

			Classifier classifier = new NaiveBayes();

			Instances trainingSet = new Instances("a", attributes, preferencesFromUser.length());
			// Set class index
			trainingSet.setClassIndex(5);

			fillTrainingSet(userID, trainingSet);

			classifier.buildClassifier(trainingSet);

			Instance testInstance = fillTestSet(itemID, trainingSet);

			double[] classificationResult = classifier.distributionForInstance(testInstance);

			double sumOfEstimates = 0;
			double sumOfProbabilities = 0;
			for (int i = 0; i < classificationResult.length; i++) {
				sumOfEstimates += classificationResult[i] * i;
				sumOfProbabilities += classificationResult[i];
			}
			double resultEstimate = sumOfEstimates / sumOfProbabilities;
			return (float) resultEstimate;
		} catch (Exception e) {
			throw new TasteException(e);
		}
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

	private void fillTrainingSet(long userID, Instances trainingSet) throws TasteException {
		PreferenceArray preferencesFromUser = dataModel.getPreferencesFromUser(userID);
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
