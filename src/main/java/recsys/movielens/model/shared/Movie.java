package recsys.movielens.model.shared;

import java.util.HashSet;
import java.util.Set;

public class Movie<T> {

	private final Set<T> genres;

	private final Set<T> directors;
	
	private final Set<T> actors;
	
	private final Set<T> actresses;
	
	private final Set<T> keywords;

	public Movie() {
		this(new HashSet<T>(), new HashSet<T>(), new HashSet<T>(), new HashSet<T>(), new HashSet<T>());
	}
	
	public Movie(Set<T> genres, Set<T> directors, Set<T> actors, Set<T> actresses, Set<T> keywords) {
		this.genres = genres;
		this.directors = directors;
		this.actors = actors;
		this.actresses = actresses;
		this.keywords = keywords;
	}

	public Set<T> getGenres() {
		return genres;
	}

	public Set<T> getDirectors() {
		return directors;
	}

	public Set<T> getActors() {
		return actors;
	}

	public Set<T> getActresses() {
		return actresses;
	}

	public Set<T> getKeywords() {
		return keywords;
	}
}
