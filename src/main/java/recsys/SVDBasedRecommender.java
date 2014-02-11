package recsys;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class SVDBasedRecommender extends RS {

	public void execute() throws Exception {
		File dataFile = new File("src/main/resources/datasets/movielens/ml-1m/ratings.dat");
//		File dataFile = new File("src/main/resources/datasets/movielens/ml-10M100K/ratings.dat");
		DataModel model = new FileDataModel(dataFile, "::");
		Factorizer factorizer = new RatingSGDFactorizer(model, 10, 10);
		Recommender recommender = new SVDRecommender(model, factorizer);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		List<RecommendedItem> recommendations = cachingRecommender.recommend(1234, 10);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}
	}
	
	public static void main(String[] args) throws Exception {
		new SVDBasedRecommender().run();
	}
}
