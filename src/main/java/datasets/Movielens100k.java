package datasets;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class Movielens100k extends CollaborativeFilteringDataset {

	public static void main(String[] args) throws Exception {
		new Movielens100k().run();
	}
	
	public StatsParams execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		String path = prop.getProperty("movielens-100k-ratings.data");
		File dataFile = new File(path);
		DataModel model = new FileDataModel(dataFile, "\t");

		StatsParams params = execute(model);
		params.title = "MovieLens 100k";
		return params;
	}

}
