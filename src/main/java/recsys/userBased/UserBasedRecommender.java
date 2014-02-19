package recsys.userBased;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.RS;

public class UserBasedRecommender extends RS {

	public void execute() throws Exception {
		String path = prop.getProperty("movielens-1m-ratings.dat");
		// String path = prop.getProperty("movielens-10m-ratings.dat");
		File dataFile = new File(path);
		DataModel model = new FileDataModel(dataFile, "::");

		Recommender recommender = new UserBasedRecommenderBuilder().buildRecommender(model);

		System.out.println("minimum possible preference: " + model.getMinPreference());
		System.out.println("maximum possible preference: " + model.getMaxPreference());

		List<RecommendedItem> recommendations = recommender.recommend(1234, 10);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}

	}

	public static void main(String[] args) throws Exception {
		new UserBasedRecommender().run();
	}
}
