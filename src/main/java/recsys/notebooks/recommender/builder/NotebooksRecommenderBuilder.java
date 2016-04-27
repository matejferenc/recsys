package recsys.notebooks.recommender.builder;

import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUserModel;
import recsys.notebooks.model.builder.NotebooksUserModelBuilder;
import recsys.notebooks.recommender.NotebooksRecommender;
import recsys.notebooks.similarity.NotebooksUserSimilarity;
import recsys.notebooks.similarity.NotebooksUserSimilarity.Include;

public class NotebooksRecommenderBuilder implements RecommenderBuilder {

	private NotebooksDataModel notebooksDataModel;
	private EnumSet<Include> include;

	public NotebooksRecommenderBuilder(NotebooksDataModel notebooksDataModel, EnumSet<Include> include) {
		this.notebooksDataModel = notebooksDataModel;
		this.include = include;
	}

	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		NotebooksUserModelBuilder notebooksUserModelBuilder = new NotebooksUserModelBuilder(dataModel, notebooksDataModel);
		NotebooksUserModel userModel = notebooksUserModelBuilder.build();
		return new NotebooksRecommender(dataModel, userModel, notebooksDataModel, include);
	}

	public String getName() {
		return "Notebooks recommender";
	}

	public String getShortName() {
		return "NTB";
	}

	public void freeReferences() {
		
	}
}
