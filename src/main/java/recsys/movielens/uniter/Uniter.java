package recsys.movielens.uniter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;

import recsys.movielens.model.imdb.ImdbActorsDataModel;
import recsys.movielens.model.imdb.ImdbActressesDataModel;
import recsys.movielens.model.imdb.ImdbDirectorsDataModel;
import recsys.movielens.model.imdb.ImdbGenresDataModel;
import recsys.movielens.model.imdb.ImdbKeywordsDataModel;
import recsys.movielens.model.movielens.MovielensDataModelLoader;
import recsys.movielens.model.movielens.MovielensDataModel;
import recsys.movielens.model.shared.Movie;

public class Uniter {

	private MovielensDataModel movielensDataModel;
	private ImdbGenresDataModel imdbGenresDataModel;
	private ImdbDirectorsDataModel imdbDirectorsDataModel;
	private ImdbActorsDataModel imdbActorsDataModel;
	private ImdbActressesDataModel imdbActressesDataModel;
	private ImdbKeywordsDataModel imdbKeywordsDataModel;
	private MovieCollection<String> movielensMoviesCollection;
	private Set<String> moviesNotFoundCollective;
	private NameToIdConverter nameToIdConverter = new NameToIdConverter();

	private static final String fieldSeparator = "::";
	private static final String setSeparator = "|";
	private static final String outputFileName = "movielensUnited.txt";
	private PrintWriter writer;
	private MovielensDataModelLoader movielensDataModelLoader;
	int moviesNotFound;
	int moviesFound;
	private Properties prop;

	public static void main(String args[]) throws Exception {
		Date start = new Date();
		Uniter uniter = new Uniter();
		uniter.execute();
		Date end = new Date();
		System.out.println("Processing time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
	}
	
	public Uniter() throws Exception {
		prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		moviesNotFoundCollective = new HashSet<String>();
		movielensMoviesCollection = new MovieCollection<String>();
	}

	void execute() throws Exception {
		createMovielensDataModel();

		uniteGenres();
		
		uniteDirectors();

		uniteActors();

		uniteActresses();

		uniteKeywords();

		System.out.println("number of unique movies not found: " + moviesNotFoundCollective.size());

		writeMovieDataToFile();
	}

	void createMovielensDataModel() throws Exception {
		File dataFile = new File(prop.getProperty("movielens-1m-movies.dat"));
		//loads movielens data from file:
		movielensDataModelLoader = new MovielensDataModelLoader(dataFile);
		movielensDataModel = movielensDataModelLoader.getDataModel();
	}

	private void writeMovieDataToFile() {
		createFile();

		List<Long> itemIDs = movielensDataModel.getItemIDs();
		FastByIDMap<String> names = movielensDataModel.getNames();
		FastByIDMap<Integer> years = movielensDataModel.getYears();
		FastByIDMap<Set<String>> genres = movielensDataModel.getGenres();
		for (Long itemID : itemIDs) {
			String movieName = names.get(itemID);
			Integer movieYear = years.get(itemID);
			Set<String> movieGenres = genres.get(itemID);
			Movie<String> movie = movielensMoviesCollection.getOrCreateMovie(movieName, movieYear);
			Set<String> imdbGenres = movie.getGenres();
			Set<String> imdbDirectors = movie.getDirectors();
			Set<String> imdbActors = movie.getActors();
			Set<String> imdbActresses = movie.getActresses();
			Set<String> imdbKeywords = movie.getKeywords();

			String movieLine = createMovieLine(movieName, itemID, movieYear, movieGenres, imdbGenres, imdbDirectors, imdbActors, imdbActresses, imdbKeywords);
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

	private String createMovieLine(String name, long id, int year, Set<String> genres,
			Set<String> imdbGenres, Set<String> imdbDirectors, Set<String> imdbActors, Set<String> imdbActresses, Set<String> imdbKeywords) {
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

	void uniteActresses() throws Exception {
		System.err.println("Uniting Actresses");
		
		File imdbActressesFile = new File(prop.getProperty("imdb-actresses.list"));
		imdbActressesDataModel = new ImdbActressesDataModel(imdbActressesFile);

		moviesNotFound = 0;
		moviesFound = 0;

		for (Long itemID : movielensDataModel.getItemIDs()) {
			String name = movielensDataModel.getNames().get(itemID);
			Integer year = movielensDataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movielensMoviesCollection.getOrCreateMovie(name, year);
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

	void uniteActors() throws Exception {
		System.err.println("Uniting Actors");
		
		File imdbActorsFile = new File(prop.getProperty("imdb-actors.list"));
		imdbActorsDataModel = new ImdbActorsDataModel(imdbActorsFile);

		moviesNotFound = 0;
		moviesFound = 0;

		for (Long itemID : movielensDataModel.getItemIDs()) {
			String name = movielensDataModel.getNames().get(itemID);
			Integer year = movielensDataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movielensMoviesCollection.getOrCreateMovie(name, year);
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

	void uniteDirectors() throws Exception {
		System.err.println("Uniting Directors");
		
		File imdbDirectorsFile = new File(prop.getProperty("imdb-directors.list"));
		imdbDirectorsDataModel = new ImdbDirectorsDataModel(imdbDirectorsFile);

		moviesNotFound = 0;
		moviesFound = 0;

		imdbDirectorsDataModel.getMovieCollection().getMovie("Toxic Avenger Part III: The Last Temptation of Toxie, The", 1989);

		for (Long itemID : movielensDataModel.getItemIDs()) {
			String name = movielensDataModel.getNames().get(itemID);
			Integer year = movielensDataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movielensMoviesCollection.getOrCreateMovie(name, year);
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

	void uniteGenres() throws Exception {
		System.err.println("Uniting Genres");
		
		File imdbGenresFile = new File(prop.getProperty("imdb-genres.list"));
		imdbGenresDataModel = new ImdbGenresDataModel(imdbGenresFile);

		moviesNotFound = 0;
		moviesFound = 0;

		// imdbGenresDataModel.getMovieCollection().getMovie("Naked Gun 2 1/2: The Smell of Fear, The", 1991);

		for (Long itemID : movielensDataModel.getItemIDs()) {
			String name = movielensDataModel.getNames().get(itemID);
			Integer year = movielensDataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movielensMoviesCollection.getOrCreateMovie(name, year);
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
	
	void uniteKeywords() throws Exception {
		System.err.println("Uniting Keywords");
		
		File imdbKeywordsFile = new File(prop.getProperty("imdb-keywords.list"));
		imdbKeywordsDataModel = new ImdbKeywordsDataModel(imdbKeywordsFile);

		moviesNotFound = 0;
		moviesFound = 0;
		Set<String> allKeywords = getAllKeywords();

		for (Long itemID : movielensDataModel.getItemIDs()) {
			String name = movielensDataModel.getNames().get(itemID);
			Integer year = movielensDataModel.getYears().get(itemID);
			Movie<String> movieLensMovie = movielensMoviesCollection.getOrCreateMovie(name, year);
			Movie<String> imdbMovie = imdbKeywordsDataModel.getMovieCollection().getMovie(name, year);
			if (imdbMovie == null) {
				String msg = "movie: '" + name + "' from year '" + year + "' not found";
				System.err.println(msg);
				moviesNotFound++;
				moviesNotFoundCollective.add(msg);
			} else {
				movieLensMovie.getKeywords().addAll(getNonUniqueKeywords(imdbMovie.getKeywords(), allKeywords));
				moviesFound++;
			}
		}
		System.err.println("Successfully matched keywords for " + moviesFound + " movies");
		System.err.println("Could not match keywords for " + moviesNotFound + " movies");
	}
	
	private Set<String> getAllKeywords() {
		MovieCollection<String> movieCollection = imdbKeywordsDataModel.getMovieCollection();
		Set<Movie<String>> movies = movieCollection.getMovies();
		Set<String> keywords = new HashSet<String>();
		for (Movie<String> movie : movies) {
			keywords.addAll(movie.getKeywords());
		}
		return keywords;
	}

	private Set<String> getNonUniqueKeywords(Set<String> movieKeywords, Set<String> allKeywords) {
		Iterator<String> iterator = movieKeywords.iterator();
		while (iterator.hasNext()) {
			String keyword = iterator.next();
			if (!allKeywords.contains(keyword)) {
				iterator.remove();
			}
		}
		return movieKeywords;
	}

}
