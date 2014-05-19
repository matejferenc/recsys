package recsys.evaluator.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public interface UserNeighborhoodBuilder {

	UserNeighborhood build(UserSimilarity userSimilarity, DataModel dataModel) throws TasteException;
}
