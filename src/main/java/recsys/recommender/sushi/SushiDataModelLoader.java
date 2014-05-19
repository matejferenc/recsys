package recsys.recommender.sushi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Iterator;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.recommender.sushi.model.SushiDataModel;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class SushiDataModelLoader {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private final SushiDataModel sushiDataModel;

	private Splitter delimiterPattern = Splitter.on("\t");

	private SushiDataModelLoader() {
		sushiDataModel = new SushiDataModel();
	}

	public SushiDataModelLoader(File dataFile) throws Exception {
		this();
		Date start = new Date();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating SushiDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator);
		Date end = new Date();
		log.info("Processing time: " + (end.getTime() - start.getTime()) / 60 + " seconds");
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
			int itemID = Integer.parseInt(itemIDString);
			String japaneseitemTitle = tokens.next();
			String styleString = tokens.next();
			Integer style = Integer.parseInt(styleString);
			String majorGroupString = tokens.next();
			Integer majorGroup = Integer.parseInt(majorGroupString);
			String minorGroupString = tokens.next();
			Integer minorGroup = Integer.parseInt(minorGroupString);
			String oilinessString = tokens.next();
			Double oiliness = Double.parseDouble(oilinessString);
			String eatingFrequencyString = tokens.next();
			Double eatingFrequency = Double.parseDouble(eatingFrequencyString);
			String priceString = tokens.next();
			Double price = Double.parseDouble(priceString);
			String sellingFrequencyString = tokens.next();
			Double sellingFrequency = Double.parseDouble(sellingFrequencyString);

			SushiPiece sushiPiece = new SushiPiece(style, majorGroup, minorGroup, oiliness, eatingFrequency, price, sellingFrequency);
			sushiDataModel.add(itemID, sushiPiece);
		} catch (Exception e) {
			throw new IllegalStateException("something wrong on this line: " + line, e);
		}
	}

	public SushiDataModel getSushiDataModel() {
		return sushiDataModel;
	}

}
