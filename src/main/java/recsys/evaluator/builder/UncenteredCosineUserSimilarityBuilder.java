package recsys.evaluator.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UncenteredCosineUserSimilarityBuilder implements UserSimilarityBuilder{

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		return new UncenteredCosineSimilarity(dataModel);
	}

}
