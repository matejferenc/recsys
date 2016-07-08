package recsys.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents preferences of one user to attributes.
 * Attributes can be for example genres, actors, directors or actresses<br/>
 */
public class SetPreference {

	/**
	 * Map from item id (can be e.g. genre, director, actor or actress)<br/>
	 * to single ItemPreference of that item.
	 */
	private Map<Integer, ItemPreference> propertyPreferences;

	public SetPreference() {
		propertyPreferences = new HashMap<Integer, ItemPreference>();
	}

	public void addPropertyPreference(Integer propertyId, Float p) {
		ItemPreference itemPreference = propertyPreferences.get(propertyId);
		if (itemPreference != null) {
			itemPreference.addPreference(p);
		} else {
			ItemPreference newItemPreference = new ItemPreference();
			propertyPreferences.put(propertyId, newItemPreference);
			newItemPreference.addPreference(p);
		}
	}

	/**
	 * Returns calculated accumulated preference value of specified property.<br/>
	 * Property can be e.g. genre ID, as specified in enriched MovieLens dataset.
	 * 
	 * @param propertyId
	 * @return
	 */
	public Float getPropertyAverage(Integer propertyId) {
		ItemPreference itemPreference = propertyPreferences.get(propertyId);
		return itemPreference.getAveragePreference();
	}
	
	/**
	 * Returns calculated preference variance of specified property.<br/>
	 * Property can be e.g. genre ID, as specified in enriched MovieLens dataset.
	 * 
	 * @param propertyId
	 * @return
	 */
	public Float getPropertyVariance(Integer propertyId) {
		ItemPreference itemPreference = propertyPreferences.get(propertyId);
		return itemPreference.getPreferenceVariance();
	}

	public Set<Integer> getAllPropertyIds() {
		return propertyPreferences.keySet();
	}
	
	public Collection<ItemPreference> getAllPropertyValues() {
		return propertyPreferences.values();
	}
}
