package recsys.movielens.uniter;

import static org.junit.Assert.*;
import org.junit.Test;

public class UniterTest {

	@Test
	public void testUnitingCounts() throws Exception {
		Uniter uniter = new Uniter();
		uniter.createMovielensDataModel();
		
		
		uniter.uniteGenres();
		assertEquals(3833, uniter.moviesFound);
		assertEquals(50, uniter.moviesNotFound);
		
		uniter.uniteDirectors();
		assertEquals(3836, uniter.moviesFound);
		assertEquals(47, uniter.moviesNotFound);
		
		uniter.uniteActors();
		assertEquals(3823, uniter.moviesFound);
		assertEquals(60, uniter.moviesNotFound);
		
		uniter.uniteActresses();
		assertEquals(3789, uniter.moviesFound);
		assertEquals(94, uniter.moviesNotFound);
		
		uniter.uniteKeywords();
		assertEquals(3824, uniter.moviesFound);
		assertEquals(59, uniter.moviesNotFound);
	}
}
