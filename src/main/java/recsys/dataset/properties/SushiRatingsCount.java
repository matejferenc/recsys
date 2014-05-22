package recsys.dataset.properties;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import recsys.dataset.SushiDataset;

public class SushiRatingsCount {

	public static void main(String args[]) throws Exception {
		DataModel dataModel = new SushiDataset().build();
		LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
		ArrayList<Integer> ratingsCounts = new ArrayList<>();
		while(itemIDs.hasNext()) {
			Long itemID = itemIDs.next();
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
