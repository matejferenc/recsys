package recsys.dataset;

import java.io.File;
import java.util.Properties;

import recsys.recommender.movielens.model.movielens.MovieLensEnrichedDataModelLoader;
import recsys.recommender.movielens.model.movielens.MovieLensEnrichedModel;

public class MovieLensEnrichedModelDataset {

	public MovieLensEnrichedModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String enrichedPath = prop.getProperty("movielens-1m-enriched-movies.dat");
		MovieLensEnrichedDataModelLoader movieLensEnrichedDataModelLoader = new MovieLensEnrichedDataModelLoader(new File(enrichedPath));
		MovieLensEnrichedModel movieLensEnrichedModel = movieLensEnrichedDataModelLoader.getMovieLensEnrichedModel();
		return movieLensEnrichedModel;
	}
}
