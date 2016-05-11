package recsys.evaluator.abstr;

import java.util.List;

public enum IncludeMetrics {

	WEIGHTED_RMSE("wrmse"),
	RMSE("rmse"),
	TAU("tau");
	
	private String shortName;

	private IncludeMetrics(String shortName) {
		this.shortName = shortName;
	}
	
	public static IncludeMetrics fromList(List<String> argsList) {
		for (IncludeMetrics ie : IncludeMetrics.values()) {
			if (argsList.contains(ie.shortName)) {
				return ie;
			}
		}
		return null;
	}
	
	public static String getAllNames() {
		StringBuilder sb = new StringBuilder();
		for (IncludeMetrics ie : IncludeMetrics.values()) {
			if (ie != IncludeMetrics.values()[0]) {
				sb.append(", ");
			}
			sb.append(ie.shortName);
		}
		return sb.toString();
	}
}
