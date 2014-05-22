package recsys.dataset;

import java.io.File;
import java.util.Properties;

import recsys.recommender.sushi.SushiUserDataModelLoader;
import recsys.recommender.sushi.model.UserModel;

public class SushiUserDataModelDataset {

	public UserModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("sushi3-udata");
		SushiUserDataModelLoader sushiUserDataModelLoader = new SushiUserDataModelLoader(new File(path));
		UserModel userModel = sushiUserDataModelLoader.getUserModel();
		return userModel;
	}
}