package recsys.movielens.model.imdb;

import org.apache.mahout.common.iterator.FileLineIterator;

import recsys.movielens.model.shared.Movie;
import recsys.movielens.uniter.MovieCollection;

public class ImdbDataModel {
	
	private MovieCollection<String> movieCollection;
	
	public ImdbDataModel() {
		this.movieCollection = new MovieCollection<String>();
	}

	public MovieCollection<String> getMovieCollection() {
		return movieCollection;
	}

	protected void skipLines(FileLineIterator dataFileIterator, int lines) {
		for (int j = 0; j < lines; j++) {
			if (dataFileIterator.hasNext()) {
				dataFileIterator.next();
			}
		}
	}
	
	protected boolean isAtEnd(String line) {
		return "-----------------------------------------------------------------------------".equals(line);
	}

	protected Movie<String> parseMovie(String movieString) {
		String movieName = parseMovieName(movieString);
		String movieYearString = parseMovieYear(movieString, movieName);

		Integer year = null;
		if (movieYearString.matches("[0-9]{4}")) {
			year = Integer.parseInt(movieYearString);
		}

		Movie<String> movie = movieCollection.getOrCreateMovie(movieName, year);
		return movie;
	}

	protected String parseMovieYear(String movieString, String movieName) {
		String yearString = movieString.substring(movieName.length() + 2, movieName.length() + 6);
		return yearString;
	}

	protected String parseMovieName(String movieString) {
		String[] split = movieString.trim().split(" \\(.*\\)");
		return split[0];
	}
}
