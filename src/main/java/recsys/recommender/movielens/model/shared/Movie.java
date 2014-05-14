package recsys.recommender.movielens.model.shared;

import java.util.HashSet;
import java.util.Set;

public class Movie<T> {

	private Set<T> genres;

	private Set<T> directors;
	
	private Set<T> actors;
	
	private Set<T> actresses;

	public Movie() {
		genres = new HashSet<T>();
		directors = new HashSet<T>();
		actors = new HashSet<T>();
		actresses = new HashSet<T>();
	}

	public Set<T> getGenres() {
		return genres;
	}

	public void setGenres(Set<T> genres) {
		this.genres = genres;
	}

	public Set<T> getDirectors() {
		return directors;
	}

	public void setDirectors(Set<T> directors) {
		this.directors = directors;
	}

	public Set<T> getActors() {
		return actors;
	}

	public void setActors(Set<T> actors) {
		this.actors = actors;
	}

	public Set<T> getActresses() {
		return actresses;
	}

	public void setActresses(Set<T> actresses) {
		this.actresses = actresses;
	}
}
