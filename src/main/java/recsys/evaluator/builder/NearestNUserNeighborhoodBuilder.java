package recsys.evaluator.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class NearestNUserNeighborhoodBuilder implements UserNeighborhoodBuilder {
	
	private int n;

	public NearestNUserNeighborhoodBuilder(int n) {
		this.n = n;
	}

	@Override
	public UserNeighborhood build(UserSimilarity userSimilarity, DataModel dataModel) throws TasteException {
		return new NearestNUserNeighborhood(n, userSimilarity, dataModel);
	}

}
