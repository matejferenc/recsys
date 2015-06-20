package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.notebooks.NotebooksUserModel;
import weka.classifiers.Classifier;

public abstract class NotebooksGlobalAndLocalClassificationRecommenderBuilder implements RecommenderBuilder {

	private final NotebooksDataModel notebooksDataModel;

	public NotebooksGlobalAndLocalClassificationRecommenderBuilder(NotebooksDataModel notebooksDataModel) {
		this.notebooksDataModel = notebooksDataModel;
	}
	
	abstract public Classifier createClassifier();

	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		NotebooksUserModelBuilder userModelBuilder = new NotebooksUserModelBuilder(dataModel, notebooksDataModel);
		NotebooksUserModel userModel = userModelBuilder.build();
		try {
			return new NotebooksGlobalAndLocalClassificationRecommender(dataModel, userModel, notebooksDataModel){

				@Override
				public Classifier createClassifier() {
					return NotebooksGlobalAndLocalClassificationRecommenderBuilder.this.createClassifier();
				}
				
			};
		} catch (Exception e) {
			throw new TasteException(e);
		}
	}

	@Override
	public String getName() {
		return "Notebooks Global And Local Random Forest Recommender Builder";
	}
	
	@Override
	public String getShortName() {
		return "GL";
	}

	@Override
	public void freeReferences() {
	}

}
