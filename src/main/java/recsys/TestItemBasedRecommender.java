package recsys;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class TestItemBasedRecommender {

	private long startTime;
	private long endTime;

	private void run() throws Exception {
		startTime = new Date().getTime();

		File dataFile = new File("src/main/resources/datasets/movielens/ml-1m/ratings.dat");
		// File dataFile = new File("src/main/resources/datasets/movielens/ml-10M100K/ratings.dat");
		DataModel model = new FileDataModel(dataFile, "::");
		ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
		Recommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		List<RecommendedItem> recommendations = cachingRecommender.recommend(15, 50);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}

		endTime = new Date().getTime();
		System.out.println("cas behu: " + (endTime - startTime) / 1000 + "s");
	}

	public static void main(String[] args) throws Exception {
		new TestItemBasedRecommender().run();
	}
}
