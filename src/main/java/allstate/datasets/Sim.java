package allstate.datasets;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import allstate.evaluator.AllstateEvaluator;
import allstate.model.AllstateDataModel;
import allstate.model.Record;
import datasets.AllstateStatsParams;

public class Sim extends Allstate {

	@Override
	public AllstateStatsParams run() throws Exception {
		long startTime = new Date().getTime();
		AllstateStatsParams params = execute();
		long endTime = new Date().getTime();
		int s = (int) ((endTime - startTime) / 1000);
		params.seconds = s;
		System.out.println("cas behu: " + s + "s");
		return params;
	}

	public AllstateStatsParams execute() throws Exception {
		String path = prop.getProperty("allstate-train-csv-unix");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");

		// simulacia vytvorenia testovacej sady
		Map<Long, Record> soldRecords = new HashMap<>();

		AllstateEvaluator.filterOutSoldRecords(model, soldRecords);
		AllstateEvaluator.shortenTestSetTransactions(model);

		AllstateStatsParams params = execute(model);
		params.title = "Allstate";
		return params;
	}

	private AllstateStatsParams execute(AllstateDataModel model) {
		AllstateStatsParams params = new AllstateStatsParams();

		params.userCount = model.dataset.size();
		params.totalRecords = countTotalRecords(model);

		TreeMap<Integer, TreeMap<String, Integer>> histograms = new TreeMap<>();

		params.aHistogram = createHistogramForColumn(model, 16, histograms);
		params.bHistogram = createHistogramForColumn(model, 17, histograms);
		params.cHistogram = createHistogramForColumn(model, 18, histograms);
		params.dHistogram = createHistogramForColumn(model, 19, histograms);
		params.eHistogram = createHistogramForColumn(model, 20, histograms);
		params.fHistogram = createHistogramForColumn(model, 21, histograms);
		params.gHistogram = createHistogramForColumn(model, 22, histograms);

		params.changeHistogram = createChangeHistogramForColumns(model, 16, 17, 18, 19, 20, 21, 22);

		params.lastDifferentHistogram = createLastDifferentHistogramForColumns(model, 16, 17, 18, 19, 20, 21, 22);

		params.transactionLengthHistogram = createTransactionLengthHistogram(model);

		addTwoParametersDependencyBubbleGraph(model, params, histograms, 6);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 7);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 8);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 10);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 11);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 12);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 13);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 14);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 15);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 16);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 17);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 18);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 19);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 20);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 21);
		addTwoParametersDependencyBubbleGraph(model, params, histograms, 22);
		
		params.parameterStringHistogram = createParameterStringHistogram(model);

		return params;
	}
}
