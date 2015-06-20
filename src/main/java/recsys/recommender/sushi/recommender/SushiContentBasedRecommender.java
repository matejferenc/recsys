package recsys.recommender.sushi.recommender;

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

import recsys.recommender.model.SetPreference;
import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.model.SushiPiece;
import recsys.recommender.sushi.model.SushiUser;
import recsys.recommender.sushi.model.SushiUserModel;

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

		int nonZeroRatingCount = getNonZeroRatingCount(styleRating, majorGroupRating, minorGroupRating);
		return (float) ((styleRating + majorGroupRating + minorGroupRating) / nonZeroRatingCount);
	}

	private int getNonZeroRatingCount(double genresRating, double directorsRating, double actorsRating) {
		return isNonZero(genresRating) + isNonZero(directorsRating) + isNonZero(actorsRating);
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

	private double calculatePreference(int itemAttribute, SetPreference attributePreferences) {
		Set<Integer> allPropertyIds = attributePreferences.getAllPropertyIds();
		if (allPropertyIds.contains(itemAttribute)) {
			return attributePreferences.getPropertyAverage(itemAttribute);
		} else
			return 0;
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
