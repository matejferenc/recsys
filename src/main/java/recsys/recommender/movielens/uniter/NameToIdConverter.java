package recsys.recommender.movielens.uniter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NameToIdConverter {

	private Map<String, Long> genreIdMap;

	private Map<String, Long> directorIdMap;

	private Map<String, Long> actorIdMap;

	private Map<String, Long> actressIdMap;

	public NameToIdConverter() {
		genreIdMap = new HashMap<String, Long>();
		directorIdMap = new HashMap<String, Long>();
		actorIdMap = new HashMap<String, Long>();
		actressIdMap = new HashMap<String, Long>();
	}

	public long convertGenre(String name) {
		return convertInternal(genreIdMap, name);
	}

	public long convertDirector(String name) {
		return convertInternal(directorIdMap, name);
	}

	public long convertActor(String name) {
		return convertInternal(actorIdMap, name);
	}

	public long convertActress(String name) {
		return convertInternal(actressIdMap, name);
	}

	private long convertInternal(Map<String, Long> textToIdMap, String text) {
		if (textToIdMap.containsKey(text)) {
			return textToIdMap.get(text);
		} else {
			long newTextId = textToIdMap.size();
			textToIdMap.put(text, newTextId);
			return newTextId;
		}
	}

	public Set<Long> convertGenresToIds(Set<String> genres) {
		Set<Long> ids = new HashSet<Long>();
		for (String genre : genres) {
			ids.add(convertGenre(genre));
		}
		return ids;
	}
	
	public Set<Long> convertDirectorsToIds(Set<String> directors) {
		Set<Long> ids = new HashSet<Long>();
		for (String director : directors) {
			ids.add(convertDirector(director));
		}
		return ids;
	}
	
	public Set<Long> convertActorsToIds(Set<String> actors) {
		Set<Long> ids = new HashSet<Long>();
		for (String actor : actors) {
			ids.add(convertActor(actor));
		}
		return ids;
	}
	
	public Set<Long> convertActressesToIds(Set<String> actresses) {
		Set<Long> ids = new HashSet<Long>();
		for (String actress : actresses) {
			ids.add(convertActress(actress));
		}
		return ids;
	}
}
