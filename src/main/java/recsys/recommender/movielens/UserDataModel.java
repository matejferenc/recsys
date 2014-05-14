package recsys.recommender.movielens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class UserDataModel {

	private static final Logger log = LoggerFactory.getLogger(UserDataModel.class);

	private static final char COMMENT_CHAR = '#';

	private FastByIDMap<UserData> data;

	private Splitter delimiterPattern = Splitter.on("::");

	private UserDataModel() {
		data = new FastByIDMap<UserData>();
	}

	public UserDataModel(File dataFile) throws Exception {
		this();
		Preconditions.checkNotNull(dataFile.getAbsoluteFile());
		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}
		Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		log.info("Creating GenresDataModel for file {}", dataFile);
		FileLineIterator iterator = new FileLineIterator(dataFile, false);
		processFile(iterator, data);
	}

	protected void processFile(FileLineIterator dataFileIterator, FastByIDMap<UserData> data) {
		log.info("Reading file info...");
		int count = 0;
		while (dataFileIterator.hasNext()) {
			String line = dataFileIterator.next();
			if (!line.isEmpty()) {
				processLine(line, data);
				if (++count % 1000000 == 0) {
					log.info("Processed {} lines", count);
				}
			}
		}
		log.info("Read lines: {}", count);
	}

	protected void processLine(String line, FastByIDMap<UserData> data) {
		// Ignore empty lines and comments
		if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
			return;
		}

		Iterator<String> tokens = delimiterPattern.split(line).iterator();
		String itemIDString = tokens.next();
		String sexString = tokens.next();
		char sex = sexString.charAt(0);
		String ageCategoryString = tokens.next();
		String occupationString = tokens.next();

		long itemID = readItemIDFromString(itemIDString);

		data.put(itemID, new UserData(sex, ageCategoryString, occupationString));
	}

	protected long readItemIDFromString(String value) {
		return Long.parseLong(value);
	}

	UserData getUserData(long itemID) {
		return data.get(itemID);
	}

	public class UserData {

		private char sex;

		private String ageCategory;

		private String occupation;

		public UserData(char sex, String ageCategory, String occupation) {
			this.sex = sex;
			this.ageCategory = ageCategory;
			this.occupation = occupation;
		}

		public char getSex() {
			return sex;
		}

		public void setSex(char sex) {
			this.sex = sex;
		}

		public String getAgeCategory() {
			return ageCategory;
		}

		public void setAgeCategory(String ageCategory) {
			this.ageCategory = ageCategory;
		}

		public String getOccupation() {
			return occupation;
		}

		public void setOccupation(String occupation) {
			this.occupation = occupation;
		}
	}
}
