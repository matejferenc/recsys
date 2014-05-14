package datasets;

import java.util.Map;
import java.util.TreeMap;

public class StatsParams {

	public String title;
	public int itemCount;
	public int userCount;
	public int totalRatingCount;
	public TreeMap<Float, Integer> ratingHistogram;
	public Map<String, Integer> ratingsCountPerItemHistogram;
	public Map<String, Integer> ratingsCountPerUserHistogram;
	public int seconds;

	public String getTitle() {
		return title;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getUserCount() {
		return userCount;
	}
	
	public int getTotalRatingCount() {
		return totalRatingCount;
	}

	public Map<Float, Integer> getRatingHistogram() {
		return ratingHistogram;
	}

	public Map<String, Integer> getRatingsCountPerItemHistogram() {
		return ratingsCountPerItemHistogram;
	}
	
	public Map<String, Integer> getRatingsCountPerUserHistogram() {
		return ratingsCountPerUserHistogram;
	}
	
	public int getSeconds() {
		return seconds;
	}

}
