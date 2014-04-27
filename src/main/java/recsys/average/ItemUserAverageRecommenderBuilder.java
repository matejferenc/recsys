package recsys.average;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.ItemUserAverageRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class ItemUserAverageRecommenderBuilder implements RecommenderBuilder {

	public ItemUserAverageRecommenderBuilder() {
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new ItemUserAverageRecommender(dataModel);
		return recommender;
	}

}
