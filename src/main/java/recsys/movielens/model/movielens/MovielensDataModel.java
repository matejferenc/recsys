package recsys.movielens.model.movielens;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;

public class MovielensDataModel {

	private List<Integer> itemIDs;

	private FastByIDMap<Set<String>> genres;

	private FastByIDMap<String> names;

	private FastByIDMap<Integer> years;


	public MovielensDataModel() {
		genres = new FastByIDMap<Set<String>>();
		names = new FastByIDMap<String>();
		years = new FastByIDMap<Integer>();
		setItemIDs(new ArrayList<Integer>());
	}

	public Set<String> getGenres(Integer itemID) {
		return genres.get(itemID);
	}

	boolean intersects(Set<String> set1, Set<String> set2) {
		for (String item : set2) {
			if (set1.contains(item))
				return true;
		}
		return false;
	}

	public FastByIDMap<Integer> getYears() {
		return years;
	}

	public FastByIDMap<String> getNames() {
		return names;
	}

	public void setNames(FastByIDMap<String> names) {
		this.names = names;
	}

	public List<Integer> getItemIDs() {
		return itemIDs;
	}

	public void setItemIDs(List<Integer> itemIDs) {
		this.itemIDs = itemIDs;
	}

	public FastByIDMap<Set<String>> getGenres() {
		return genres;
	}

	public void setGenres(FastByIDMap<Set<String>> genres) {
		this.genres = genres;
	}

}
