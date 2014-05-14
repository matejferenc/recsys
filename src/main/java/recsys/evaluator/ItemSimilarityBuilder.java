package recsys.evaluator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public interface ItemSimilarityBuilder {

	ItemSimilarity build(DataModel dataModel) throws TasteException;
}
