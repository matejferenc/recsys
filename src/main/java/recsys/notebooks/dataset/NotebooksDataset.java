package recsys.notebooks.dataset;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.impl.model.file.MatrixDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

public class NotebooksDataset {

	public DataModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		String path = prop.getProperty("notebooks-ratings");
		
		File dataFile = new File(path);
		DataModel model = new MatrixDataModel(dataFile, ",", 0f);

		return model;
	}

}
