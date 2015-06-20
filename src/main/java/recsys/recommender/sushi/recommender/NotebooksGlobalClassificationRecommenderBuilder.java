package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUserModel;
import weka.classifiers.Classifier;

public abstract class NotebooksGlobalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final NotebooksDataModel notebooksDataModel;

	public NotebooksGlobalClassificationRecommenderBuilder(NotebooksDataModel notebooksDataModel) {
		this.notebooksDataModel = notebooksDataModel;
	}
	
	abstract public Classifier createClassifier();

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		NotebooksUserModelBuilder userModelBuilder = new NotebooksUserModelBuilder(dataModel, notebooksDataModel);
		NotebooksUserModel userModel = userModelBuilder.build();
		try {
			return new NotebooksGlobalClassificationRecommender(dataModel, userModel, notebooksDataModel){

				@Override
				public Classifier createClassifier() {
					return NotebooksGlobalClassificationRecommenderBuilder.this.createClassifier();
				}
				
			};
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	public String getName() {
		return "Notebooks Global Random Forest Recommender Builder";
	}

	@Override
	public void freeReferences() {
	}

}
