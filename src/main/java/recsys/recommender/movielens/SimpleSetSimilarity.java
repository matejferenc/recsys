package recsys.recommender.movielens;

import java.util.Set;

public class SimpleSetSimilarity implements SetSimilarity {

	/**
	 * Returns simple set similarity defined as:
	 * similarity = intersectionSize / (set1size + set2size)
	 * <br/>
	 * Example 1: S1 = {a,b,c}, S2= {a,b,d,e,f}
	 * <br/>
	 * similarity(S1,S2) = 0.5
	 * <br/>
	 * Example 2: S1 = {a,b,c}, S2= {a,b,c,d,e,f}
	 * <br/>
	 * similarity(S1,S2) = 0.666
	 * <br/>
	 * Example 3: S1 = {a,b,c}, S2= {a,b,c}
	 * <br/>
	 * similarity(S1,S2) = 1
	 * <br/>
	 * Example 4: S1 = {a,b}, S2= {a,b,c,d,e,f}
	 * <br/>
	 * similarity(S1,S2) = 0.5
	 */
	@Override
	public Float getSimilarity(Set<String> set1, Set<String> set2) {
		int intersectionSize = 0;
		for (String item : set2) {
			if (set1.contains(item))
				intersectionSize++;
		}
		return ((float) intersectionSize) * 2 / (set1.size() + set2.size());
	}

}
