package recsys.recommender.sushi.model;

import recsys.recommender.model.SetPreference;

public class User {

	// will only have ids: 0, 1
	private SetPreference stylePreferences;

	// will only have ids: 0, 1
	private SetPreference majorGroupPreferences;

	// will only have ids: 0 - 11
	private SetPreference minorGroupPreferences;

	private NumericPreference oilinessPreferences;

	private NumericPreference eatingFrequencyPreferences;

	private NumericPreference pricePreferences;

	private NumericPreference sellingFrequencyPreferences;

	public User() {
		stylePreferences = new SetPreference();
		majorGroupPreferences = new SetPreference();
		minorGroupPreferences = new SetPreference();
		oilinessPreferences = new NumericPreference();
		eatingFrequencyPreferences = new NumericPreference();
		pricePreferences = new NumericPreference();
		sellingFrequencyPreferences = new NumericPreference();
	}

	public SetPreference getStylePreferences() {
		return stylePreferences;
	}

	public SetPreference getMajorGroupPreferences() {
		return majorGroupPreferences;
	}

	public SetPreference getMinorGroupPreferences() {
		return minorGroupPreferences;
	}

	public NumericPreference getOilinessPreferences() {
		return oilinessPreferences;
	}

	public NumericPreference getEatingFrequencyPreferences() {
		return eatingFrequencyPreferences;
	}

	public NumericPreference getPricePreferences() {
		return pricePreferences;
	}

	public NumericPreference getSellingFrequencyPreferences() {
		return sellingFrequencyPreferences;
	}

}
