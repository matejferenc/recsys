package allstate.recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dataModel.AllstateDataModel;
import dataModel.Record;

public class LastRecordRecommender implements AllstateRecommender {

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
		results.add(getLastValue(records, 16));
		results.add(getLastValue(records, 17));
		results.add(getLastValue(records, 18));
		results.add(getLastValue(records, 19));
		results.add(getLastValue(records, 20));
		results.add(getLastValue(records, 21));
		results.add(getLastValue(records, 22));
		return results;
	}

	private Integer getLastValue(List<Record> records, int i) {
		Integer lastValue = null;
		for (Record record : records) {
			lastValue = Integer.parseInt((String) record.get(i));
		}
		return lastValue;
	}
}
