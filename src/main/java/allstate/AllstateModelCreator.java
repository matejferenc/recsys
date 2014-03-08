package allstate;

import java.io.File;
import java.util.Properties;

import dataModel.AllstateDataModel;

public class AllstateModelCreator {

	public AllstateDataModel createModel() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("allstate-train-csv");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");
		return model;
	}
}
