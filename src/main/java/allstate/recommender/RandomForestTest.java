package allstate.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

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

public class RandomForestTest {

	private Properties prop;

	private static String test1 = "0,tcp,ftp_data,SF,491,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0.00,0.00,0.00,0.00,1.00,0.00,0.00,150,25,0.17,0.03,0.17,0.00,0.00,0.00,0.05,0.00,normal";
	private static String test2 = "0,udp,other,SF,146,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,13,1,0.00,0.00,0.00,0.00,0.08,0.15,0.00,255,1,0.00,0.60,0.88,0.00,0.00,0.00,0.00,0.00,normal";
	private static String test3 = "0,tcp,private,S0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,123,6,1.00,1.00,0.00,0.00,0.05,0.07,0.00,255,26,0.10,0.05,0.00,0.00,1.00,1.00,0.00,0.00,normal";
	private static String test4 = "0,tcp,http,SF,232,8153,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,5,5,0.20,0.20,0.00,0.00,1.00,0.00,0.00,30,255,1.00,0.00,0.03,0.04,0.03,0.01,0.00,0.01,normal";
	private static String test5 = "0,tcp,http,SF,199,420,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,30,32,0.00,0.00,0.00,0.00,1.00,0.00,0.09,255,255,1.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,normal";
	private static String test6 = "0,tcp,private,REJ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,121,19,0.00,0.00,1.00,1.00,0.16,0.06,0.00,255,19,0.07,0.07,0.00,0.00,0.00,0.00,1.00,1.00,normal";

	public static void main(String args[]) throws Exception {
		RandomForestTest randomForest = new RandomForestTest();
		randomForest.loadProperties();
		// randomForest.generateFileDescriptor();
		// randomForest.run();
		DecisionForest forest = randomForest.buildForest();
		randomForest.predict(forest, test1);
		randomForest.predict(forest, test2);
		randomForest.predict(forest, test3);
		randomForest.predict(forest, test4);
		randomForest.predict(forest, test5);
		randomForest.predict(forest, test6);
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
		params.add(prop.getProperty("testRF"));
		params.add("-f");
		params.add(prop.getProperty("testRFinfo"));
		params.add("-d");
		params.add("N");
		params.add("3");
		params.add("C");
		params.add("2");
		params.add("N");
		params.add("C");
		params.add("4");
		params.add("N");
		params.add("C");
		params.add("8");
		params.add("N");
		params.add("2");
		params.add("C");
		params.add("19");
		params.add("N");
		params.add("L");
		String[] args = params.toArray(new String[] {});
		Describe.main(args);
	}

	void run() throws Exception {
		List<String> params = new ArrayList<>();
		params.add("-d");
		params.add(prop.getProperty("testRF"));
		params.add("-ds");
		params.add(prop.getProperty("testRFinfo"));
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

	DecisionForest buildForest() throws Exception {
		boolean complemented = false;
		Integer m = null;
		Integer minSplitNum = null;
		Double minVarianceProportion = null;
		Builder forestBuilder;
		Path dataPath = new Path(prop.getProperty("testRF"));
		Path datasetPath = new Path(prop.getProperty("testRFinfo"));
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
