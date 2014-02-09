package recsys;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBasedRecommender extends RS {

	public void execute() throws Exception {
		String path = prop.getProperty("movielens-1m-ratings.dat");
		File dataFile = new File(path);
		DataModel model = new FileDataModel(dataFile, "::");
		
		ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
		Recommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		List<RecommendedItem> recommendations = cachingRecommender.recommend(18, 10);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem.getItemID() + ": " + recommendedItem.getValue());
		}

	}

	public static void main(String[] args) throws Exception {
		new ItemBasedRecommender().run();
	}
}
