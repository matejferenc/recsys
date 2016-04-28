package recsys.sushi.evaluator;

import java.util.List;

/**
 * Enum specifying which similarity builder will be used.
 *
 */
public enum IncludeUserSimilarityBuilder {
	
	SUSHI_USER_SIMILARITY_BUILDER("s"),
	SUSHI_USER_WEIGHTS_SIMILARITY_BUILDER("w"),
	SUSHI_USER_WEIGHTED_SIMILARITY_BUILDER("wd");

	private String shortName;

	private IncludeUserSimilarityBuilder(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public static IncludeUserSimilarityBuilder fromList(List<String> argsList) {
		for (IncludeUserSimilarityBuilder iusb : IncludeUserSimilarityBuilder.values()) {
			if (argsList.contains(iusb.shortName)) {
				return iusb;
			}
		}
		return null;
	}

	public static String getAllNames() {
		StringBuilder sb = new StringBuilder();
		for (IncludeUserSimilarityBuilder iusb : IncludeUserSimilarityBuilder.values()) {
			if (iusb != IncludeUserSimilarityBuilder.values()[0]) {
				sb.append(", ");
			}
			sb.append(iusb.shortName);
		}
		return sb.toString();
	}
}
