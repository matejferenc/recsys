package recsys.movielens.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class GenresDataModel {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private FastByIDMap<Set<String>> data;

	private FastByIDMap<String> names;

	private Splitter delimiterPattern = Splitter.on("::");

	private GenresDataModel() {
		data = new FastByIDMap<Set<String>>();
		names = new FastByIDMap<String>();
	}

	public GenresDataModel(File dataFile) throws Exception {
		this();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating GenresDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator, data, names);
	}

	protected void processFile(FileLineIterator dataFileIterator, FastByIDMap<Set<String>> genres, FastByIDMap<String> names) {
		log.info("Reading file info...");
		int count = 0;
		while (dataFileIterator.hasNext()) {
			String line = dataFileIterator.next();
			if (!line.isEmpty()) {
				processLine(line, genres, names);
				if (++count % 1000000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	/**
	 * processing of one line
	 * @param line
	 * @param genres
	 * @param names
	 */
	protected void processLine(String line, FastByIDMap<Set<String>> genres, FastByIDMap<String> names) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		Iterator<String> tokens = delimiterPattern.split(line).iterator();
		String itemIDString = tokens.next();
		String itemTitleString = tokens.next();
		String genresString = tokens.next();

		long itemID = readItemIDFromString(itemIDString);

		List<String> genresList = readGenresFromString(genresString);
		Set<String> genresSet = new HashSet<String>(genresList);
		genres.put(itemID, genresSet);

		names.put(itemID, itemTitleString);

	}

	private List<String> readGenresFromString(String genresString) {
		String[] split = genresString.split("\\|");
		return Arrays.asList(split);
	}

	protected long readItemIDFromString(String value) {
		return Long.parseLong(value);
	}

	public Set<String> getGenres(long itemID) {
		return data.get(itemID);
	}

	public boolean intersects(Set<String> set1, Set<String> set2) {
		for (String item : set2) {
			if (set1.contains(item))
				return true;
		}
		return false;
	}
}
