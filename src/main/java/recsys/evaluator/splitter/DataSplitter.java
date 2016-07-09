package recsys.evaluator.splitter;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;

public interface DataSplitter {

	boolean hasNext();
	
	Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> next() throws Exception;
}
