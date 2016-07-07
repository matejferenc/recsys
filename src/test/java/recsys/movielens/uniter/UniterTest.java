package recsys.movielens.uniter;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Regression test for keeping already successfully matched movies in the set of matched movies.
 */
public class UniterTest {

	@Test
	public void testUnitingCounts() throws Exception {
		Uniter uniter = new Uniter();
		uniter.createMovielensDataModel();
		
		uniter.uniteGenres();
		assertEquals(3855, uniter.moviesFound);
		assertEquals(28, uniter.moviesNotFound);
		
		uniter.uniteDirectors();
		assertEquals(3856, uniter.moviesFound);
		assertEquals(27, uniter.moviesNotFound);
		
		uniter.uniteActors();
		assertEquals(3843, uniter.moviesFound);
		assertEquals(40, uniter.moviesNotFound);
		
		uniter.uniteActresses();
		assertEquals(3809, uniter.moviesFound);
		assertEquals(74, uniter.moviesNotFound);
		
		uniter.uniteKeywords();
		assertEquals(3824, uniter.moviesFound);
		assertEquals(59, uniter.moviesNotFound);
	}
}
