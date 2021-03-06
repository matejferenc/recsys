package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.average.AverageRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class AverageRecommenderBuilder implements RecommenderBuilder {

	public AverageRecommenderBuilder() {
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new AverageRecommender(dataModel);
		return recommender;
	}

	@Override
	public String getName() {
		return "Average";
	}

	@Override
	public void freeReferences() {
	}

	@Override
	public String getShortName() {
		return "a";
	}

}
