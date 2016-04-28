package recsys.sushi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of sushi products.
 *
 */
public class SushiItemDataModel {

	// price has values from interval [0,5]
	public static final double MAX_PRICE = 5;

	// oiliness has values from interval [0,4]
	public static int MAX_OILINESS = 4;

	private Map<Integer, SushiPiece> sushis;

	public SushiItemDataModel() {
		sushis = new HashMap<>();
	}

	public SushiPiece getSushiPiece(int itemID) {
		return sushis.get(itemID);
	}

	public void add(Integer itemID, SushiPiece sushiPiece) {
		sushis.put(itemID, sushiPiece);
	}
}
