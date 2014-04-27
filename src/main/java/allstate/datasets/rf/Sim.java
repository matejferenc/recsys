package allstate.datasets.rf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Properties;

import allstate.evaluator.AllstateEvaluator;
import allstate.model.AllstateDataModel;
import allstate.model.Record;

public class Sim {
	
	protected Properties prop;
	
	public Sim() {
		prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Sim().run();
	}
	
	private void run() throws Exception {
		String path = prop.getProperty("allstate-train-csv-unix");
		File dataFile = new File(path);
		AllstateDataModel model = new AllstateDataModel(dataFile, ",", "NA");

		Map<Long, Record> soldRecords = new HashMap<>();

		AllstateEvaluator.filterOutSoldRecords(model, soldRecords);
		AllstateEvaluator.extractFirstRecordOnly(model);
		
		double ratio = 0.7;
		Random r = new Random(0);
		
		List<Long> testUserIds = new ArrayList<>();
		List<Long> trainUserIds = AllstateEvaluator.getRandomTrainUserIds(model, ratio, r, testUserIds);
		
		Map<Long, RecordEnhanced> trainDataset = createTrainDataset(trainUserIds, model, soldRecords);
		Map<Long, RecordEnhanced> testDataset = createTrainDataset(testUserIds, model, soldRecords);
		
		String trainDatasetPath = prop.getProperty("allstate-train-created-csv-unix");
		writeDatasetToFile(trainDataset, trainDatasetPath);
		String testDatasetPath = prop.getProperty("allstate-test-created-csv-unix");
		writeDatasetToFile(testDataset, testDatasetPath);

	}

	private void writeDatasetToFile(Map<Long, RecordEnhanced> dataset, String datasetPath) throws Exception {
		FileWriter fileWriter = new FileWriter(datasetPath);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		for (Entry<Long, RecordEnhanced> entry : dataset.entrySet()) {
			writer.write("" + entry.getKey() + ",");
			writer.write(entry.getValue().toString(",", "0"));
			writer.newLine();
		}
		writer.close();
	}

	private Map<Long, RecordEnhanced> createTrainDataset(List<Long> trainUserIds, AllstateDataModel model, Map<Long, Record> soldRecords) {
		Map<Long, RecordEnhanced> trainDataset = new HashMap<>();
		for (Long userId : trainUserIds) {
			RecordEnhanced recordEnhanced = new RecordEnhanced();
			recordEnhanced.record = model.dataset.get(userId).get(0);
			recordEnhanced.parameter16 = soldRecords.get(userId).get(16);
			recordEnhanced.parameter17 = soldRecords.get(userId).get(17);
			recordEnhanced.parameter18 = soldRecords.get(userId).get(18);
			recordEnhanced.parameter19 = soldRecords.get(userId).get(19);
			recordEnhanced.parameter20 = soldRecords.get(userId).get(20);
			recordEnhanced.parameter21 = soldRecords.get(userId).get(21);
			recordEnhanced.parameter22 = soldRecords.get(userId).get(22);
			trainDataset.put(userId, recordEnhanced);
		}
		return trainDataset;
	}
}
