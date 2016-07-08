package recsys.movielens.uniter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NameToIdConverter {

	private Map<String, Integer> genreIdMap;

	private Map<String, Integer> directorIdMap;

	private Map<String, Integer> actorIdMap;

	private Map<String, Integer> actressIdMap;
	
	private Map<String, Integer> keywordsIdMap;

	public NameToIdConverter() {
		genreIdMap = new HashMap<String, Integer>();
		directorIdMap = new HashMap<String, Integer>();
		actorIdMap = new HashMap<String, Integer>();
		actressIdMap = new HashMap<String, Integer>();
		keywordsIdMap = new HashMap<String, Integer>();
	}

	public Integer convertGenre(String name) {
		return convertInternal(genreIdMap, name);
	}

	public Integer convertDirector(String name) {
		return convertInternal(directorIdMap, name);
	}

	public Integer convertActor(String name) {
		return convertInternal(actorIdMap, name);
	}

	public Integer convertActress(String name) {
		return convertInternal(actressIdMap, name);
	}
	
	public Integer convertKeyword(String name) {
		return convertInternal(keywordsIdMap, name);
	}

	private Integer convertInternal(Map<String, Integer> textToIdMap, String text) {
		if (textToIdMap.containsKey(text)) {
			return textToIdMap.get(text);
		} else {
			Integer newTextId = textToIdMap.size();
			textToIdMap.put(text, newTextId);
			return newTextId;
		}
	}

	public Set<Integer> convertGenresToIds(Set<String> genres) {
		Set<Integer> ids = new HashSet<Integer>();
		for (String genre : genres) {
			ids.add(convertGenre(genre));
		}
		return ids;
	}
	
	public Set<Integer> convertDirectorsToIds(Set<String> directors) {
		Set<Integer> ids = new HashSet<Integer>();
		for (String director : directors) {
			ids.add(convertDirector(director));
		}
		return ids;
	}
	
	public Set<Integer> convertActorsToIds(Set<String> actors) {
		Set<Integer> ids = new HashSet<Integer>();
		for (String actor : actors) {
			ids.add(convertActor(actor));
		}
		return ids;
	}
	
	public Set<Integer> convertActressesToIds(Set<String> actresses) {
		Set<Integer> ids = new HashSet<Integer>();
		for (String actress : actresses) {
			ids.add(convertActress(actress));
		}
		return ids;
	}
	
	public Set<Integer> convertKeywordsToIds(Set<String> keywords) {
		Set<Integer> ids = new HashSet<Integer>();
		for (String keyword : keywords) {
			ids.add(convertKeyword(keyword));
		}
		return ids;
	}
}
