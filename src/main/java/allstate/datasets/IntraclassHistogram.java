package allstate.datasets;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.mahout.common.Pair;

public class IntraclassHistogram extends TreeMap<Pair<Integer, Integer>, Integer> {

	private static final long serialVersionUID = 1L;
	
	private Integer firstParameterNumber;
	private Integer secondParameterNumber;

	public String toJavascriptArray() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean first = true;
		for (Entry<Pair<Integer, Integer>, Integer> entry : this.entrySet()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			Pair<Integer, Integer> key = entry.getKey();
			Integer value = entry.getValue();
			sb.append("[" + key.getSecond() + ", " + key.getFirst() + ", " + value + "]");
		}
		sb.append(" ]");
		return sb.toString();
	}

	public Integer getFirstParameterNumber() {
		return firstParameterNumber;
	}

	public void setFirstParameterNumber(Integer firstParameterNumber) {
		this.firstParameterNumber = firstParameterNumber;
	}

	public Integer getSecondParameterNumber() {
		return secondParameterNumber;
	}

	public void setSecondParameterNumber(Integer secondParameterNumber) {
		this.secondParameterNumber = secondParameterNumber;
	}
}
