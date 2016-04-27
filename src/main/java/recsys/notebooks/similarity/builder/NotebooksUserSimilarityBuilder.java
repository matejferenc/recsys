package recsys.notebooks.similarity.builder;

import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUserModel;
import recsys.notebooks.model.builder.NotebooksUserModelBuilder;
import recsys.notebooks.similarity.NotebooksUserSimilarity;
import recsys.notebooks.similarity.NotebooksUserSimilarity.Include;
import recsys.similarity.builder.UserSimilarityBuilder;

public class NotebooksUserSimilarityBuilder implements UserSimilarityBuilder {

	private final NotebooksDataModel notebooksDataModel;
	private EnumSet<Include> include;
	
	public NotebooksUserSimilarityBuilder(NotebooksDataModel notebooksDataModel, EnumSet<Include> include) {
		this.notebooksDataModel = notebooksDataModel;
		this.include = include;
	}

	@Override
	public UserSimilarity build(DataModel dataModel) throws TasteException {
		NotebooksUserModelBuilder notebooksUserModelBuilder = new NotebooksUserModelBuilder(dataModel, notebooksDataModel);
		NotebooksUserModel userModel = notebooksUserModelBuilder.build();
		return new NotebooksUserSimilarity(userModel, include);
	}

}
