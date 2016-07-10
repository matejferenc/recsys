package recsys.movielens.evaluator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import recsys.movielens.similarity.MovielensUserSimilarityFunctionPool;

public class MovielensUserSimilarityFunctionPoolTest {

	@Test
	public void test() {
		MovielensUserSimilarityFunctionPool pool = new MovielensUserSimilarityFunctionPool();
		pool.generateRandom(10);
		assertEquals(10, pool.size());
		pool.nextGeneration();
		assertEquals(10, pool.size());
	}
}
