package datasets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import allstate.datasets.IntraclassHistogram;

public class AllstateStatsParams {

	public String title;
	public int userCount;
	public int totalRecords;
	public int seconds;

	public TreeMap<String, Integer> aHistogram;
	public TreeMap<String, Integer> bHistogram;
	public TreeMap<String, Integer> cHistogram;
	public TreeMap<String, Integer> dHistogram;
	public TreeMap<String, Integer> eHistogram;
	public TreeMap<String, Integer> fHistogram;
	public TreeMap<String, Integer> gHistogram;

	public TreeMap<Integer, Float> changeHistogram;
	
	public TreeMap<Integer, Float> lastDifferentHistogram;
	
	public TreeMap<Integer, Double> transactionLengthHistogram;
	
	public List<List<IntraclassHistogram>> interclassHistograms = new ArrayList<>();
	
	public Map<String, Integer> parameterStringHistogram;

	
	
	public List<List<IntraclassHistogram>> getInterclassHistograms() {
		return interclassHistograms;
	}

	public TreeMap<Integer, Float> getLastDifferentHistogram() {
		return lastDifferentHistogram;
	}

	public TreeMap<Integer, Double> getTransactionLengthHistogram() {
		return transactionLengthHistogram;
	}
	
	public TreeMap<Integer, Float> getChangeHistogram() {
		return changeHistogram;
	}

	public TreeMap<String, Integer> getaHistogram() {
		return aHistogram;
	}

	public TreeMap<String, Integer> getbHistogram() {
		return bHistogram;
	}

	public TreeMap<String, Integer> getcHistogram() {
		return cHistogram;
	}

	public TreeMap<String, Integer> getdHistogram() {
		return dHistogram;
	}

	public TreeMap<String, Integer> geteHistogram() {
		return eHistogram;
	}

	public TreeMap<String, Integer> getfHistogram() {
		return fHistogram;
	}

	public TreeMap<String, Integer> getgHistogram() {
		return gHistogram;
	}

	public String getTitle() {
		return title;
	}

	public int getUserCount() {
		return userCount;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public int getSeconds() {
		return seconds;
	}

	public Map<String, Integer> getParameterStringHistogram() {
		return parameterStringHistogram;
	}

	public void setParameterStringHistogram(Map<String, Integer> parameterStringHistogram) {
		this.parameterStringHistogram = parameterStringHistogram;
	}

}
