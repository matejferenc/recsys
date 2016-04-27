package recsys.movielens.dataset;

import java.io.File;
import java.util.Properties;

import recsys.movielens.model.movielens.MovieLensEnrichedDataModelLoader;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;

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
