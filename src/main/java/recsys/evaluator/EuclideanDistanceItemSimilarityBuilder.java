package recsys.evaluator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class EuclideanDistanceItemSimilarityBuilder implements ItemSimilarityBuilder {

	@Override
	public ItemSimilarity build(DataModel dataModel) throws TasteException {
		return new EuclideanDistanceSimilarity(dataModel);
	}

}
