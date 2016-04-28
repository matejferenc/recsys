package recsys.sushi.recommender;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.model.NumericPreference;
import recsys.model.SetPreference;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiPiece;
import recsys.sushi.model.SushiUser;
import recsys.sushi.model.SushiUserModel;

import com.google.common.base.Preconditions;

public class SushiContentBasedRecommender implements Recommender {

	private final DataModel dataModel;
	private final SushiUserModel userModel;
	private final SushiItemDataModel sushiDataModel;

	private static final Logger log = LoggerFactory.getLogger(SushiContentBasedRecommender.class);

	public SushiContentBasedRecommender(DataModel dataModel, SushiUserModel userModel, SushiItemDataModel sushiDataModel) {
		this.dataModel = dataModel;
		this.userModel = userModel;
		this.sushiDataModel = sushiDataModel;

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
		SushiUser user = userModel.get((int) userID);
		double styleRating = calculateStyleRating(user, (int) itemID);
		double majorGroupRating = calculateMajorGroupRating(user, (int) itemID);
		double minorGroupRating = calculateMinorGroupRating(user, (int) itemID);
		double oilinessRating = calculateOilinessRating(user, (int) itemID);
		double priceRating = calculatePriceRating(user, (int) itemID);

		int nonZeroRatingCount = getNonZeroRatingCount(styleRating, majorGroupRating, minorGroupRating, oilinessRating, priceRating);
		return (float) ((styleRating + majorGroupRating + minorGroupRating) / nonZeroRatingCount);
	}

	private int getNonZeroRatingCount(double styleRating, double majorGroupRating, double minorGroupRating, double oilinessRating, double priceRating) {
		return isNonZero(styleRating) + isNonZero(majorGroupRating) + isNonZero(minorGroupRating) + isNonZero(oilinessRating) + isNonZero(priceRating);
	}

	private int isNonZero(double rating) {
		return Math.abs(rating) < 0.001 ? 0 : 1;
	}

	private double calculateMinorGroupRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getStyle(), user.getMinorGroupPreferences());
	}

	private double calculateMajorGroupRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getStyle(), user.getMajorGroupPreferences());
	}

	private double calculateStyleRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getStyle(), user.getStylePreferences());
	}
	
	private double calculateOilinessRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getOiliness(), user.getOilinessPreferences());
	}

	private double calculatePriceRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getPrice(), user.getPricePreferences());
	}

	private double calculatePreference(int itemAttribute, SetPreference attributePreferences) {
		Set<Integer> allPropertyIds = attributePreferences.getAllPropertyIds();
		if (allPropertyIds.contains(itemAttribute)) {
			return attributePreferences.getPropertyAverage(itemAttribute);
		} else {
			return 0;
		}
	}
	
	/**
	 * Linear preference guess.
	 * @param itemValue
	 * @param attributePreferences
	 * @return
	 */
	private double calculatePreference(double itemValue, NumericPreference attributePreferences) {
		double difference = Math.abs(attributePreferences.getPreferredValue() - itemValue);
		if (difference > attributePreferences.getVariance()) {
			return 0;
		} else {
			return 1 - (1/attributePreferences.getVariance()) * difference;
		}
		
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
