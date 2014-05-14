package allstate.recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import allstate.model.AllstateDataModel;
import allstate.model.Record;

public class FirstRecordRecommender implements AllstateRecommender {

	@Override
	public Map<Long, List<Integer>> recommend(AllstateDataModel model) {
		Map<Long, List<Integer>> results = new HashMap<>();
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			results.put(entry.getKey(), recommend(entry.getValue()));
		}
		return results;
	}

	private List<Integer> recommend(List<Record> records) {
		List<Integer> results = new ArrayList<>();
		results.add(getFirstValue(records, 16));
		results.add(getFirstValue(records, 17));
		results.add(getFirstValue(records, 18));
		results.add(getFirstValue(records, 19));
		results.add(getFirstValue(records, 20));
		results.add(getFirstValue(records, 21));
		results.add(getFirstValue(records, 22));
		return results;
	}

	private Integer getFirstValue(List<Record> records, int i) {
		return Integer.parseInt(records.get(0).get(i));
	}
}
