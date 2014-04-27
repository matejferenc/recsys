package recsys.userBased;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;

public class UserBasedEvaluator {

	public static void main(String[] args) throws Exception {
		UserBasedEvaluator e = new UserBasedEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
//		String path = prop.getProperty("movielens-10m-ratings.dat");
		String path = prop.getProperty("movielens-1m-ratings.dat");
//		String path = prop.getProperty("movielens-100k-ratings.data");
//		String path = prop.getProperty("user_ratedmovies-timestamps.dat");
		File dataFile = new File(path);
//		DataModel dataModel = new FileDataModel(dataFile, "\t");
		DataModel dataModel = new FileDataModel(dataFile, "::");

		EuclideanDistanceSimilarity euclideanDistanceSimilarity = new EuclideanDistanceSimilarity(dataModel);
		NearestNUserNeighborhood nearest5UserNeighborhood = new NearestNUserNeighborhood(5, euclideanDistanceSimilarity, dataModel);
		
		UserBasedRecommenderBuilder recommenderBuilder = new UserBasedRecommenderBuilder(euclideanDistanceSimilarity, nearest5UserNeighborhood);

		System.out.println("minimum possible preference: " + dataModel.getMinPreference());
		System.out.println("maximum possible preference: " + dataModel.getMaxPreference());

//		RecommenderEvaluator e = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderEvaluator e = new RMSRecommenderEvaluator();
		double evaluate = e.evaluate(recommenderBuilder, null, dataModel, 0.7, 0.3);
		System.out.println("Score: " + evaluate);
	}
}
