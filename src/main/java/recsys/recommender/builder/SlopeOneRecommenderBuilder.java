package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

@SuppressWarnings("deprecation")
public class SlopeOneRecommenderBuilder implements RecommenderBuilder {

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new SlopeOneRecommender(dataModel);
		return recommender;
	}

	@Override
	public String getName() {
		return "Slope One Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "SO";
	}

	@Override
	public void freeReferences() {
		
	}

}
