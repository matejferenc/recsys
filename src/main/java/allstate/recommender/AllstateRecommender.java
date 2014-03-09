package allstate.recommender;

import java.util.List;
import java.util.Map;

import allstate.model.AllstateDataModel;

public interface AllstateRecommender {

	Map<Long, List<Integer>> recommend(AllstateDataModel model);
}
