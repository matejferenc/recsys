package recsys;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class UserBasedEvaluator {

	public static void main(String[] args) throws Exception {
		UserBasedEvaluator e = new UserBasedEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("movielens-10m-ratings.dat");
		File dataFile = new File(path);
		DataModel dataModel = new FileDataModel(dataFile, "::");

		UserBasedRecommenderBuilder recommenderBuilder = new UserBasedRecommenderBuilder();

		System.out.println("minimum possible preference: " + dataModel.getMinPreference());
		System.out.println("maximum possible preference: " + dataModel.getMaxPreference());

		RecommenderEvaluator e = new AverageAbsoluteDifferenceRecommenderEvaluator();
		double evaluate = e.evaluate(recommenderBuilder, null, dataModel, 0.7, 0.3);
		System.out.println("Score: " + evaluate);
	}
}
