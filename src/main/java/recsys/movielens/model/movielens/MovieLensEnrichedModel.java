package recsys.movielens.model.movielens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import recsys.movielens.model.shared.Movie;

public class MovieLensEnrichedModel {

	private Map<Long, Movie<Integer>> movies;
	
	private MovielensDataModel movielensDataModel;
	
	public MovieLensEnrichedModel() {
		this.movielensDataModel = new MovielensDataModel();
		this.movies = new HashMap<>();
	}
	
	public void addEnrichedMovie(String itemTitle, long itemID, int itemYear, Set<String> style,
			Set<Integer> imdbGenres, Set<Integer> imdbDirectors, Set<Integer> imdbActors, Set<Integer> imdbActresses, Set<Integer> imdbKeywords){
		movielensDataModel.getItemIDs().add(itemID);
		movielensDataModel.getNames().put(itemID, itemTitle);
		movielensDataModel.getGenres().put(itemID, style);
		movielensDataModel.getYears().put(itemID, itemYear);
		
		Movie<Integer> movie = new Movie<>(imdbGenres, imdbDirectors, imdbActors, imdbActresses, imdbKeywords);
		movies.put(itemID, movie);
	}
	
	public Set<Integer> getItemImdbGenres(long itemId){
		Movie<Integer> movie = getMovie(itemId);
		return movie.getGenres();
	}
	
	public Set<Integer> getItemImdbDirectors(long itemId){
		Movie<Integer> movie = getMovie(itemId);
		return movie.getDirectors();
	}
	
	public Set<Integer> getItemImdbActors(long itemId){
		Movie<Integer> movie = getMovie(itemId);
		return movie.getActors();
	}
	
	public Set<Integer> getItemImdbActresses(long itemId){
		Movie<Integer> movie = getMovie(itemId);
		return movie.getActresses();
	}
	
	public Set<Integer> getItemImdbKeywords(long itemId){
		Movie<Integer> movie = getMovie(itemId);
		return movie.getKeywords();
	}
	
	private Movie<Integer> getMovie(long itemId) {
		Movie<Integer> movie = movies.get(itemId);
		return movie;
	}
	
	public MovielensDataModel getMovielensDataModel() {
		return movielensDataModel;
	}

	public void setMovielensDataModel(MovielensDataModel movielensDataModel) {
		this.movielensDataModel = movielensDataModel;
	}

	public List<Long> getIds(){
		return movielensDataModel.getItemIDs();
	}
	
	public String getItemTitle(long itemId){
		return movielensDataModel.getNames().get(itemId);
	}
	
	public Integer getItemYear(long itemId){
		return movielensDataModel.getYears().get(itemId);
	}
	
	public Set<String> getItemGenres(long itemId){
		return movielensDataModel.getGenres().get(itemId);
	}
}
