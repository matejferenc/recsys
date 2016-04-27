package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.average.UserAverageRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class UserAverageRecommenderBuilder implements RecommenderBuilder {

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new UserAverageRecommender(dataModel);
		return recommender;
	}

	@Override
	public String getName() {
		return "User average recommender builder";
	}
	
	@Override
	public String getShortName() {
		return "UA";
	}

	@Override
	public void freeReferences() {
	}

}
