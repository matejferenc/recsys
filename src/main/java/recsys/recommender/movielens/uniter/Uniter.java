package recsys.recommender.movielens.uniter;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;

import recsys.recommender.movielens.model.imdb.ImdbActorsDataModel;
import recsys.recommender.movielens.model.imdb.ImdbActressesDataModel;
import recsys.recommender.movielens.model.imdb.ImdbDirectorsDataModel;
import recsys.recommender.movielens.model.imdb.ImdbGenresDataModel;
import recsys.recommender.movielens.model.movielens.MovieLensDataModelLoader;
import recsys.recommender.movielens.model.movielens.MovielensDataModel;
import recsys.recommender.movielens.model.shared.Movie;

public class Uniter {

	private ImdbGenresDataModel imdbGenresDataModel;
	private MovielensDataModel dataModel;
	private MovieLensMovieModel<String> movieLensMovieModel;
	private ImdbDirectorsDataModel imdbDirectorsDataModel;
	private ImdbActorsDataModel imdbActorsDataModel;
	private ImdbActressesDataModel imdbActressesDataModel;
	private MovieCollection<String> movieCollection;
	private Set<String> moviesNotFoundCollective;
	private NameToIdConverter nameToIdConverter = new NameToIdConverter();

	private static final String fieldSeparator = "::";
	private static final String setSeparator = "|";
	private static final String outputFileName = "movielensUnited.txt";
	private PrintWriter writer;
	private MovieLensDataModelLoader movieLensDataModelLoader;

	public static void main(String args[]) throws Exception {
		Date start = new Date();
		Uniter uniter = new Uniter();
		uniter.execute();
		Date end = new Date();
		System.out.println("Processing time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
	}

	void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		File dataFile = new File(prop.getProperty("movielens-1m-movies.dat"));
		
		//loads movielens data from file:
		movieLensDataModelLoader = new MovieLensDataModelLoader(dataFile);
		dataModel = movieLensDataModelLoader.getDataModel();

		File imdbGenresFile = new File(prop.getProperty("imdb-genres.list"));
		imdbGenresDataModel = new ImdbGenresDataModel(imdbGenresFile);

		File imdbDirectorsFile = new File(prop.getProperty("imdb-directors.list"));
		imdbDirectorsDataModel = new ImdbDirectorsDataModel(imdbDirectorsFile);

		File imdbActorsFile = new File(prop.getProperty("imdb-actors.list"));
		imdbActorsDataModel = new ImdbActorsDataModel(imdbActorsFile);

		File imdbActressesFile = new File(prop.getProperty("imdb-actresses.list"));
		imdbActressesDataModel = new ImdbActressesDataModel(imdbActressesFile);

		movieLensMovieModel = new MovieLensMovieModel<String>(dataModel);
		moviesNotFoundCollective = new HashSet<String>();
		movieCollection = movieLensMovieModel.getMovieCollection();

		uniteGenres();

		uniteDirectors();

		uniteActors();

		uniteActresses();

		System.out.println(moviesNotFoundCollective.size());

		writeMovieDataToFile();
	}

	private void writeMovieDataToFile() {
		createFile();

		MovielensDataModel movielensDataModel = movieLensMovieModel.getMovielensDataModel();
		MovieCollection<String> movieCollection = movieLensMovieModel.getMovieCollection();
		List<Long> itemIDs = movielensDataModel.getItemIDs();
		FastByIDMap<String> names = movielensDataModel.getNames();
		FastByIDMap<Integer> years = movielensDataModel.getYears();
		FastByIDMap<Set<String>> genres = movielensDataModel.getGenres();
		for (Long itemID : itemIDs) {
			String movieName = names.get(itemID);
			Integer movieYear = years.get(itemID);
			Set<String> movieGenres = genres.get(itemID);
			Movie<String> movie = movieCollection.getMovie(movieName, movieYear);
			Set<String> imdbGenres = movie.getGenres();
			Set<String> imdbDirectors = movie.getDirectors();
			Set<String> imdbActors = movie.getActors();
			Set<String> imdbActresses = movie.getActresses();

			String movieLine = createMovieLine(movieName, itemID, movieYear, movieGenres, imdbGenres, imdbDirectors, imdbActors, imdbActresses);
			writeToFile(movieLine);
		}

		closeFile();
	}

	private void closeFile() {
		writer.close();
	}

	private void createFile() {
		try {
			writer = new PrintWriter(outputFileName, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(String movieLine) {
		writer.println(movieLine);
	}

	private String createMovieLine(String name, long id, int year, Set<String> genres, Set<String> imdbGenres, Set<String> imdbDirectors, Set<String> imdbActors, Set<String> imdbActresses) {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append(fieldSeparator);
		sb.append(name);
		sb.append(fieldSeparator);
		sb.append(year);
		sb.append(fieldSeparator);
		sb.append(createSetString(genres));
		sb.append(fieldSeparator);
		sb.append(createSetString(nameToIdConverter.convertGenresToIds(imdbGenres)));
		sb.append(fieldSeparator);
		sb.append(createSetString(nameToIdConverter.convertDirectorsToIds(imdbDirectors)));
		sb.append(fieldSeparator);
		sb.append(createSetString(nameToIdConverter.convertActorsToIds(imdbActors)));
		sb.append(fieldSeparator);
		sb.append(createSetString(nameToIdConverter.convertActressesToIds(imdbActresses)));
		return sb.toString();
	}

	private <T> String createSetString(Set<T> ids) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T id : ids) {
			if (!first) {
				sb.append(setSeparator);
			}
			first = false;
			sb.append(id);
		}
		return sb.toString();
	}

	private void uniteActresses() {
		System.err.println("Uniting Actresses");

		int moviesNotFound = 0;
		int moviesFound = 0;

		for (Long itemID : dataModel.getItemIDs()) {
			String name = dataModel.getNames().get(itemID);
			Integer year = dataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movieCollection.getOrCreateMovie(name, year);
			Movie<String> imdbMovie = imdbActressesDataModel.getMovieCollection().getMovie(name, year);
			if (imdbMovie == null) {
				String msg = "movie: '" + name + "' from year '" + year + "' not found";
				System.err.println(msg);
				moviesNotFound++;
				moviesNotFoundCollective.add(msg);
			} else {
				movieLensMovie.getActresses().addAll(imdbMovie.getActresses());
				moviesFound++;
			}
		}
		System.err.println("Successfully matched actresses for " + moviesFound + " movies");
		System.err.println("Could not match actresses for " + moviesNotFound + " movies");
	}

	private void uniteActors() {
		System.err.println("Uniting Actors");

		int moviesNotFound = 0;
		int moviesFound = 0;

		for (Long itemID : dataModel.getItemIDs()) {
			String name = dataModel.getNames().get(itemID);
			Integer year = dataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movieCollection.getOrCreateMovie(name, year);
			Movie<String> imdbMovie = imdbActorsDataModel.getMovieCollection().getMovie(name, year);
			if (imdbMovie == null) {
				String msg = "movie: '" + name + "' from year '" + year + "' not found";
				System.err.println(msg);
				moviesNotFound++;
				moviesNotFoundCollective.add(msg);
			} else {
				movieLensMovie.getActors().addAll(imdbMovie.getActors());
				moviesFound++;
			}
		}
		System.err.println("Successfully matched actors for " + moviesFound + " movies");
		System.err.println("Could not match actors for " + moviesNotFound + " movies");
	}

	private void uniteDirectors() {
		System.err.println("Uniting Directors");

		int moviesNotFound = 0;
		int moviesFound = 0;

		imdbDirectorsDataModel.getMovieCollection().getMovie("Toxic Avenger Part III: The Last Temptation of Toxie, The", 1989);

		for (Long itemID : dataModel.getItemIDs()) {
			String name = dataModel.getNames().get(itemID);
			Integer year = dataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movieCollection.getOrCreateMovie(name, year);
			Movie<String> imdbMovie = imdbDirectorsDataModel.getMovieCollection().getMovie(name, year);
			if (imdbMovie == null) {
				String msg = "movie: '" + name + "' from year '" + year + "' not found";
				System.err.println(msg);
				moviesNotFound++;
				moviesNotFoundCollective.add(msg);
			} else {
				movieLensMovie.getDirectors().addAll(imdbMovie.getDirectors());
				moviesFound++;
			}
		}
		System.err.println("Successfully matched directors for " + moviesFound + " movies");
		System.err.println("Could not match directors for " + moviesNotFound + " movies");
	}

	void uniteGenres() {
		System.err.println("Uniting Genres");

		int moviesNotFound = 0;
		int moviesFound = 0;

		// imdbGenresDataModel.getMovieCollection().getMovie("Naked Gun 2 1/2: The Smell of Fear, The", 1991);

		for (Long itemID : dataModel.getItemIDs()) {
			String name = dataModel.getNames().get(itemID);
			Integer year = dataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movieCollection.getOrCreateMovie(name, year);
			Movie<String> imdbMovie = imdbGenresDataModel.getMovieCollection().getMovie(name, year);
			if (imdbMovie == null) {
				String msg = "movie: '" + name + "' from year '" + year + "' not found";
				System.err.println(msg);
				moviesNotFound++;
				moviesNotFoundCollective.add(msg);
			} else {
				movieLensMovie.getGenres().addAll(imdbMovie.getGenres());
				moviesFound++;
			}
		}
		System.err.println("Successfully matched genres for " + moviesFound + " movies");
		System.err.println("Could not match genres for " + moviesNotFound + " movies");
	}

}
