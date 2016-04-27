package recsys.notebooks.dataset;

import java.io.File;
import java.util.Properties;

import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.loader.NotebooksDataModelLoader;

public class NotebooksDataModelDataset {

	public NotebooksDataModel build() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		String path = prop.getProperty("notebooks-data");
		NotebooksDataModelLoader notebooksDataModelLoader = new NotebooksDataModelLoader(new File(path));
		NotebooksDataModel notebooksDataModel = notebooksDataModelLoader.getNotebooksDataModel();
		return notebooksDataModel;
	}
}
