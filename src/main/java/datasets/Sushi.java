package datasets;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.impl.model.file.MatrixDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class Sushi extends CollaborativeFilteringDataset {
	
	public static void main(String[] args) throws Exception {
		new Sushi().run();
	}

	@Override
	public StatsParams execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		String path = prop.getProperty("sushi3b-5000-10-score");
		File dataFile = new File(path);
		DataModel model = new MatrixDataModel(dataFile, " ", -1);

		StatsParams params = execute(model);
		params.title = "Sushi";
		return params;
	}

}
