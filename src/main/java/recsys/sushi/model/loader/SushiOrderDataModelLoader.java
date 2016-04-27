package recsys.sushi.model.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.sushi.model.OrderModel;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class SushiOrderDataModelLoader {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private final OrderModel orderModel;

	private Splitter delimiterPattern = Splitter.on(" ");

	public SushiOrderDataModelLoader() throws Exception {
		orderModel = new OrderModel();
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("sushi3b-5000-10-order");
		File dataFile = new File(path);
		
		Date start = new Date();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating SushiUserModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator);
		Date end = new Date();
		log.info("Processing time: " + (end.getTime() - start.getTime()) / 60 + " seconds");
	}

	protected void processFile(FileLineIterator dataFileIterator) {
		log.info("Reading file info...");
		// skip the first line:
		if(dataFileIterator.hasNext()){
			dataFileIterator.next();
		}
		int count = 0;
		while (dataFileIterator.hasNext()) {
			String line = dataFileIterator.next();
			if (!line.isEmpty()) {
				count++;
				processLine(line, count);
				if (count % 1000000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	@SuppressWarnings("unused")
	protected void processLine(String line, int lineNumber) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		try {
			Iterator<String> tokens = delimiterPattern.split(line).iterator();
			String zeroString = tokens.next();
			String tenString = tokens.next();
			
			List<Integer> order = orderModel.getOrCreate(lineNumber);
			for (int i = 0; i < 10; i++) {
				String sushiIdString = tokens.next();
				Integer sushiId = Integer.parseInt(sushiIdString);
				order.add(sushiId);
			}

		} catch (Exception e) {
			throw new IllegalStateException("something wrong on this line: " + line, e);
		}
	}

	public OrderModel getOrderModel() {
		return orderModel;
	}

}
