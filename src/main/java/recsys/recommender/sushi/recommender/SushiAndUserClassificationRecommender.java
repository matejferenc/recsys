package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.SushiPiece;
import recsys.recommender.sushi.model.SushiUser;
import recsys.recommender.sushi.model.SushiUserModel;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

@SuppressWarnings("deprecation")
public abstract class SushiAndUserClassificationRecommender extends SushiClassificationRecommender {

	private Attribute genderAttribute;
	private Attribute ageAttribute;
	private Attribute prefectureIDUntil15Attribute;
	private Attribute prefectureIDCurrentAttribute;
	private Attribute regionIDUntil15Attribute;
	private Attribute regionIDCurrentAttribute;
	private Attribute eastWestIDUntil15Attribute;
	private Attribute eastWestIDCurrentAttribute;

	public SushiAndUserClassificationRecommender(DataModel dataModel, SushiUserModel userModel, SushiItemDataModel sushiDataModel) throws Exception {
		super(dataModel, userModel, sushiDataModel);
		trainGlobalModel();
	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		try {
			double globalResult = getGlobalResult(userID, itemID);

			double localResult = getLocalResult(userID, itemID);

			return (float) (globalResult + localResult) / 2;
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	protected void setAttributeValues(SushiPiece sushiPiece, Instance instance, SushiUser user) {
		super.setAttributeValues(sushiPiece, instance, user);
		instance.setValue(ageAttribute, user.getAge());
		instance.setValue(genderAttribute, user.getGender());
		instance.setValue(prefectureIDCurrentAttribute, user.getPrefectureIDCurrent());
		instance.setValue(prefectureIDUntil15Attribute, user.getPrefectureIDUntil15());
		instance.setValue(regionIDCurrentAttribute, user.getRegionIDCurrent());
		instance.setValue(regionIDUntil15Attribute, user.getRegionIDUntil15());
		instance.setValue(eastWestIDCurrentAttribute, user.getEastWestIDCurrent());
		instance.setValue(eastWestIDUntil15Attribute, user.getEastWestIDUntil15());
	}

	public int getAttributeCount() {
		// in this model we added 8 attributes
		return super.getAttributeCount() + 8;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected FastVector createAttributes() {
		FastVector attributes = super.createAttributes();
		// Declare a nominal attribute along with its values
		FastVector gender = new FastVector(2);
		gender.addElement("0");
		gender.addElement("1");
		genderAttribute = new Attribute("gender", gender);

		ageAttribute = new Attribute("age");

		FastVector prefectureIDUntil15 = new FastVector(48);
		for (int i = 0; i < 48; i++) {
			prefectureIDUntil15.addElement(i + "");
		}
		prefectureIDUntil15Attribute = new Attribute("prefectureIDUntil15", prefectureIDUntil15);

		FastVector prefectureIDCurrent = new FastVector(48);
		for (int i = 0; i < 48; i++) {
			prefectureIDCurrent.addElement(i + "");
		}
		prefectureIDCurrentAttribute = new Attribute("prefectureIDCurrent", prefectureIDCurrent);

		FastVector regionIDUntil15 = new FastVector(12);
		for (int i = 0; i < 12; i++) {
			regionIDUntil15.addElement(i + "");
		}
		regionIDUntil15Attribute = new Attribute("regionIDUntil15", regionIDUntil15);

		FastVector regionIDCurrent = new FastVector(12);
		for (int i = 0; i < 12; i++) {
			regionIDCurrent.addElement(i + "");
		}
		regionIDCurrentAttribute = new Attribute("regionIDCurrent", regionIDCurrent);

		FastVector eastWestIDUntil15 = new FastVector(2);
		for (int i = 0; i < 2; i++) {
			eastWestIDUntil15.addElement(i + "");
		}
		eastWestIDUntil15Attribute = new Attribute("eastWestIDUntil15", eastWestIDUntil15);

		FastVector eastWestIDCurrent = new FastVector(2);
		for (int i = 0; i < 2; i++) {
			eastWestIDCurrent.addElement(i + "");
		}
		eastWestIDCurrentAttribute = new Attribute("eastWestIDCurrent", eastWestIDCurrent);

		attributes.addElement(genderAttribute);
		attributes.addElement(ageAttribute);
		attributes.addElement(prefectureIDCurrentAttribute);
		attributes.addElement(prefectureIDUntil15Attribute);
		attributes.addElement(regionIDCurrentAttribute);
		attributes.addElement(regionIDUntil15Attribute);
		attributes.addElement(eastWestIDCurrentAttribute);
		attributes.addElement(eastWestIDUntil15Attribute);
		return attributes;
	}

}
