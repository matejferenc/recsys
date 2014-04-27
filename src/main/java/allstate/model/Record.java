package allstate.model;

import java.util.ArrayList;

public class Record extends ArrayList<String> {

	private static final long serialVersionUID = 1L;

	public String toString(String separator, String nullValueReplacement) {
		StringBuilder sb = new StringBuilder();
		for (String parameter : this) {
			if(parameter == null) {
				parameter = nullValueReplacement;
			}
			sb.append("" + parameter + ",");
		}
		String string = sb.toString();
		String substring = string.substring(0, string.length() - 1);
		return substring;
	}

}
