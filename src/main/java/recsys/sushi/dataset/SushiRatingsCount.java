package recsys.sushi.dataset;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.mahout.cf.taste.impl.common.IntPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class SushiRatingsCount {

	public static void main(String args[]) throws Exception {
		DataModel dataModel = new SushiDataset().build();
		IntPrimitiveIterator itemIDs = dataModel.getItemIDs();
		ArrayList<Integer> ratingsCounts = new ArrayList<>();
		while(itemIDs.hasNext()) {
			Integer itemID = itemIDs.next();
			PreferenceArray preferencesForItem = dataModel.getPreferencesForItem(itemID);
			int ratingsCount = preferencesForItem.getIDs().length;
			ratingsCounts.add(ratingsCount);
		}
		Collections.sort(ratingsCounts);
		for (Integer ratingsCount : ratingsCounts) {
			System.out.println(ratingsCount);
		}
	}
}
