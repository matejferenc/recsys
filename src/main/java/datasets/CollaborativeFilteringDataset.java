package datasets;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public abstract class CollaborativeFilteringDataset {
	
	public StatsParams run() throws Exception {
		long startTime = new Date().getTime();
		StatsParams params = execute();
		long endTime = new Date().getTime();
		int s = (int) ((endTime - startTime) / 1000);
		params.seconds = s;
		System.out.println("cas behu: " + s + "s");
		return params;
	}
	
	public abstract StatsParams execute() throws Exception;

	public StatsParams execute(DataModel model) throws Exception {
		StatsParams params = new StatsParams();

		params.itemCount = model.getNumItems();
		params.userCount = model.getNumUsers();
		
		System.out.println("items: " + params.itemCount);
		System.out.println("users: " + params.userCount);
		
		params.totalRatingCount = 0;

		// histogram poctu hodnoteni na jeden film
		Map<Long, Integer> itemRatingsCount = new HashMap<Long, Integer>();

		// histogram poctu hodnoteni na jedneho uzivatela
		Map<Long, Integer> userRatingsCount = new HashMap<Long, Integer>();

		// histogram hodnoteni
		Map<Float, Integer> totalRatingsCount = new HashMap<Float, Integer>();

		LongPrimitiveIterator itemIDs = model.getItemIDs();
		while (itemIDs.hasNext()) {
			Long itemId = itemIDs.next();
			PreferenceArray preferencesForItem = model.getPreferencesForItem(itemId);
			int preferencesCountForItem = preferencesForItem.length();
			itemRatingsCount.put(itemId, preferencesCountForItem);
			params.totalRatingCount += preferencesCountForItem;
			for (int i = 0; i < preferencesCountForItem; i++) {
				Preference preference = preferencesForItem.get(i);
				float value = preference.getValue();

				if (totalRatingsCount.containsKey(value)) {
					Integer count = totalRatingsCount.get(value);
					totalRatingsCount.put(value, count + 1);
				} else {
					totalRatingsCount.put(value, 1);
				}

			}
		}
		System.out.println(totalRatingsCount);
		params.ratingHistogram = new TreeMap<>(totalRatingsCount);
		
		double density = params.totalRatingCount / (params.itemCount * (double)params.userCount);

		// zoskupime podla poctu hodnoteni
		TreeMap<Integer, Integer> ratingsCountPerItemHistogram = new TreeMap<>();
		for (Map.Entry<Long, Integer> entry : itemRatingsCount.entrySet()) {
			// Long itemId = entry.getKey();
			Integer ratingsCount = entry.getValue();

			if (ratingsCountPerItemHistogram.containsKey(ratingsCount)) {
				Integer count = ratingsCountPerItemHistogram.get(ratingsCount);
				ratingsCountPerItemHistogram.put(ratingsCount, count + 1);
			} else {
				ratingsCountPerItemHistogram.put(ratingsCount, 1);
			}
		}
		int step = (int) (params.totalRatingCount / (ratingsCountPerItemHistogram.size() * 20 * 0.06 / density));
		params.ratingsCountPerItemHistogram = groupHistogramWithXColumns(ratingsCountPerItemHistogram, step);
		System.out.println(params.ratingsCountPerItemHistogram);

		int totalPreferencesFromUser = 0;
		LongPrimitiveIterator userIDs = model.getUserIDs();
		while (userIDs.hasNext()) {
			Long userId = userIDs.next();
			PreferenceArray preferencesFromUser = model.getPreferencesFromUser(userId);
			int preferencesCountFromUser = preferencesFromUser.length();
			userRatingsCount.put(userId, preferencesCountFromUser);
			totalPreferencesFromUser += preferencesCountFromUser;
		}
		// sanity check
		if (totalPreferencesFromUser != params.totalRatingCount) {
			throw new IllegalStateException("check failed. expected " + params.totalRatingCount + " but was " + totalPreferencesFromUser);
		}

		// zoskupime podla poctu hodnoteni
		TreeMap<Integer, Integer> ratingsCountPerUserHistogram = new TreeMap<>();
		for (Map.Entry<Long, Integer> entry : userRatingsCount.entrySet()) {
			// Long userId = entry.getKey();
			Integer ratingsCount = entry.getValue();

			if (ratingsCountPerUserHistogram.containsKey(ratingsCount)) {
				Integer count = ratingsCountPerUserHistogram.get(ratingsCount);
				ratingsCountPerUserHistogram.put(ratingsCount, count + 1);
			} else {
				ratingsCountPerUserHistogram.put(ratingsCount, 1);
			}
		}
		step = (int) (params.totalRatingCount / (ratingsCountPerUserHistogram.size() * 20 * 0.06 / density));
		params.ratingsCountPerUserHistogram = groupHistogramWithXColumns(ratingsCountPerUserHistogram, step);
		System.out.println(params.ratingsCountPerUserHistogram);

		
		return params;
	}
	
	Map<String, Integer> groupHistogramWithXColumns(TreeMap<Integer, Integer> histogram, int hint) {
		// number of columns
		int x = 10;
		Map<String, Integer> groupedHistogram = null;
		groupedHistogram = groupHistogramWithStep(histogram, hint);
		Map<String, Integer> bestGroupedHistogram = groupedHistogram;
		int bestBarsDiff = Integer.MAX_VALUE;
		hint = hint / 2;
		Integer prevBarsDiff = null;
		int signum = 1;
		int step = hint;
		for (int i = 21; i >= 0; i--) {
			groupedHistogram = groupHistogramWithStep(histogram, step < 1 ? 1 : step);
			int barsDiff = Math.abs(groupedHistogram.size() - x);
			if(barsDiff < bestBarsDiff) {
				bestBarsDiff = barsDiff;
				bestGroupedHistogram = groupedHistogram;
			}
			if(prevBarsDiff != null){
				signum = Math.round(Math.signum(groupedHistogram.size() - x));
				if(signum == 0) signum = 1;
			}
			step += signum * i;
			prevBarsDiff = barsDiff;
		}
		return bestGroupedHistogram;
	}

	Map<String, Integer> groupHistogramWithStep(TreeMap<Integer, Integer> histogram, int step) {
		Map<String, Integer> grouped = new LinkedHashMap<>();
		Integer first = histogram.firstKey();
		Integer last = null;
		Integer total = 0;
		int i = 0;
		for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) {
			Integer value = entry.getKey();
			Integer itemsCount = entry.getValue();
			last = value;

			if (first == null) {
				first = value;
			}

			total += itemsCount;

			if (i == step) {
				grouped.put(first + " - " + last, total);
				total = 0;
				first = null;
				i = 0;
			}
			i++;
		}
		// pridame posledny interval
		grouped.put(first + " - " + last, total);
		return grouped;
	}
}
