package recsys.movieLens;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.recommender.movielens.GenresDataModel;

public class MovieLensGenreBasedEvaluator {

	public static void main(String[] args) throws Exception {
		MovieLensGenreBasedEvaluator e = new MovieLensGenreBasedEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("movielens-1m-ratings.dat");
//		String path = prop.getProperty("movielens-10m-ratings.dat");
		File dataFile = new File(path);
//		DataModel dataModel = new FileDataModel(dataFile, "\t");
		DataModel dataModel = new FileDataModel(dataFile, "::");

		String genresPath = prop.getProperty("movielens-1m-movies.dat");
//		String genresPath = prop.getProperty("movielens-10m-movies.dat");
		GenresDataModel genresModel = new GenresDataModel(new File(genresPath));
		MovieLensGenreBasedRecommenderBuilder recommenderBuilder = new MovieLensGenreBasedRecommenderBuilder(genresModel);

		System.out.println("minimum possible preference: " + dataModel.getMinPreference());
		System.out.println("maximum possible preference: " + dataModel.getMaxPreference());

//		RecommenderEvaluator e = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderEvaluator e = new RMSRecommenderEvaluator();
		double evaluate = e.evaluate(recommenderBuilder, null, dataModel, 0.7, 0.3);
		System.out.println("Score: " + evaluate);
	}
}
