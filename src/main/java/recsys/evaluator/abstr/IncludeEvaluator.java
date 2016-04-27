package recsys.evaluator.abstr;

import java.util.List;

import recsys.sushi.evaluator.IncludeUserSimilarityBuilder;

public enum IncludeEvaluator {

	WEIGHTED_RMSE("wrmse"),
	RMSE("rmse"),
	TAU("tau");
	
	private String shortName;

	private IncludeEvaluator(String shortName) {
		this.shortName = shortName;
	}
	
	public static IncludeEvaluator fromList(List<String> argsList) {
		for (IncludeEvaluator ie : IncludeEvaluator.values()) {
			if (argsList.contains(ie.shortName)) {
				return ie;
			}
		}
		return null;
	}
	
	public static String getAllNames() {
		StringBuilder sb = new StringBuilder();
		for (IncludeEvaluator ie : IncludeEvaluator.values()) {
			if (ie != IncludeEvaluator.values()[0]) {
				sb.append(", ");
			}
			sb.append(ie.shortName);
		}
		return sb.toString();
	}
}
