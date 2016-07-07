package recsys.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents preference of one attribute, e.g. genre, director, actor or actress.
 */
public class ItemPreference {

	private float averagePreference;

	private int numberOfAddedPreferences;

	private float cumulativePreference;

	private List<Float> preferences = new ArrayList<Float>();

	private float preferenceVariance;

	public void addPreference(Float p) {
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
		float cummulativeDifferences = 0;
		for (Float preference : preferences) {
			float diff = (float) Math.pow(Math.abs(averagePreference - preference), 2);
			cummulativeDifferences += diff;
		}
		preferenceVariance = cummulativeDifferences / n;
	}
	
	public void freeReferences() {
		preferences = null;
	}

	public float getAveragePreference() {
		return averagePreference;
	}

	public float getPreferenceVariance() {
		return preferenceVariance;
	}

}
