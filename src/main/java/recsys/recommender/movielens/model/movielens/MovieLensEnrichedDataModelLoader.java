package recsys.recommender.movielens.model.movielens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.recommender.movielens.model.shared.Movie;
import recsys.recommender.movielens.model.shared.MovieLensEnrichedModel;
import recsys.recommender.movielens.uniter.MovieLensMovieModel;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class MovieLensEnrichedDataModelLoader {

	private static final String SET_DELIMITER = "\\|";

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private MovieLensEnrichedModel movieLensEnrichedModel;

	private Splitter delimiterPattern = Splitter.on("::");

	private MovieLensEnrichedDataModelLoader() {
		movieLensEnrichedModel = new MovieLensEnrichedModel();
	}

	public MovieLensEnrichedDataModelLoader(File dataFile) throws Exception {
		this();
		Date start = new Date();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating MovieLensEnrichedDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator);
		Date end = new Date();
		System.out.println("Processing time: " + (end.getTime() - start.getTime()) / 60 + " seconds");
	}

	protected void processFile(FileLineIterator dataFileIterator) {
		log.info("Reading file info...");
		int count = 0;
		while (dataFileIterator.hasNext()) {
			String line = dataFileIterator.next();
			if (!line.isEmpty()) {
				processLine(line);
				if (++count % 1000000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	/**
	 * processing of one line
	 * 
	 * @param line
	 * @param genres
	 * @param names
	 */
	protected void processLine(String line) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		try {
			Iterator<String> tokens = delimiterPattern.split(line).iterator();
			String itemIDString = tokens.next();
			long itemID = Long.parseLong(itemIDString);
			String itemTitle = tokens.next();
			String itemYearString = tokens.next();
			Integer itemYear = Integer.parseInt(itemYearString);
			String genresString = tokens.next();
			Set<String> genresSet = readGenresFromString(genresString);
			String imdbGenresString = tokens.next();
			Set<Integer> imdbGenresSet = readIdsFromString(imdbGenresString);
			String imdbDirectorsString = tokens.next();
			Set<Integer> imdbDirectorsSet = readIdsFromString(imdbDirectorsString);
			String imdbActorsString = tokens.next();
			Set<Integer> imdbActorsSet = readIdsFromString(imdbActorsString);
			String imdbActressesString = tokens.next();
			Set<Integer> imdbActressesSet = readIdsFromString(imdbActressesString);

			movieLensEnrichedModel.addEnrichedMovie(itemTitle, itemID, itemYear, genresSet, imdbGenresSet, imdbDirectorsSet, imdbActorsSet, imdbActressesSet);
		} catch (Exception e) {
			throw new IllegalStateException("something wrong on this line: " + line, e);
		}
	}

	private Set<String> readGenresFromString(String genresString) {
		String[] split = genresString.split(SET_DELIMITER);
		return new HashSet<String>(Arrays.asList(split));
	}

	private Set<Integer> readIdsFromString(String string) {
		Set<Integer> ids = new HashSet<Integer>();
		if (string.equals(""))
			return ids;
		String[] split = string.split(SET_DELIMITER);
		for (int i = 0; i < split.length; i++) {
			ids.add(Integer.parseInt(split[i]));
		}
		return ids;
	}

	protected long readItemIDFromString(String value) {
		return Long.parseLong(value);
	}

	public MovieLensEnrichedModel getMovieLensEnrichedModel() {
		return movieLensEnrichedModel;
	}

}
