package recsys.dataset;

import java.io.File;
import java.util.Properties;

import recsys.recommender.sushi.SushiDataModelLoader;
import recsys.recommender.sushi.model.SushiDataModel;

public class SushiDataModelDataset {

	public SushiDataModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("sushi3-idata");
		SushiDataModelLoader sushiDataModelLoader = new SushiDataModelLoader(new File(path));
		SushiDataModel sushiDataModel = sushiDataModelLoader.getSushiDataModel();
		return sushiDataModel;
	}
}
