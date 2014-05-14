package allstate.recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import allstate.datasets.Allstate;
import allstate.model.AllstateDataModel;
import allstate.model.Record;

public class MostFrequentValueRecommender implements AllstateRecommender {

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
		for (int i = 16; i <= 22; i++) {
			String item = getMostFrequent(records, i);
			results.add(Integer.parseInt(item));
		}
		return results;
	}

	private String getMostFrequent(List<Record> records, int i) {
		Map<String, Integer> histogram = new HashMap<>();
		for (Record record : records) {
			String parameterValue = record.get(i);
			if (histogram.containsKey(parameterValue)) {
				histogram.put(parameterValue, histogram.get(parameterValue) + 1);
			} else {
				histogram.put(parameterValue, 1);
			}
		}
		String mostFrequent = null;
		Integer mostFrequentCount = Integer.MIN_VALUE;
		for (Entry<String, Integer> entry : histogram.entrySet()) {
			if (entry.getValue() > mostFrequentCount) {
				mostFrequent = entry.getKey();
				mostFrequentCount = entry.getValue();
			} else if (entry.getValue() == mostFrequentCount) {
				// najcastejsia hodnota tohto parametru
				Integer mostFrequentValue = Allstate.mostFrequentParameterValues.get(i);
				if(entry.getKey().equals(mostFrequentValue + "")) {
					mostFrequent = entry.getKey();
				}
			}
		}
		return mostFrequent;
	}

}
