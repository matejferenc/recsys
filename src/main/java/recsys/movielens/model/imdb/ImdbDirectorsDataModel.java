package recsys.movielens.model.imdb;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.movielens.model.shared.Movie;
import recsys.movielens.uniter.MovieCollection;

import com.google.common.base.Preconditions;

public class ImdbDirectorsDataModel {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private MovieCollection<String> movieCollection;

	private int count;

	private ImdbDirectorsDataModel() {
		movieCollection = new MovieCollection<String>();
	}

	public ImdbDirectorsDataModel(File dataFile) throws Exception {
		this();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating ImdbDirectorsDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator);
	}

	protected void processFile(FileLineIterator dataFileIterator) {
		log.info("Reading file info...");
		count = 0;
		skipLines(dataFileIterator, 235);
		while (dataFileIterator.hasNext()) {
			String line = dataFileIterator.next();
			if(isAtEnd(line))
				break;
			if (!line.isEmpty()) {
				processLine(line, dataFileIterator);
				if (++count % 1000000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	private boolean isAtEnd(String line) {
		return "-----------------------------------------------------------------------------".equals(line);
	}

	private void skipLines(FileLineIterator dataFileIterator, int lines) {
		for (int j = 0; j < lines; j++) {
			if (dataFileIterator.hasNext()) {
				dataFileIterator.next();
			}
		}
	}

	/**
	 * processing of one line
	 * 
	 * @param line
	 * @param dataFileIterator
	 * @param genres
	 * @param names
	 */
	protected void processLine(String line, FileLineIterator dataFileIterator) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		try {
			String[] parts = line.split("\\t+");
			String directorName = parts[0];
			String movieString = parts[1];

			Movie<String> movie = parseMovie(movieString.trim());
			movie.getDirectors().add(directorName);

			while (dataFileIterator.hasNext()) {
				line = dataFileIterator.next();
				if (line.isEmpty()) {
					break;
				} else {
					movie = parseMovie(line.trim());
					movie.getDirectors().add(directorName);

					if (++count % 1000000 == 0) {
						log.info("Processed {} lines", count);
					}
				}
			}

		} catch (Exception e) {
			throw new IllegalStateException("something wrong with this line: " + line, e);
		}
	}

	private Movie<String> parseMovie(String movieString) {
		String movieName = parseMovieName(movieString);
		String movieYearString = parseMovieYear(movieString, movieName);

		Integer year = null;
		if (movieYearString.matches("[0-9]{4}")) {
			year = Integer.parseInt(movieYearString);
		}

		Movie<String> movie = movieCollection.getOrCreateMovie(movieName, year);
		return movie;
	}

	private String parseMovieYear(String movieString, String movieName) {
		String yearString = movieString.substring(movieName.length() + 2, movieName.length() + 6);
		return yearString;
	}

	private String parseMovieName(String movieString) {
		String[] split = movieString.trim().split(" \\(.*\\)");
		return split[0];
	}

	public MovieCollection<String> getMovieCollection() {
		return movieCollection;
	}

	public void setMovieCollection(MovieCollection<String> movieCollection) {
		this.movieCollection = movieCollection;
	}
}
