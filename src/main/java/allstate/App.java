package allstate;

import allstate.evaluator.AllstateEvaluator;
import allstate.recommender.AllstateRecommender;
import allstate.recommender.LastRecordRecommender;
import dataModel.AllstateDataModel;

public class App {

	public static void main(String[] args) throws Exception {
		AllstateDataModel model = new AllstateModelCreator().createModel();
		AllstateRecommender recommender = new LastRecordRecommender();
		AllstateEvaluator evaluator = new AllstateEvaluator(recommender, model);
		double successRatio = evaluator.evaluate(0.3);
		System.out.println(successRatio);
	}

}
