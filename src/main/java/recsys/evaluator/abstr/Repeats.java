package recsys.evaluator.abstr;

import java.util.List;

public class Repeats {

	public static int fromList(List<String> argsList) {
		for (String s : argsList) {
			if (s.startsWith("repeats=")) {
				return Integer.parseInt(s.substring(8));
			}
		}
		throw new IllegalArgumentException("No repeats included in parameters. Please include repeats using 'repeats=X'");
	}


}
