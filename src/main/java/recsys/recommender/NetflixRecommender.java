package recsys.recommender;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.example.netflix.NetflixDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class NetflixRecommender extends RS {

	@Override
	public void execute() throws Exception {
		String path = prop.getProperty("netflix-dir");
		File dataFile = new File(path);
		DataModel model = new NetflixDataModel(dataFile, false);

		UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(15, userSimilarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		long userId = 6;// 6, 7, 8, 10, 25, 33, 42, 59, 79, 83, 87, 94, 97, 116, 126, 130, 131, 133, 134, 142, 149, 158, 164, 168, 169, 177, 178, 183, 188, 189, 192, 195

		recommendForUser(cachingRecommender, 6);
		recommendForUser(cachingRecommender, 7);
		recommendForUser(cachingRecommender, 8);
		recommendForUser(cachingRecommender, 10);
	}

	private void recommendForUser(Recommender cachingRecommender, long userId) throws TasteException {
		List<RecommendedItem> recommendations = cachingRecommender.recommend(userId, 10);
		System.out.println("Recommendation for user: " + userId);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}
	}
	
	public static void main(String[] args) throws Exception {
		new NetflixRecommender().run();
	}

}
