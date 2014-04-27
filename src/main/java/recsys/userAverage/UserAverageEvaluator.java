package recsys.userAverage;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class UserAverageEvaluator {

	public static void main(String[] args) throws Exception {
		UserAverageEvaluator e = new UserAverageEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
//		String path = prop.getProperty("movielens-10m-ratings.dat");
		String path = prop.getProperty("movielens-1m-ratings.dat");
//		String path = prop.getProperty("movielens-100k-ratings.data");
		File dataFile = new File(path);
//		DataModel dataModel = new FileDataModel(dataFile, "\t");
		DataModel dataModel = new FileDataModel(dataFile, "::");

		UserAverageRecommenderBuilder recommenderBuilder = new UserAverageRecommenderBuilder();

		System.out.println("minimum possible preference: " + dataModel.getMinPreference());
		System.out.println("maximum possible preference: " + dataModel.getMaxPreference());

//		RecommenderEvaluator e = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderEvaluator e = new RMSRecommenderEvaluator();
		double evaluate = e.evaluate(recommenderBuilder, null, dataModel, 0.7, 0.3);
		System.out.println("Score: " + evaluate);
	}
}