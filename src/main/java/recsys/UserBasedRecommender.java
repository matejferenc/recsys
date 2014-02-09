package recsys;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBasedRecommender extends RS{

	public void execute() throws Exception {
		String path = prop.getProperty("movielens-1m-ratings.dat");
		File dataFile = new File(path);
		DataModel model = new FileDataModel(dataFile, "::");
		
		UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, userSimilarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		List<RecommendedItem> recommendations = cachingRecommender.recommend(1234, 10);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}

	}

	public static void main(String[] args) throws Exception {
		new UserBasedRecommender().run();
	}
}
