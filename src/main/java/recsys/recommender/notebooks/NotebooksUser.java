package recsys.recommender.notebooks;

import recsys.recommender.model.SetPreference;
import recsys.recommender.sushi.model.NumericPreference;

public class NotebooksUser {

	private NumericPreference hddPreferences;

	private NumericPreference displayPreferences;

	private NumericPreference pricePreferences;

	private SetPreference producerPreferences;

	private NumericPreference ramPreferences;

	public NotebooksUser() {
		hddPreferences = new NumericPreference();
		displayPreferences = new NumericPreference();
		pricePreferences = new NumericPreference();
		producerPreferences = new SetPreference();
		ramPreferences = new NumericPreference();
	}

	public NumericPreference getHddPreferences() {
		return hddPreferences;
	}

	public NumericPreference getDisplayPreferences() {
		return displayPreferences;
	}

	public SetPreference getManufacturerPreferences() {
		return producerPreferences;
	}

	public NumericPreference getRamPreferences() {
		return ramPreferences;
	}

	public NumericPreference getPricePreferences() {
		return pricePreferences;
	}

}