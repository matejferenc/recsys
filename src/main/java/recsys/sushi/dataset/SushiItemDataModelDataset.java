package recsys.sushi.dataset;

import java.io.File;
import java.util.Properties;

import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.model.loader.SushiItemDataModelLoader;

public class SushiItemDataModelDataset {

	public SushiItemDataModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("sushi3-idata");
		SushiItemDataModelLoader sushiItemDataModelLoader = new SushiItemDataModelLoader(new File(path));
		SushiItemDataModel sushiItemDataModel = sushiItemDataModelLoader.getSushiDataModel();
		return sushiItemDataModel;
	}
}
