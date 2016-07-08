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
	public List<RecommendedItem> recommend(Integer userID, int howMany) throws TasteException {
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(Integer userID, int howMany, IDRescorer rescorer) throws TasteException {
		return null;
	}

	@Override
	public Double estimatePreference(Integer userID, Integer itemID) throws TasteException {
		SushiUser user = userModel.get((int) userID);
		Double styleRating = calculateStyleRating(user, (int) itemID);
		Double majorGroupRating = calculateMajorGroupRating(user, (int) itemID);
		Double minorGroupRating = calculateMinorGroupRating(user, (int) itemID);
		Double oilinessRating = calculateOilinessRating(user, (int) itemID);
		Double priceRating = calculatePriceRating(user, (int) itemID);

		int nonZeroRatingCount = getNonZeroRatingCount(styleRating, majorGroupRating, minorGroupRating, oilinessRating, priceRating);
		return (double) ((styleRating + majorGroupRating + minorGroupRating) / nonZeroRatingCount);
	}

	private int getNonZeroRatingCount(Double styleRating, Double majorGroupRating, Double minorGroupRating, Double oilinessRating, Double priceRating) {
		return isNonZero(styleRating) + isNonZero(majorGroupRating) + isNonZero(minorGroupRating) + isNonZero(oilinessRating) + isNonZero(priceRating);
	}

	private int isNonZero(Double rating) {
		return Math.abs(rating) < 0.001 ? 0 : 1;
	}

	private Double calculateMinorGroupRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getStyle(), user.getMinorGroupPreferences());
	}

	private Double calculateMajorGroupRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getStyle(), user.getMajorGroupPreferences());
	}

	private Double calculateStyleRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getStyle(), user.getStylePreferences());
	}
	
	private Double calculateOilinessRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getOiliness(), user.getOilinessPreferences());
	}

	private Double calculatePriceRating(SushiUser user, int itemID) {
		SushiPiece sushiPiece = sushiDataModel.getSushiPiece(itemID);
		return calculatePreference(sushiPiece.getPrice(), user.getPricePreferences());
	}

	private Double calculatePreference(int itemAttribute, SetPreference attributePreferences) {
		Set<Integer> allPropertyIds = attributePreferences.getAllPropertyIds();
		if (allPropertyIds.contains(itemAttribute)) {
			return attributePreferences.getPropertyAverage(itemAttribute);
		} else {
			return 0d;
		}
	}
	
	/**
	 * Linear preference guess.
	 * @param itemValue
	 * @param attributePreferences
	 * @return
	 */
	private Double calculatePreference(Double itemValue, NumericPreference attributePreferences) {
		Double difference = Math.abs(attributePreferences.getPreferredValue() - itemValue);
		if (difference > attributePreferences.getVariance()) {
			return 0d;
		} else {
			return 1 - (1/attributePreferences.getVariance()) * difference;
		}
		
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

}
