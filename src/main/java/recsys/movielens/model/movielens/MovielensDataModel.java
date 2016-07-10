package recsys.movielens.model.movielens;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;

public class MovielensDataModel {

	private List<Long> itemIDs;

	private FastByIDMap<Set<String>> style;

	private FastByIDMap<String> names;

	private FastByIDMap<Integer> years;


	public MovielensDataModel() {
		style = new FastByIDMap<Set<String>>();
		names = new FastByIDMap<String>();
		years = new FastByIDMap<Integer>();
		setItemIDs(new ArrayList<Long>());
	}

	public Set<String> getGenres(long itemID) {
		return style.get(itemID);
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

	public List<Long> getItemIDs() {
		return itemIDs;
	}

	public void setItemIDs(List<Long> itemIDs) {
		this.itemIDs = itemIDs;
	}

	public FastByIDMap<Set<String>> getGenres() {
		return style;
	}

	public void setGenres(FastByIDMap<Set<String>> style) {
		this.style = style;
	}

}
