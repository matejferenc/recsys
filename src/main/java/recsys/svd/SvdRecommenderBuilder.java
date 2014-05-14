package recsys.svd;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class SvdRecommenderBuilder implements RecommenderBuilder {

	private Factorizer factorizer;

	public SvdRecommenderBuilder(Factorizer factorizer) {
		this.factorizer = factorizer;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		Recommender recommender = new SVDRecommender(dataModel, factorizer);
		return recommender;
	}

	@Override
	public String getName() {
		return "SVD Recommender Builder with (" + factorizer.getName() + ")";
	}

	@Override
	public void freeReferences() {
		// TODO Auto-generated method stub
		
	}

}
