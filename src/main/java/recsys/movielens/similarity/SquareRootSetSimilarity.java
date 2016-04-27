package recsys.movielens.similarity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SquareRootSetSimilarity implements SetSimilarity {

	/**
	 * Returns simple set similarity defined as:<br/>
	 * similarity = intersectionSize / (set1size + set2size) <br/>
	 * Example 1: S1 = {a,b,c}, S2= {a,b,d,e,f} <br/>
	 * similarity(S1,S2) = 0.55 <br/>
	 * Example 2: S1 = {a,b,c}, S2= {a,b,c,d,e,f} <br/>
	 * similarity(S1,S2) = 0.79 <br/>
	 * Example 3: S1 = {a,b,c}, S2= {a,b,c} <br/>
	 * similarity(S1,S2) = 1 <br/>
	 * Example 4: S1 = {a,b}, S2= {a,b,c,d,e,f} <br/>
	 * similarity(S1,S2) = 0.74
	 */
	@Override
	public Float getSimilarity(Set<String> set1, Set<String> set2) {
		int intersectionSize = 0;
		for (String item : set2) {
			if (set1.contains(item))
				intersectionSize++;
		}
		float i1 = (float) intersectionSize / set1.size();
		float i2 = (float) intersectionSize / set2.size();
		return (float) Math.sqrt((i1 * i1 + i2 * i2) / 2);
	}

	public static void main(String[] args) {
		SquareRootSetSimilarity s = new SquareRootSetSimilarity();
		System.out.println(s.getSimilarity(new HashSet<String>(Arrays.asList("a", "b", "c")), new HashSet<String>(Arrays.asList("a", "b", "d", "e", "f"))));
		System.out.println(s.getSimilarity(new HashSet<String>(Arrays.asList("a", "b", "c")), new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e", "f"))));
		System.out.println(s.getSimilarity(new HashSet<String>(Arrays.asList("a", "b", "c")), new HashSet<String>(Arrays.asList("a", "b", "c"))));
		System.out.println(s.getSimilarity(new HashSet<String>(Arrays.asList("a", "b")), new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e", "f"))));
	}
}
