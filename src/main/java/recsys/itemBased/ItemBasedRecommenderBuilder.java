package recsys.itemBased;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBasedRecommenderBuilder implements RecommenderBuilder {

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
		Recommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
		return recommender;
	}

}
