package allstate.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.classifier.df.DecisionForest;
import org.apache.mahout.classifier.df.builder.DecisionTreeBuilder;
import org.apache.mahout.classifier.df.data.DataConverter;
import org.apache.mahout.classifier.df.data.Dataset;
import org.apache.mahout.classifier.df.data.Instance;
import org.apache.mahout.classifier.df.mapreduce.BuildForest;
import org.apache.mahout.classifier.df.mapreduce.Builder;
import org.apache.mahout.classifier.df.mapreduce.inmem.InMemBuilder;
import org.apache.mahout.classifier.df.tools.Describe;

import allstate.model.AllstateDataModel;
import allstate.model.Record;

public class RandomForestRecommender implements AllstateRecommender {

	private Properties prop;

	public static void main(String args[]) throws Exception {
		RandomForestRecommender randomForest = new RandomForestRecommender();
		randomForest.loadProperties();
		// randomForest.generateFileDescriptor();
		// randomForest.run();
		// 16-22
		int parameterIndex = 22;
		DecisionForest forest = randomForest.buildForest(parameterIndex, randomForest.prop.getProperty("allstate-train-created-csv-unix"),
				randomForest.prop.getProperty("allstate-train-created-info-unix"));
		randomForest.predict(randomForest.prop, forest, parameterIndex);
	}
	

	@Override
	public Map<Long, List<Integer>> recommend(AllstateDataModel model) {
		loadProperties();
		String trainFileName = prop.getProperty("allstate-train-created-csv-unix");
		String datasetFileName = prop.getProperty("allstate-train-created-info-unix");
		try {
			return predictAllParameters(trainFileName, model, datasetFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<Long, List<Integer>> predictAllParameters(String trainFileName, AllstateDataModel model, String datasetFileName) throws Exception {
		Map<Long, List<Integer>> predictions = new HashMap<>();
		for (int i = 16; i <= 22; i++) {
			int parameterIndex = i;
			DecisionForest forest = buildForest(parameterIndex, trainFileName, datasetFileName);
			predict(forest, datasetFileName, model, parameterIndex, predictions);
		}
		return predictions;
	}

	private void predict(DecisionForest forest, String datasetFileName, AllstateDataModel model, int parameterIndex, Map<Long, List<Integer>> predictions)
			throws Exception {
		Path datasetPath = new Path(datasetFileName + "P" + parameterIndex);
		Dataset dataset = Dataset.load(new Configuration(), datasetPath);
		DataConverter converter = new DataConverter(dataset);
		Random r = new Random(1);

		String line;
		System.out.println("prediction started");
		for (Entry<Long, List<Record>> entry : model.dataset.entrySet()) {
			// vyberieme vzdy prvy zaznam. podla neho odhadujeme posledny
			line = entry.getKey() + ",";
			line += entry.getValue().get(0).toString(",", "0");
			line += ",X,X,X,X,X,X,X";
			
			Instance instance = converter.convert(line);
			double prediction = forest.classify(dataset, r, instance);
			String predictedValue = dataset.getLabelString(prediction);
			Long userID = Long.parseLong(line.split(",")[0]);
			if (predictions.containsKey(userID)) {
				predictions.get(userID).add(Integer.parseInt(predictedValue));
			} else {
				predictions.put(userID, new ArrayList<Integer>());
				predictions.get(userID).add(Integer.parseInt(predictedValue));
			}
		}
	}

	private void predict(Properties prop, DecisionForest forest, int parameterIndex) throws Exception {
		Path datasetPath = new Path(prop.getProperty("allstate-train-created-info-unix") + "P" + parameterIndex);
		Dataset dataset = Dataset.load(new Configuration(), datasetPath);
		DataConverter converter = new DataConverter(dataset);
		Random r = new Random(1);

		BufferedReader reader = new BufferedReader(new FileReader(new File(prop.getProperty("allstate-test-created-csv-unix"))));
		// BufferedReader reader = new BufferedReader(new FileReader(new
		// File(prop.getProperty("allstate-train-created-csv-unix"))));
		String line;
		System.out.println("prediction started");
		int correct = 0;
		int incorrect = 0;
		while ((line = reader.readLine()) != null) {
			Instance instance = converter.convert(line);
			double prediction = forest.classify(dataset, r, instance);
			String predictedValue = dataset.getLabelString(prediction);
			String parameter = line.split(",")[parameterIndex + 1];
			System.out.println("expected: " + parameter + "\t predicted: " + predictedValue);
			if (predictedValue.equals(parameter)) {
				correct++;
			} else {
				incorrect++;
			}
		}
		System.out.println("correct: " + correct);
		System.out.println("incorrect: " + incorrect);
		reader.close();
	}

	void loadProperties() {
		prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	void generateFileDescriptor() throws Exception {
		List<String> params = new ArrayList<>();
		params.add("-p");
		params.add(prop.getProperty("allstate-train-created-csv-unix"));
		params.add("-f");
		params.add(prop.getProperty("allstate-train-created-info-unix") + "N22");
		params.add("-d");
		// params.add("3");
		// params.add("I");
		// params.add("C");
		// params.add("I");
		// params.add("C");
		// params.add("I");
		// params.add("2");
		// params.add("C");
		// params.add("N");
		// params.add("C");
		// params.add("C");
		// params.add("2");
		// params.add("N");
		// params.add("C");
		// params.add("C");
		// params.add("N");
		// params.add("7");
		// params.add("C");
		// params.add("7");
		// params.add("I");
		// params.add("L");

		params.add("17");
		params.add("I");
		params.add("7");
		params.add("N");
		params.add("7");
		params.add("I");
		params.add("L");

		String[] args = params.toArray(new String[] {});
		Describe.main(args);
	}

	void run() throws Exception {
		List<String> params = new ArrayList<>();
		params.add("-d");
		params.add(prop.getProperty("testRF"));
		params.add("-ds");
		params.add(prop.getProperty("allstate-train-created-info-unix") + "16");
		params.add("-sl");
		params.add("5");
		params.add("-p");
		params.add("-t");
		params.add("100");
		params.add("-o");
		params.add("nsl-forest");
		String[] args = params.toArray(new String[] {});
		ToolRunner.run(new Configuration(), new BuildForest(), args);
	}

	DecisionForest buildForest(int parameterIndex, String trainFileName, String datasetFileName) throws Exception {
		boolean complemented = false;
		Integer m = null;
		Integer minSplitNum = null;
		Double minVarianceProportion = null;
		Builder forestBuilder;
		Path dataPath = new Path(trainFileName);
		Path datasetPath = new Path(datasetFileName + "P" + parameterIndex);
		Long seed = 1L;
		int nbTrees = 100;
		DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder();
		if (m != null) {
			treeBuilder.setM(m);
		}
		treeBuilder.setComplemented(complemented);
		if (minSplitNum != null) {
			treeBuilder.setMinSplitNum(minSplitNum);
		}
		if (minVarianceProportion != null) {
			treeBuilder.setMinVarianceProportion(minVarianceProportion);
		}
		forestBuilder = new InMemBuilder(treeBuilder, dataPath, datasetPath, seed, new Configuration());
		DecisionForest forest = forestBuilder.build(nbTrees);
		return forest;
	}

	void predict(DecisionForest forest, String line) throws Exception {
		Path datasetPath = new Path(prop.getProperty("testRFinfo"));
		Dataset dataset = Dataset.load(new Configuration(), datasetPath);
		DataConverter converter = new DataConverter(dataset);
		Instance instance = converter.convert(line);
		Random rng = new Random(1);
		double prediction = forest.classify(dataset, rng, instance);
		System.out.println(prediction);
	}

}
