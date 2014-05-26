package recsys.recommender.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a set of genres, actors, directors or actresses<br/>
 * and the preferences for each single genre, actor, director or actress.
 * 
 * @author matej
 * 
 */
public class SetPreference {

	/**
	 * Map from item id (can be genre, director, actor or actress)<br/>
	 * to single ItemPreference of that item.
	 */
	private Map<Integer, ItemPreference> propertyPreferences;

	/**
	 * Represents preference of one genre, director, actor or actress.
	 * 
	 * @author matej
	 * 
	 */
	public class ItemPreference {

		private double averagePreference;

		private int numberOfAddedPreferences;

		private double cumulativePreference;

		private List<Double> preferences = new ArrayList<Double>();

		private double preferenceVariance;

		public void addPreference(double p) {
			numberOfAddedPreferences++;
			cumulativePreference += p;
			preferences.add(p);
			calculateAveragePreference();
			calculatePreferenceVariance();
		}

		private void calculateAveragePreference() {
			averagePreference = cumulativePreference / numberOfAddedPreferences;
		}

		private void calculatePreferenceVariance() {
			int n = preferences.size();
			double cummulativeDifferences = 0;
			for (Double preference : preferences) {
				double diff = Math.pow(Math.abs(getAveragePreference() - preference), 2);
				cummulativeDifferences += diff;
			}
			preferenceVariance = cummulativeDifferences / n;
		}

		public double getAveragePreference() {
			return averagePreference;
		}

		public double getPreferenceVariance() {
			return preferenceVariance;
		}

	}

	public SetPreference() {
		propertyPreferences = new HashMap<Integer, ItemPreference>();
	}

	public void addPropertyPreference(int imdbPropertyId, double p) {
		if (propertyPreferences.containsKey(imdbPropertyId)) {
			propertyPreferences.get(imdbPropertyId).addPreference(p);
		} else {
			ItemPreference newItemPreference = new ItemPreference();
			propertyPreferences.put(imdbPropertyId, newItemPreference);
			newItemPreference.addPreference(p);
		}
	}

	/**
	 * Returns calculated accumulated preference value of specified property.<br/>
	 * Property can be genre ID, as specified in enriched MovieLens dataset.
	 * 
	 * @param propertyId
	 * @return
	 */
	public double getPropertyPreference(int propertyId) {
		ItemPreference itemPreference = propertyPreferences.get(propertyId);
		return itemPreference.getAveragePreference();
	}
	
	/**
	 * Returns calculated preference variance of specified property.<br/>
	 * Property can be genre ID, as specified in enriched MovieLens dataset.
	 * 
	 * @param propertyId
	 * @return
	 */
	public double getPropertyVariance(int propertyId) {
		ItemPreference itemPreference = propertyPreferences.get(propertyId);
		return itemPreference.getPreferenceVariance();
	}

	public Set<Integer> getAllPropertyIds() {
		return propertyPreferences.keySet();
	}
}
