package recsys.sushi.evaluator;

import java.util.EnumSet;
import java.util.List;

/**
 * Enum of properties in sushi dataset.
 */
public enum IncludeProperties {

	STYLE("s", "style"),
	MAJOR("j", "major"),
	MINOR("n", "minor"),
	OILINESS("o", "oiliness"),
	PRICE("p", "price"),
	GENDER("g", "gender"),
	AGE("a", "age"),
	REGION15("r15", "region15"),
	REGION_CURRENT("r_c", "regionCurrent"),
	PREFECTURE15("p15", "prefecture15"),
	PREFECTURE_CURRENT("p_c", "prefectureCurrent"),
	EAST_WEST15("ew15", "eastWest15"),
	EAST_WEST_CURRENT("ew_c", "eastWestCurrent");
	
	private String shortName;
	private String IntegerName;

	private IncludeProperties(String shortName, String IntegerName) {
		this.shortName = shortName;
		this.IntegerName = IntegerName;
	}
	
	public String getShortName(){
		return shortName;
	}
	
	public static EnumSet<IncludeProperties> fromList(List<String> argsList) {
		EnumSet<IncludeProperties> includeProperties = EnumSet.noneOf(IncludeProperties.class);
		for (IncludeProperties ip : IncludeProperties.values()) {
			if (argsList.contains(ip.IntegerName)) {
				includeProperties.add(ip);
			}
		}
		return includeProperties;
	}

	public static String getAllNames() {
		StringBuilder sb = new StringBuilder();
		for (IncludeProperties ip : IncludeProperties.values()) {
			if (ip != IncludeProperties.values()[0]) {
				sb.append(", ");
			}
			sb.append(ip.IntegerName);
		}
		return sb.toString();
	}
}
