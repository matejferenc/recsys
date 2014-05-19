package recsys.recommender.sushi;

import java.util.Map;

public class SushiDataModel {

	private Map<Integer, SushiPiece> sushis;

	public SushiPiece getSushiPiece(int itemID) {
		return sushis.get(itemID);
	}

	public void add(Integer itemID, SushiPiece sushiPiece) {
		sushis.put(itemID, sushiPiece);
	}
}
