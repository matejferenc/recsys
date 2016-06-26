package recsys.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents preference of one e.g. genre, director, actor or actress.
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
