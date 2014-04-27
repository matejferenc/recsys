package recsys.evaluator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class PearsonCorrelationUserSimilarityBuilder implements UserSimilarityBuilder{

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		return new PearsonCorrelationSimilarity(dataModel);
	}

}
