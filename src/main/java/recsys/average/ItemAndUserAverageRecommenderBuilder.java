package recsys.average;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.ItemAndUserAverageRecommender;

public class ItemAndUserAverageRecommenderBuilder implements RecommenderBuilder {

	public ItemAndUserAverageRecommenderBuilder() {
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new ItemAndUserAverageRecommender(dataModel);
		return recommender;
	}

	@Override
	public String getName() {
		return "Item and User Average Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "IAUA";
	}

	@Override
	public void freeReferences() {
	}

}
