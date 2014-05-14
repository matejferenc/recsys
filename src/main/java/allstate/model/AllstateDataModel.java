package allstate.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.file.MatrixDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

public class AllstateDataModel {

	public Map<Long, List<Record>> dataset = new HashMap<Long, List<Record>>();

	private String replaceWithNull;
	private File dataFile;
	private char delimiter;
	private Splitter delimiterPattern;
	private static final Logger log = LoggerFactory.getLogger(MatrixDataModel.class);

	public AllstateDataModel(Map<Long, List<Record>> dataset) {
		this.dataset = dataset;
	}

	public AllstateDataModel shallowCopy() {
		Map<Long, List<Record>> copy = new HashMap<Long, List<Record>>();
		for (Entry<Long, List<Record>> entry : dataset.entrySet()) {
			List<Record> records = entry.getValue();
			List<Record> recordsCopy = new ArrayList<>();
			for (Record record : records) {
				recordsCopy.add(record);
			}
			copy.put(entry.getKey(), recordsCopy);
		}
		return new AllstateDataModel(copy);
	}

	public AllstateDataModel(File dataFile, String delimiterRegex, String replaceWithNull) throws IOException {

		this.replaceWithNull = replaceWithNull;
		this.dataFile = Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");

		log.info("Creating AllstateDataModel for file {}", dataFile);

		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		String firstLine = iterator.peek();
		while (firstLine.isEmpty()) {
			iterator.next();
			firstLine = iterator.peek();
		}
		Closeables.close(iterator, true);

		if (delimiterRegex == null) {
			delimiter = FileDataModel.determineDelimiter(firstLine);
			delimiterPattern = Splitter.on(delimiter);
		} else {
			delimiter = '\0';
			delimiterPattern = Splitter.onPattern(delimiterRegex);
			if (!delimiterPattern.split(firstLine).iterator().hasNext()) {
				throw new IllegalArgumentException("Did not find a delimiter(pattern) in first line");
			}
		}
		List<String> firstLineSplit = Lists.newArrayList();
		for (String token : delimiterPattern.split(firstLine)) {
			firstLineSplit.add(token);
		}

		reload();
	}

	protected void reload() {
		buildModel();
	}

	private void buildModel() {
		FileLineIterator iterator;
		try {
			iterator = new FileLineIterator(dataFile, false);
			processFile(iterator);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void processFile(FileLineIterator dataOrUpdateFileIterator) {
		log.info("Reading file info...");
		int count = 0;

		String firstLine = dataOrUpdateFileIterator.next();

		while (dataOrUpdateFileIterator.hasNext()) {
			String line = dataOrUpdateFileIterator.next();
			if (!line.isEmpty()) {
				processLine(line);
				if (++count % 10000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	protected void processLine(String line) {
		Iterator<String> tokens = delimiterPattern.split(line).iterator();
		Record record = new Record();

		Long userID = Long.parseLong(tokens.next());

		while (tokens.hasNext()) {
			String next = tokens.next();
			if (replaceWithNull.equals(next)) {
				next = null;
			}
			record.add(next);
		}

		addRecordToDataset(record, userID);

	}

	private void addRecordToDataset(Record record, Long userID) {
		if (dataset.containsKey(userID)) {
			dataset.get(userID).add(record);
		} else {
			ArrayList<Record> records = new ArrayList<>();
			records.add(record);
			dataset.put(userID, records);
		}
	}

}
