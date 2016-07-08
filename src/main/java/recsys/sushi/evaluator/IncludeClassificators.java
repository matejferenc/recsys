package recsys.sushi.evaluator;

import java.util.EnumSet;
import java.util.List;

/**
 * Enum with classificators we use with sushi dataset.
 *
 */
public enum IncludeClassificators {

	J48("j48"),
	RANDOM_TREE("randomTree"),
	LOGISTIC("logistic"),
	NAIVE_BAYES("naiveBayes"),
	RANDOM_FOREST("randomForest");
	
	private String IntegerName;

	private IncludeClassificators(String IntegerName) {
		this.IntegerName = IntegerName;
	}
	
	public static EnumSet<IncludeClassificators> fromList(List<String> argsList) {
		EnumSet<IncludeClassificators> includeClassificators = EnumSet.noneOf(IncludeClassificators.class);
		for (IncludeClassificators ic : IncludeClassificators.values()) {
			if (argsList.contains(ic.IntegerName)) {
				includeClassificators.add(ic);
			}
		}
		return includeClassificators;
	}
	
	public static String getAllNames() {
		StringBuilder sb = new StringBuilder();
		for (IncludeClassificators ic : IncludeClassificators.values()) {
			if (ic != IncludeClassificators.values()[0]) {
				sb.append(", ");
			}
			sb.append(ic.IntegerName);
		}
		return sb.toString();
	}
}
