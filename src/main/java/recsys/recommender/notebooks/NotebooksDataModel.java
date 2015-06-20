package recsys.recommender.notebooks;

import java.util.HashMap;
import java.util.Map;

public class NotebooksDataModel {
	
	public static final int MAX_RAM = 2000;

	public static final int MAX_PRICE = 54395;

	public static final int MAX_HDD = 640;

	public static final int MAX_DISPLAY = 31;

	private Map<Integer, Notebook> notebooks;

	public NotebooksDataModel() {
		notebooks = new HashMap<>();
	}

	public Notebook getNotebook(int itemID) {
		return notebooks.get(itemID);
	}

	public void add(Integer itemID, Notebook notebook) {
		notebooks.put(itemID, notebook);
	}
}
