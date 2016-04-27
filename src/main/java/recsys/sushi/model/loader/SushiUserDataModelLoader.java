package recsys.sushi.model.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Iterator;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recsys.sushi.model.SushiUser;
import recsys.sushi.model.SushiUserModel;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class SushiUserDataModelLoader {

	private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private final SushiUserModel userModel;

	private Splitter delimiterPattern = Splitter.on("\t");

	private SushiUserDataModelLoader() {
		userModel = new SushiUserModel();
	}

	public SushiUserDataModelLoader(File dataFile) throws Exception {
		this();
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

	/**
	 * processing of one line
	 * 
	 * @param line
	 * @param genres
	 * @param names
	 */
	protected void processLine(String line, int lineNumber) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		try {
			Iterator<String> tokens = delimiterPattern.split(line).iterator();
			String userIDString = tokens.next();
			int userID = Integer.parseInt(userIDString);
			String genderString = tokens.next();
			Integer gender = Integer.parseInt(genderString);
			String ageString = tokens.next();
			Integer age = Integer.parseInt(ageString);
			String totalTimeString = tokens.next();
			String prefectureIDUntil15String = tokens.next();
			Integer prefectureIDUntil15 = Integer.parseInt(prefectureIDUntil15String);
			String regionIDUntil15String = tokens.next();
			Integer regionIDUntil15 = Integer.parseInt(regionIDUntil15String);
			String eastWestIDUntil15String = tokens.next();
			Integer eastWestIDUntil15 = Integer.parseInt(eastWestIDUntil15String);
			String prefectureIDCurrentString = tokens.next();
			Integer prefectureIDCurrent = Integer.parseInt(prefectureIDCurrentString);
			String regionIDCurrentString = tokens.next();
			Integer regionIDCurrent = Integer.parseInt(regionIDCurrentString);
			String eastWestIDCurrentString = tokens.next();
			Integer eastWestIDCurrent = Integer.parseInt(eastWestIDCurrentString);
			String attributes5And8EqualString = tokens.next();

//			User user = userModel.getOrCreate(userID);
			// we use artificial IDs for users - the line numbers
			SushiUser user = userModel.getOrCreate(lineNumber);
			user.setAge(age);
			user.setGender(gender);
			user.setPrefectureIDCurrent(prefectureIDCurrent);
			user.setPrefectureIDUntil15(prefectureIDUntil15);
			user.setRegionIDCurrent(regionIDCurrent);
			user.setRegionIDUntil15(regionIDUntil15);
			user.setEastWestIDUntil15(eastWestIDUntil15);
			user.setEastWestIDCurrent(eastWestIDCurrent);
		} catch (Exception e) {
			throw new IllegalStateException("something wrong on this line: " + line, e);
		}
	}

	public SushiUserModel getUserModel() {
		return userModel;
	}

}
