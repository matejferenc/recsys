package recsys.recommender.hetrecMovielens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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

	private Splitter delimiterPattern = Splitter.on("\t");

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

	protected void processFile(FileLineIterator dataFileIterator, FastByIDMap<Set<String>> data, FastByIDMap<String> names) {
		log.info("Reading file info...");
		int count = 0;
		while (dataFileIterator.hasNext()) {
			String line = dataFileIterator.next();
			if (!line.isEmpty()) {
				processLine(line, data, names);
				if (++count % 1000000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	protected void processLine(String line, FastByIDMap<Set<String>> data, FastByIDMap<String> names) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		Iterator<String> tokens = delimiterPattern.split(line).iterator();
		String itemIDString = tokens.next();
		String genreString = tokens.next();

		long itemID = readItemIDFromString(itemIDString);

		Set<String> genresSet = new HashSet<String>(Arrays.asList(genreString));
		if (data.containsKey(itemID))
			data.get(itemID).add(genreString);
		else
			data.put(itemID, genresSet);
	}

	protected long readItemIDFromString(String value) {
		return Long.parseLong(value);
	}

	Set<String> getGenres(long itemID) {
		return data.get(itemID);
	}

	boolean intersects(Set<String> set1, Set<String> set2) {
		for (String item : set2) {
			if (set1.contains(item))
				return true;
		}
		return false;
	}
}
