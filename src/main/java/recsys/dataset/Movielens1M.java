package recsys.dataset;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class Movielens1M {

	public DataModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		String path = prop.getProperty("movielens-1m-ratings.dat");
		File dataFile = new File(path);
		DataModel model = new FileDataModel(dataFile, "::");

		return model;
	}

}
