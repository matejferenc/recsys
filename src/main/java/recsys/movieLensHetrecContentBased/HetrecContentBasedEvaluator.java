package recsys.movieLensHetrecContentBased;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.hetrecMovielens.GenresDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class HetrecContentBasedEvaluator {

	public static void main(String[] args) throws Exception {
		HetrecContentBasedEvaluator e = new HetrecContentBasedEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("user_ratedmovies-timestamps.dat");
		File dataFile = new File(path);
		DataModel dataModel = new FileDataModel(dataFile, "\t");

		String genresPath = prop.getProperty("movie_genres.dat");
		GenresDataModel genresModel = new GenresDataModel(new File(genresPath));
		HetrecContentBasedRecommenderBuilder recommenderBuilder = new HetrecContentBasedRecommenderBuilder(genresModel);

		System.out.println("minimum possible preference: " + dataModel.getMinPreference());
		System.out.println("maximum possible preference: " + dataModel.getMaxPreference());

		// RecommenderEvaluator e = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderEvaluator e = new RMSRecommenderEvaluator();
		double evaluate = e.evaluate(recommenderBuilder, null, dataModel, 0.7, 0.3);
		System.out.println("Score: " + evaluate);
	}
}
