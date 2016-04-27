package recsys.notebooks.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.model.NotebooksUserModel;
import recsys.notebooks.model.builder.NotebooksUserModelBuilder;
import recsys.notebooks.recommender.NotebooksLocalClassificationRecommender;
import weka.classifiers.Classifier;

public abstract class NotebooksLocalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final NotebooksDataModel notebooksDataModel;

	public NotebooksLocalClassificationRecommenderBuilder(NotebooksDataModel notebooksDataModel) {
		this.notebooksDataModel = notebooksDataModel;
	}
	
	abstract public Classifier createClassifier();

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		NotebooksUserModelBuilder userModelBuilder = new NotebooksUserModelBuilder(dataModel, notebooksDataModel);
		NotebooksUserModel userModel = userModelBuilder.build();
		try {
			return new NotebooksLocalClassificationRecommender(dataModel, userModel, notebooksDataModel){

				@Override
				public Classifier createClassifier() {
					return NotebooksLocalClassificationRecommenderBuilder.this.createClassifier();
				}
				
			};
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	public String getName() {
		return "Notebooks Local Random Forest Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

}
