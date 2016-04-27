package recsys.similarity.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public interface UserSimilarityBuilder {

	UserSimilarity build(DataModel dataModel) throws TasteException;
}
