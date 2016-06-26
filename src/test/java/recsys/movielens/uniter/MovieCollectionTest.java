package recsys.movielens.uniter;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import recsys.movielens.model.imdb.ImdbGenresDataModel;
import recsys.movielens.model.shared.Movie;

public class MovieCollectionTest {
	
	private Properties prop = new Properties();
	private ImdbGenresDataModel imdbGenresDataModel;
	
	@Test
	public void testFindingMovieForGenres() throws Exception {
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		File imdbGenresFile = new File(prop.getProperty("imdb-genres.list"));
		imdbGenresDataModel = new ImdbGenresDataModel(imdbGenresFile);
		
		assertMovieGenreFound("Underneath, The", 1995);
		assertMovieGenreFound("Misérables, Les", 1995);
		assertMovieGenreFound("Crossing Guard, The", 1995);
		assertMovieGenreFound("Horseman on the Roof, The (Hussard sur le toit, Le)", 1995);
		assertMovieGenreFound("Last of the High Kings, The (a.k.a. Summer Fling)", 1996);
		assertMovieGenreFound("Vie est belle, La (Life is Rosey)", 1987);
		assertMovieGenreFound("Old Lady Who Walked in the Sea, The (Vieille qui marchait dans la mer, La)", 1991);
		assertMovieGenreFound("G. I. Blues", 1960);
		assertMovieGenreFound("Lords of Flatbush, The", 1974);
		assertMovieGenreFound("East-West (Est-ouest)", 1999);
		assertMovieGenreFound("...And Justice for All", 1979);
		assertMovieGenreFound("Thirty-Two Short Films About Glenn Gould", 1993);
		assertMovieGenreFound("I, Worst of All (Yo, la peor de todas)", 1990);
		assertMovieGenreFound("Eye of Vichy, The (Oeil de Vichy, L')", 1993);
		assertMovieGenreFound("Dog's Life, A", 1920);
		assertMovieGenreFound("Blood In, Blood Out (a.k.a. Bound by Honor)", 1993);
		assertMovieGenreFound("Operation Condor (Feiying gaiwak)", 1990);
		assertMovieGenreFound("Fright Night Part II", 1989);
		assertMovieGenreFound("Naked Gun 2 1/2: The Smell of Fear, The", 1991);
		assertMovieGenreFound("8 1/2 Women", 1999);
		assertMovieGenreFound("8 1/2", 1963);
	}
	
	@Test
	public void testFindingMovieForDirectors() throws Exception {
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		File imdbGenresFile = new File(prop.getProperty("imdb-genres.list"));
		imdbGenresDataModel = new ImdbGenresDataModel(imdbGenresFile);
	}
	
	private void assertMovieGenreFound(String name, int year) {
		Movie<String> imdbMovie = imdbGenresDataModel.getMovieCollection().getMovie(name, year);
		assertNotNull(imdbMovie);
	}

	@Test
	public void testRemovingCommas() {
		assertEquals("Steal Big Steal Little", MovieCollection.removeCommasNotBeforeArticle("Steal Big, Steal Little"));
		assertEquals("Underneath, The", MovieCollection.removeCommasNotBeforeArticle("Underneath, The"));
		assertEquals("Horseman on the Roof, The (Hussard sur le toit, Le)", MovieCollection.removeCommasNotBeforeArticle("Horseman on the Roof, The (Hussard sur le toit, Le)"));
		assertEquals("I Worst of All (Yo la peor de todas)", MovieCollection.removeCommasNotBeforeArticle("I, Worst of All (Yo, la peor de todas)"));;
	}
	
}
