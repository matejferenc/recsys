package recsys.movielens.uniter;

public class MovieId {

	private String name;

	private Integer year;

	public MovieId(String name, Integer year) {
		this.setName(fixName(name));
		this.setYear(year);
	}

	private String fixName(String name) {
		return name.toLowerCase().trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MovieId) {
			MovieId m = (MovieId) obj;
			if (year == null) {
				return name.equals(fixName(m.name));
			} else {
				return name.equals(fixName(m.name)) && year.equals(m.year);
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
