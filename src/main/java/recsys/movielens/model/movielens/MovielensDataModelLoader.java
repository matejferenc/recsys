package recsys.movielens.model.movielens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class MovielensDataModelLoader {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private Splitter delimiterPattern = Splitter.on("::");
	
	private MovielensDataModel movielensDataModel;
	
	public MovielensDataModelLoader() {
		movielensDataModel = new MovielensDataModel();
	}

	public MovielensDataModelLoader(File dataFile) throws Exception {
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
	 */
	protected void processLine(String line) {
		if (line.equals("3309::Dog's Life, A (1920)::Comedy")) {
			boolean stop = true;
		}
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}
		
		try {
			Iterator<String> tokens = delimiterPattern.split(line).iterator();
			String itemIDString = tokens.next();
			String itemTitleWithYearString = tokens.next();
			String itemTitle = itemTitleWithYearString.substring(0, itemTitleWithYearString.length() - 7);
			String itemYearString = itemTitleWithYearString.substring(itemTitleWithYearString.length() - 5, itemTitleWithYearString.length() - 1);
			Integer itemYear = Integer.parseInt(itemYearString);
			String styleString = tokens.next();

			long itemID = readItemIDFromString(itemIDString);

			List<String> styleList = readGenresFromString(styleString);
			Set<String> styleSet = new HashSet<String>(styleList);
			addMovieToDataModel(itemTitle, itemYear, itemID, styleSet);
		} catch (Exception e) {
			throw new IllegalStateException("something wrong on this line: " + line, e);
		}
	}

	private void addMovieToDataModel(String itemTitle, Integer itemYear, long itemID, Set<String> styleSet) {
		movielensDataModel.getGenres().put(itemID, styleSet);
		movielensDataModel.getNames().put(itemID, itemTitle);
		movielensDataModel.getYears().put(itemID, itemYear);
		movielensDataModel.getItemIDs().add(itemID);
	}

	private List<String> readGenresFromString(String styleString) {
		String[] split = styleString.split("\\|");
		return Arrays.asList(split);
	}

	protected long readItemIDFromString(String value) {
		return Long.parseLong(value);
	}

	public MovielensDataModel getDataModel() {
		return movielensDataModel;
	}

}
