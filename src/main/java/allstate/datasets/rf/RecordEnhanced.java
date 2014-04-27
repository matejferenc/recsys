package allstate.datasets.rf;

import allstate.model.Record;

public class RecordEnhanced {

	public Record record;

	public String parameter16;
	public String parameter17;
	public String parameter18;
	public String parameter19;
	public String parameter20;
	public String parameter21;
	public String parameter22;

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public String toString(String separator, String nullValueReplacement) {
		StringBuilder sb = new StringBuilder();
		sb.append(record.toString(separator, nullValueReplacement));
		sb.append(separator + parameter16);
		sb.append(separator + parameter17);
		sb.append(separator + parameter18);
		sb.append(separator + parameter19);
		sb.append(separator + parameter20);
		sb.append(separator + parameter21);
		sb.append(separator + parameter22);
		return sb.toString();
	}

}
