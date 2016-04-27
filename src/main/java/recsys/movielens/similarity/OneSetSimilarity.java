package recsys.movielens.similarity;

import java.util.Set;

public class OneSetSimilarity implements SetSimilarity {

	/**
	 * Returns simple set similarity defined as:<br/>
	 * similarity = 1 <br/>
	 * Example 1: S1 = {a,b,c}, S2= {a,b,d,e,f} <br/>
	 * similarity(S1,S2) = 1 <br/>
	 * Example 2: S1 = {a,b,c}, S2= {a,b,c,d,e,f} <br/>
	 * similarity(S1,S2) = 1 <br/>
	 * Example 3: S1 = {a,b,c}, S2= {a,b,c} <br/>
	 * similarity(S1,S2) = 1 <br/>
	 * Example 4: S1 = {a,b}, S2= {a,b,c,d,e,f} <br/>
	 * similarity(S1,S2) = 1
	 */
	@Override
	public Float getSimilarity(Set<String> set1, Set<String> set2) {
		return 1f;
	}

}
