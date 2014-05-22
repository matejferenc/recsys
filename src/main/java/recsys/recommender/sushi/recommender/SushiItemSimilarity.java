package recsys.recommender.sushi.recommender;

import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.recommender.sushi.SushiPiece;
import recsys.recommender.sushi.model.SushiDataModel;

public class SushiItemSimilarity implements ItemSimilarity {

	private final SushiDataModel sushiDataModel;

	public SushiItemSimilarity(SushiDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
		SushiPiece sushiPiece1 = sushiDataModel.getSushiPiece((int) itemID1);
		SushiPiece sushiPiece2 = sushiDataModel.getSushiPiece((int) itemID2);
		double styleSimilarity = calculateStyleSimilarity(sushiPiece1, sushiPiece2);
		double majorGroupSimilarity = calculateMajorGroupSimilarity(sushiPiece1, sushiPiece2);
		double minorGroupSimilarity = calculateMinorGroupSimilarity(sushiPiece1, sushiPiece2);
		double oilinessSimilarity = calculateOilinessSimilarity(sushiPiece1, sushiPiece2);
		double priceSimilarity = calculatePriceSimilarity(sushiPiece1, sushiPiece2);
		return (styleSimilarity + majorGroupSimilarity + minorGroupSimilarity + oilinessSimilarity + priceSimilarity) / 5;
	}

	private double calculateOilinessSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return 1 - Math.abs(sushiPiece1.getOiliness() - sushiPiece2.getOiliness()) / SushiDataModel.MAX_OILINESS;
	}

	private double calculatePriceSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return 1 - Math.abs(sushiPiece1.getPrice() - sushiPiece2.getPrice()) / SushiDataModel.MAX_PRICE;
	}

	private double calculateMajorGroupSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return sushiPiece1.getMajorGroup() == sushiPiece2.getMajorGroup() ? 1 : 0;
	}

	private double calculateStyleSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return sushiPiece1.getStyle() == sushiPiece2.getStyle() ? 1 : 0;
	}

	private double calculateMinorGroupSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return sushiPiece1.getMinorGroup() == sushiPiece2.getMinorGroup() ? 1 : 0;
	}

	@Override
	public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
		int length = itemID2s.length;
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = itemSimilarity(itemID1, itemID2s[i]);
		}
		return result;
	}

	@Override
	public long[] allSimilarItemIDs(long itemID) throws TasteException {
		return null;
	}

	@Override
	public String getName() {
		return "Sushi Item Similarity";
	}

}
