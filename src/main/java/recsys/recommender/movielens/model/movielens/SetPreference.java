package recsys.recommender.movielens.model.movielens;

import java.util.HashMap;
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

		private double preference;

		private int numberOfAddedPreferences;

		private double cumulativePreference;

		public void addPreference(double p) {
			numberOfAddedPreferences++;
			cumulativePreference += p;
			preference = cumulativePreference / numberOfAddedPreferences;
		}

		public double getPreference() {
			return preference;
		}

	}

	public SetPreference() {
		propertyPreferences = new HashMap<Integer, ItemPreference>();
	}

	public void addPropertyPreference(int imdbPropertyId, double p){
		if(propertyPreferences.containsKey(imdbPropertyId)){
			propertyPreferences.get(imdbPropertyId).addPreference(p);
		}else{
			ItemPreference newItemPreference = new ItemPreference();
			propertyPreferences.put(imdbPropertyId, newItemPreference);
			newItemPreference.addPreference(p);
		}
	}
	
	/**
	 * Returns calculated accumulated preference value of specified property.<br/>
	 * Property can be genre ID, as specified in enriched MovieLens dataset.
	 * @param propertyId
	 * @return
	 */
	public double getPropertyPreference(int propertyId){
		ItemPreference itemPreference = propertyPreferences.get(propertyId);
		return itemPreference.getPreference();
	}
	
	public Set<Integer> getAllPropertyIds(){
		return propertyPreferences.keySet();
	}
}
