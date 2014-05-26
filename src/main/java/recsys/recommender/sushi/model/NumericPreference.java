package recsys.recommender.sushi.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math.stat.regression.SimpleRegression;

public class NumericPreference {

	// map from numeric value to rating
	private SortedMap<Double, Double> preferences;

	public NumericPreference() {
		preferences = new TreeMap<>();
	}

	public void addPreference(Double value, Double rating) {
		preferences.put(value, rating);
	}

	public Double interpolation(Double value) {
		Double previousRating = null;
		Iterator<Entry<Double, Double>> it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			if (value < numericValue) {
				if (previousRating == null)
					return rating;
				else
					return (previousRating + rating) / 2;
			} else {
				previousRating = rating;
			}
		}
		return previousRating;
	}

	public Double preferredValue() {
		Double sumOfPreferences = 0d;
		int ratingsCount = 0;
		Iterator<Entry<Double, Double>> it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			sumOfPreferences += rating * numericValue;
			ratingsCount++;
		}
		return sumOfPreferences / ratingsCount;
	}
	
	public double getVariance(){
		SimpleRegression simpleRegression = new SimpleRegression();
		Iterator<Entry<Double, Double>> it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			simpleRegression.addData(numericValue, rating);
		}
		double cummulativeDifferences = 0;
		it = preferences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry<Double, Double>) it.next();
			Double numericValue = pair.getKey();
			Double rating = pair.getValue();
			double predict = simpleRegression.predict(numericValue);
			double diff = Math.pow(Math.abs(predict - rating), 2);
			cummulativeDifferences += diff;
		}
		return cummulativeDifferences / preferences.size();
	}
	

}
