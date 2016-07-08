package recsys.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents preference of one attribute, e.g. genre, director, actor or actress.
 */
public class ItemPreference {

	private Double averagePreference;

	private int numberOfAddedPreferences;

	private Double cumulativePreference;

	private List<Double> preferences = new ArrayList<Double>();

	private Double preferenceVariance;

	public void addPreference(Double p) {
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
		Double cummulativeDifferences = 0d;
		for (Double preference : preferences) {
			Double diff = (double) Math.pow(Math.abs(averagePreference - preference), 2);
			cummulativeDifferences += diff;
		}
		preferenceVariance = cummulativeDifferences / n;
	}
	
	public void freeReferences() {
		preferences = null;
	}

	public Double getAveragePreference() {
		return averagePreference;
	}

	public Double getPreferenceVariance() {
		return preferenceVariance;
	}

}
