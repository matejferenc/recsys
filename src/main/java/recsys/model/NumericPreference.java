package recsys.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math.stat.regression.SimpleRegression;

/**
 * Class for counting mean and variance of numeric preferences.
 *
 */
public class NumericPreference {

	// map from numeric value to rating
	private SortedMap<Double, Double> preferences;

	public NumericPreference() {
		preferences = new TreeMap<>();
	}

	public void addPreference(Double value, Double rating) {
		preferences.put(value, rating);
	}

	public Double getPreferredValue() {
		Double sumOfPreferences = 0d;
		Double ratingTotal = 0d;
		Iterator<Entry<Double, Double>> it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			sumOfPreferences += rating * numericValue;
			ratingTotal += rating;
		}
		return sumOfPreferences / ratingTotal;
	}
	
	public Double getVariance(){
		SimpleRegression simpleRegression = new SimpleRegression();
		Iterator<Entry<Double, Double>> it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			simpleRegression.addData(numericValue, rating);
		}
		Double cummulativeDifferences = 0d;
		it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			Double predict = (double) simpleRegression.predict(numericValue);
			Double diff = (double) Math.pow(Math.abs(predict - rating), 2);
			cummulativeDifferences += diff;
		}
		return cummulativeDifferences / preferences.size();
	}
	

}
