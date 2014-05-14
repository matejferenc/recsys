package recsys.recommender.movielens.model.imdb;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.recommender.movielens.model.shared.Movie;
import recsys.recommender.movielens.uniter.MovieCollection;

import com.google.common.base.Preconditions;

public class ImdbGenresDataModel {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private MovieCollection<String> movieCollection;

	private ImdbGenresDataModel() {
		movieCollection = new MovieCollection<String>();
	}

	public ImdbGenresDataModel(File dataFile) throws Exception {
		this();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating GenresDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator);
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

			String[] parts = line.split(" \\(.*\\)");
			String itemTitle = parts[0];
			String rest = parts[1];

			String yearString = line.substring(parts[0].length() + 2, parts[0].length() + 6);
			Integer year = null;
			if (yearString.matches("[0-9]{4}")) {
				year = Integer.parseInt(yearString);
			}

			String[] split = rest.split("\t+");
			String genre = split[split.length - 1];

			Movie<String> movie = movieCollection.getOrCreateMovie(itemTitle, year);
			movie.getGenres().add(genre);
		} catch (Exception e) {
			throw new IllegalStateException("something wrong with this line: " + line, e);
		}
	}

	public MovieCollection<String> getMovieCollection() {
		return movieCollection;
	}

	public void setMovieCollection(MovieCollection<String> movieCollection) {
		this.movieCollection = movieCollection;
	}
}
