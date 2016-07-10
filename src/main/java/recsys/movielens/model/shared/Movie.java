package recsys.movielens.model.shared;

import java.util.HashSet;
import java.util.Set;

public class Movie<T> {

	private final Set<T> style;

	private final Set<T> majorGroup;
	
	private final Set<T> minorGroup;
	
	private final Set<T> oiliness;
	
	private final Set<T> price;

	public Movie() {
		this(new HashSet<T>(), new HashSet<T>(), new HashSet<T>(), new HashSet<T>(), new HashSet<T>());
	}
	
	public Movie(Set<T> style, Set<T> majorGroup, Set<T> minorGroup, Set<T> oiliness, Set<T> price) {
		this.style = style;
		this.majorGroup = majorGroup;
		this.minorGroup = minorGroup;
		this.oiliness = oiliness;
		this.price = price;
	}

	public Set<T> getGenres() {
		return style;
	}

	public Set<T> getDirectors() {
		return majorGroup;
	}

	public Set<T> getActors() {
		return minorGroup;
	}

	public Set<T> getActresses() {
		return oiliness;
	}

	public Set<T> getKeywords() {
		return price;
	}
}
