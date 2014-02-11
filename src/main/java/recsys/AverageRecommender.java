package recsys;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.ItemAverageRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class AverageRecommender extends RS {

	public void execute() throws Exception {
		File dataFile = new File("src/main/resources/datasets/movielens/ml-1m/ratings.dat");
		// File dataFile = new File("src/main/resources/datasets/movielens/ml-10M100K/ratings.dat");
		DataModel model = new FileDataModel(dataFile, "::");
		Recommender recommender = new ItemAverageRecommender(model);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		System.out.println("minimum possible preference: " + model.getMinPreference());
		System.out.println("maximum possible preference: " + model.getMaxPreference());

		List<RecommendedItem> recommendations = cachingRecommender.recommend(4, 10);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}
	}

	public static void main(String[] args) throws Exception {
		new AverageRecommender().run();
	}
}
