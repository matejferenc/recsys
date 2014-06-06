package recsys.evaluator.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class PearsonCorrelationItemSimilarityBuilder implements ItemSimilarityBuilder {

	@Override
	public ItemSimilarity build(DataModel dataModel) throws TasteException {
		return new PearsonCorrelationSimilarity(dataModel);
	}

}
