package recsys.recommender.movielens.uniter;

import java.util.HashMap;

import recsys.recommender.movielens.model.shared.Movie;

public class MovieCollection<T> {

	private HashMap<MovieId, Movie<T>> movies;

	public MovieCollection() {
		movies = new HashMap<>();
	}

	public Movie<T> getMovie(String name, Integer year) {
		Movie<T> movieInternal = getMovieInternal(name, year);
		if (movieInternal != null)
			return movieInternal;
		else
			return tryFixYear(name, year);
	}

	public Movie<T> getMovieInternal(String name, Integer year) {
		name = fixName(name);
		MovieId m = new MovieId(name, year);
		if (movies.containsKey(m))
			return movies.get(m);
		else {
			Movie<T> fixed = tryFixArticle(name, year);
			if (fixed != null) {
				return fixed;
			} else {
				return tryAddArticle(name, year);
			}
		}
	}

	private Movie<T> tryAddArticle(String name, Integer year) {
		if (name.startsWith("the ") || name.startsWith("a ") || name.startsWith("an ") || name.startsWith("la ") || name.startsWith("les ") || name.startsWith("il "))
			return null;

		Movie<T> movie = getMovieInternal("the " + name, year);
		if (movie != null)
			return movie;
		movie = getMovieInternal("a " + name, year);
		if (movie != null)
			return movie;
		movie = getMovieInternal("an " + name, year);
		if (movie != null)
			return movie;
		movie = getMovieInternal("la " + name, year);
		if (movie != null)
			return movie;
		movie = getMovieInternal("les " + name, year);
		if (movie != null)
			return movie;
		movie = getMovieInternal("il " + name, year);
		if (movie != null)
			return movie;
		return null;
	}

	private Movie<T> tryFixYear(String name, Integer year) {
		Movie<T> movieYearPlus1 = getMovieInternal(name, year + 1);
		if (movieYearPlus1 != null)
			return movieYearPlus1;
		Movie<T> movieYearPlus2 = getMovieInternal(name, year + 2);
		if (movieYearPlus2 != null)
			return movieYearPlus2;
		Movie<T> movieYearMinus1 = getMovieInternal(name, year - 1);
		if (movieYearMinus1 != null)
			return movieYearMinus1;
		Movie<T> movieYearMinus2 = getMovieInternal(name, year - 2);
		if (movieYearMinus2 != null)
			return movieYearMinus2;

		return null;
	}

	private Movie<T> tryFixParenthesis(String name, Integer year) {
		if (name.matches(".*\\(.*\\)")) {
			String[] parts = name.split("\\(");
			String firstName = parts[0];
			String secondName = parts[1];
			secondName = secondName.substring(0, secondName.length() - 1);
			Movie<T> movie1 = getMovieInternal(firstName, year);
			if (movie1 != null) {
				return movie1;
			} else {
				return getMovieInternal(secondName, year);
			}
		} else if (name.matches(".*:.*")) {
			String[] parts = name.split("\\:");
			String firstName = parts[0];
			String secondName = parts[1];
			secondName = secondName.substring(0, secondName.length());
			Movie<T> movie1 = getMovieInternal(firstName, year);
			if (movie1 != null) {
				return movie1;
			} else {
				return getMovieInternal(secondName, year);
			}
		}
		return null;
	}

	private Movie<T> tryFixArticle(String name, Integer year) {
		Movie<T> tryFixParenthesis = tryFixParenthesis(name, year);
		if(tryFixParenthesis != null)
			return tryFixParenthesis;
		
		if (name.endsWith(", the")) {
			String alteredName = name.substring(0, name.length() - 5);
			return getMovieInternal(alteredName, year);
		} else if (name.endsWith(", les")) {
			String alteredName = name.substring(0, name.length() - 5);
			return getMovieInternal(alteredName, year);
		} else if (name.endsWith(", il")) {
			String alteredName = name.substring(0, name.length() - 4);
			return getMovieInternal(alteredName, year);
		} else if (name.endsWith(", la")) {
			String alteredName = name.substring(0, name.length() - 4);
			return getMovieInternal(alteredName, year);
		} else if (name.endsWith(", an")) {
			String alteredName = name.substring(0, name.length() - 4);
			return getMovieInternal(alteredName, year);
		} else if (name.endsWith(", a")) {
			String alteredName = name.substring(0, name.length() - 3);
			return getMovieInternal(alteredName, year);
		} else {
			return null;
		}
	}

	public Movie<T> getOrCreateMovie(String name, Integer year) {
		name = fixName(name);
		MovieId m = new MovieId(name, year);
		if (movies.containsKey(m))
			return movies.get(m);
		else {
			Movie<T> newMovie = new Movie<T>();
			movies.put(m, newMovie);
			return newMovie;
		}
	}

	private String fixName(String name) {
		return name.toLowerCase().trim().replace("!", "").replace("\"", "");
	}
}
