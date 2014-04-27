package recsys.svd;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class SvdRecommenderBuilder implements RecommenderBuilder {

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
//		Factorizer factorizer = new SVDPlusPlusFactorizer(dataModel, 0, 10);
		Factorizer factorizer = new ALSWRFactorizer(dataModel, 3, 0.065, 10);
		Recommender recommender = new SVDRecommender(dataModel, factorizer);
		return recommender;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
