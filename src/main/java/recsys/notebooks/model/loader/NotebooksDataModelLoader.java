package recsys.notebooks.model.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Iterator;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.notebooks.model.Notebook;
import recsys.notebooks.model.NotebooksDataModel;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class NotebooksDataModelLoader {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private final NotebooksDataModel notebooksDataModel;

	private Splitter delimiterPattern = Splitter.on(",");

	private NotebooksDataModelLoader() {
		notebooksDataModel = new NotebooksDataModel();
	}

	public NotebooksDataModelLoader(File dataFile) throws Exception {
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
	 * @param style
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
			String hddString = tokens.next();
			Integer hdd = Integer.parseInt(hddString);
			String displayString = tokens.next();
			Integer display = Integer.parseInt(displayString);
			String priceString = tokens.next();
			Integer price = Integer.parseInt(priceString);
			String producerString = tokens.next();
			String ramString = tokens.next();
			Integer ram = Integer.parseInt(ramString);

			Notebook notebook = new Notebook(hdd, display, price, ram, producerString);
			notebooksDataModel.add(itemID, notebook);
		} catch (Exception e) {
			throw new IllegalStateException("something wrong on this line: " + line, e);
		}
	}

	public NotebooksDataModel getNotebooksDataModel() {
		return notebooksDataModel;
	}

}
