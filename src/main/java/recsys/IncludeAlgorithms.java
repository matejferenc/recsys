package recsys;

import java.util.EnumSet;
import java.util.List;

public enum IncludeAlgorithms {

	USER_BASED("ub"),
	USER_BASED_EUCLIDEAN_DISTANCE("ubed"),
	USER_BASED_PEARSON_CORRELATION("ubpc"),
	ITEM_BASED_EUCLIDEAN_DISTANCE("ibed"),
	ITEM_BASED_PEARSON_CORRELATION("ibpc"),
	USER_AVERAGE("ua"),
	ITEM_AVERAGE("ia"),
	ITEM_USER_AVERAGE("iua"),
	ITEM_AND_USER_AVERAGE("iaua"),
	SUSHI_ITEM_SIMILARITY("ibs"),
	GLOBAL_AND_LOCAL_CLASSIFICATION("gl"),
	GLOBAL_CLASSIFICATION("g"),
	LOCAL_CLASSIFICATION("l"),
	USER_GLOBAL_AND_LOCAL_CLASSIFICATION("glu"),
	SVD_PLUS_PLUS("svdsgd"),
	SVD_ASWLR("svdaswlr"),
	SLOPE_ONE("so"),
	MOVIE_LENS_USER_SIMILARITY("mlus"),
	MOVIE_LENS_CONTENT_BASED("mlcb"),
	NOTEBOOKS("ntb"),
	USER_BASED_SIMILARITY("ubs");
	
	private String shortName;

	private IncludeAlgorithms(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName(){
		return shortName;
	}
	
	public static EnumSet<IncludeAlgorithms> fromList(List<String> argsList) {
		EnumSet<IncludeAlgorithms> includeAlgorithms = EnumSet.noneOf(IncludeAlgorithms.class);
		for (IncludeAlgorithms ia : IncludeAlgorithms.values()) {
			if (argsList.contains(ia.getShortName())) {
				includeAlgorithms.add(ia);
			}
		}
		return includeAlgorithms;
	}

}
