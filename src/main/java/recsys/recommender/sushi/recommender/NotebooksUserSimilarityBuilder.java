package recsys.recommender.sushi.recommender;

import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUserModel;
import recsys.recommender.sushi.recommender.NotebooksUserSimilarity.Include;

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
