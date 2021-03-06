package recsys.movielens.model.imdb;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.movielens.model.shared.Movie;

import com.google.common.base.Preconditions;

public class ImdbActressesDataModel extends ImdbDataModel {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private int count;

	public ImdbActressesDataModel(File dataFile) throws Exception {
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating ImdbActressesDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator);
	}

	protected void processFile(FileLineIterator dataFileIterator) {
		log.info("Reading file info...");
		count = 0;
		skipLines(dataFileIterator, 241);
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

	/**
	 * processing of one line
	 */
	protected void processLine(String line, FileLineIterator dataFileIterator) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		try {
			String[] parts = line.split("\\t+");
			String actressName = parts[0];
			String movieString = parts[1];

			Movie<String> movie = parseMovie(movieString.trim());
			movie.getActresses().add(actressName);

			while (dataFileIterator.hasNext()) {
				line = dataFileIterator.next();
				if (line.isEmpty()) {
					break;
				} else {
					movie = parseMovie(line.trim());
					movie.getActresses().add(actressName);

					if (++count % 1000000 == 0) {
						log.info("Processed {} lines", count);
					}
				}
			}

		} catch (Exception e) {
			throw new IllegalStateException("something wrong with this line: " + line, e);
		}
	}

}
