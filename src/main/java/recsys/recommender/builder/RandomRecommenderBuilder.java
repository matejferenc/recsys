package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.RandomRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class RandomRecommenderBuilder implements RecommenderBuilder {

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new RandomRecommender(dataModel);
		return recommender;
	}

	@Override
	public String getName() {
		return "Random Recommender Builder";
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getShortName() {
		return "Rand";
	}

}
