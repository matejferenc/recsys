package allstate.recommender;

import java.util.List;
import java.util.Map;

import dataModel.AllstateDataModel;

public interface AllstateRecommender {

	Map<Long, List<Integer>> recommend(AllstateDataModel model);
}
