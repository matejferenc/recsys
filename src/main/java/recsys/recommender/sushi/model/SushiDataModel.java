package recsys.recommender.sushi.model;

import java.util.HashMap;
import java.util.Map;

import recsys.recommender.sushi.SushiPiece;

public class SushiDataModel {

	private Map<Integer, SushiPiece> sushis;
	
	public SushiDataModel() {
		sushis = new HashMap<>();
	}

	public SushiPiece getSushiPiece(int itemID) {
		return sushis.get(itemID);
	}

	public void add(Integer itemID, SushiPiece sushiPiece) {
		sushis.put(itemID, sushiPiece);
	}
}
