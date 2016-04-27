package recsys.movielens.uniter;

import java.util.List;
import java.util.Set;

import recsys.movielens.model.movielens.MovielensDataModel;

public class MovieLensMovieModel<T> {

	private MovielensDataModel movielensDataModel;
	
	private MovieCollection<T> movieCollection;

	public MovieLensMovieModel(MovielensDataModel movielensDataModel) {
		this.movielensDataModel = movielensDataModel;
		this.movieCollection = new MovieCollection<T>();
	}

	public MovieCollection<T> getMovieCollection() {
		return movieCollection;
	}

	public void setMovieCollection(MovieCollection<T> movieCollection) {
		this.movieCollection = movieCollection;
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
