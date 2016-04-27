package recsys.similarity.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.similarity.TrueEuclideanDistanceSimilarity;

public class TrueEuclideanDistanceUserSimilarityBuilder implements UserSimilarityBuilder{

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		return new TrueEuclideanDistanceSimilarity(dataModel);
	}

}
