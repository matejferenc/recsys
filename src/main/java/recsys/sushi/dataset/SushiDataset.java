package recsys.sushi.dataset;

import java.io.File;
import java.util.Properties;

import org.apache.mahout.cf.taste.impl.model.file.MatrixDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

/**
 * Class used to load a model of sushi dataset.
 *
 */
public class SushiDataset {

	public DataModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		String path = prop.getProperty("sushi3b-5000-10-score");
		
		File dataFile = new File(path);
		DataModel model = new MatrixDataModel(dataFile, " ", -1f);

		return model;
	}

}
