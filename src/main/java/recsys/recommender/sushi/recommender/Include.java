package recsys.recommender.sushi.recommender;

public enum Include {

	STYLE("s"),
	MAJOR("j"),
	MINOR("n"),
	OILINESS("o"),
	PRICE("p"),
	GENDER("g"),
	AGE("a"),
	REGION15("r15"),
	REGION_CURRENT("r_c"),
	PREFECTURE15("p15"),
	PREFECTURE_CURRENT("p_c"),
	EAST_WEST15("ew15"),
	EAST_WEST_CURRENT("ew_c");
	
	private String shortName;

	private Include(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName(){
		return shortName;
	}
}
