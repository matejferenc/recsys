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

	@Override
	public String getName() {
		return "Item User Average Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "IUA";
	}

	@Override
	public void freeReferences() {
	}

}
