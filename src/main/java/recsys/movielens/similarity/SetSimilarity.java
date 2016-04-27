package recsys.movielens.similarity;

import java.util.Set;

public interface SetSimilarity {

	public Float getSimilarity(Set<String> set1, Set<String> set2);

}
