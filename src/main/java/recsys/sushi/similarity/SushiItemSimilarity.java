package recsys.sushi.similarity;

import java.util.Collection;
import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.sushi.evaluator.IncludeProperties;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.SushiPiece;

public class SushiItemSimilarity implements ItemSimilarity {

	private final SushiItemDataModel sushiDataModel;
	private EnumSet<IncludeProperties> includeProperties;

	public SushiItemSimilarity(SushiItemDataModel sushiDataModel, EnumSet<IncludeProperties> includeProperties) {
		this.sushiDataModel = sushiDataModel;
		this.includeProperties = includeProperties;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
		SushiPiece sushiPiece1 = sushiDataModel.getSushiPiece((int) itemID1);
		SushiPiece sushiPiece2 = sushiDataModel.getSushiPiece((int) itemID2);
		double similarity = (includeProperties.contains(IncludeProperties.STYLE) ? calculateStyleSimilarity(sushiPiece1, sushiPiece2) : 0)
				+ (includeProperties.contains(IncludeProperties.MAJOR) ? calculateMajorGroupSimilarity(sushiPiece1, sushiPiece2) : 0)
				+ (includeProperties.contains(IncludeProperties.MINOR) ? calculateMinorGroupSimilarity(sushiPiece1, sushiPiece2) : 0)
				+ (includeProperties.contains(IncludeProperties.OILINESS) ? calculateOilinessSimilarity(sushiPiece1, sushiPiece2) : 0)
				+ (includeProperties.contains(IncludeProperties.PRICE) ? calculatePriceSimilarity(sushiPiece1, sushiPiece2) : 0);
		return similarity/ includeProperties.size();
	}

	private double calculateOilinessSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return 1 - Math.abs(sushiPiece1.getOiliness() - sushiPiece2.getOiliness()) / SushiItemDataModel.MAX_OILINESS;
	}

	private double calculatePriceSimilarity(SushiPiece sushiPiece1, SushiPiece sushiPiece2) {
		return 1 - Math.abs(sushiPiece1.getPrice() - sushiPiece2.getPrice()) / SushiItemDataModel.MAX_PRICE;
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

	@Override
	public String getShortName() {
		return "SIS";
	}

}
