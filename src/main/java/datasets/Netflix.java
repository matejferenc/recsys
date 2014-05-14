package datasets;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.example.netflix.NetflixDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class Netflix extends CollaborativeFilteringDataset {

	public static void main(String[] args) throws Exception {
		new Netflix().execute();
	}
	
	public StatsParams execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		String path = prop.getProperty("netflix-dir");
		File dataFile = new File(path);
		DataModel model = new NetflixDataModel(dataFile, false);
		
		StatsParams params = execute(model);
		params.title = "Netflix";
		return params;
	}

}
