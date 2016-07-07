package recsys.movielens.model.movielens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import recsys.movielens.model.shared.Movie;

public class MovieLensEnrichedModel {

	private Map<Integer, Movie<Integer>> movies;
	
	private MovielensDataModel movielensDataModel;
	
	public MovieLensEnrichedModel() {
		this.movielensDataModel = new MovielensDataModel();
		this.movies = new HashMap<>();
	}
	
	public void addEnrichedMovie(String itemTitle, int itemID, int itemYear, Set<String> genres,
			Set<Integer> imdbGenres, Set<Integer> imdbDirectors, Set<Integer> imdbActors, Set<Integer> imdbActresses, Set<Integer> imdbKeywords){
		movielensDataModel.getItemIDs().add(itemID);
		movielensDataModel.getNames().put(itemID, itemTitle);
		movielensDataModel.getGenres().put(itemID, genres);
		movielensDataModel.getYears().put(itemID, itemYear);
		
		Movie<Integer> movie = new Movie<>(imdbGenres, imdbDirectors, imdbActors, imdbActresses, imdbKeywords);
		movies.put(itemID, movie);
	}
	
	public Set<Integer> getItemImdbGenres(int itemId){
		return movies.get(itemId).getGenres();
	}
	
	public Set<Integer> getItemImdbDirectors(int itemId){
		return movies.get(itemId).getDirectors();
	}
	
	public Set<Integer> getItemImdbActors(int itemId){
		return movies.get(itemId).getActors();
	}
	
	public Set<Integer> getItemImdbActresses(int itemId){
		return movies.get(itemId).getActresses();
	}
	
	public Set<Integer> getItemImdbKeywords(int itemId){
		return movies.get(itemId).getKeywords();
	}
	
	public MovielensDataModel getMovielensDataModel() {
		return movielensDataModel;
	}

	public void setMovielensDataModel(MovielensDataModel movielensDataModel) {
		this.movielensDataModel = movielensDataModel;
	}

	public List<Integer> getIds(){
		return movielensDataModel.getItemIDs();
	}
	
	public String getItemTitle(int itemId){
		return movielensDataModel.getNames().get(itemId);
	}
	
	public Integer getItemYear(int itemId){
		return movielensDataModel.getYears().get(itemId);
	}
	
	public Set<String> getItemGenres(int itemId){
		return movielensDataModel.getGenres().get(itemId);
	}
}
