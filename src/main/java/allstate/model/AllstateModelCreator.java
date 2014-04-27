package allstate.model;

import java.io.File;
import java.util.Properties;

public class AllstateModelCreator {

	public AllstateDataModel createModel() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("allstate-train-csv-unix");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");
		return model;
	}
}
