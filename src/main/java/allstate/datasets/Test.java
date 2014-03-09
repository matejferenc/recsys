package allstate.datasets;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import allstate.model.AllstateDataModel;
import datasets.AllstateStatsParams;

public class Test extends Allstate {

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
		String path = prop.getProperty("allstate-test-csv");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");

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

		// histogramy group size (6) vzhladom ku vsetkym parametrom
		List<IntraclassHistogram> row = null;
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 6, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy homeowner (7) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 7, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy car age (8) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 8, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy age oldest (11) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 10, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy age oldest (12) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 12, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy age oldest (13) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 13, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy age oldest (14) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 14, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		// histogramy age oldest (15) vzhladom ku vsetkym parametrom
		row = new ArrayList<>();
		row.addAll(createIntraclassHistograms(model, 15, histograms, 16, 17, 18, 19, 20, 21, 22));
		params.interclassHistograms.add(row);

		return params;
	}
}
