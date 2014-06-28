package recsys.evaluator.sushi;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.dataset.SushiDataset;
import recsys.dataset.SushiItemDataModelDataset;
import recsys.evaluator.AbstractDifferenceRecommenderFairListEvaluator;
import recsys.evaluator.TauRecommenderFairListEvaluator;
import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.recommender.SushiAndUserClassificationRecommenderBuilder;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class SushiEvaluator {

	public static void main(String[] args) throws Exception {
		SushiEvaluator e = new SushiEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		DataModel dataModel = new SushiDataset().build();

		List<RecommenderBuilder> builders = new ArrayList<>();

		SushiItemDataModel sushiDataModel = new SushiItemDataModelDataset().build();
//		 UserSimilarityBuilder sushiUserSimilarityBuilder = new SushiUserSimilarityBuilder(sushiDataModel);
//		UserSimilarityBuilder sushiUserSimilarityBuilder = new SushiUserWeightsSimilarityBuilder(sushiDataModel);
		// UserSimilarityBuilder sushiUserSimilarityBuilder = new SushiUserWeightedSimilarityBuilder(sushiDataModel);

//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));

//		 EuclideanDistanceUserSimilarityBuilder euclideanDistanceUserSimilarityBuilder = new EuclideanDistanceUserSimilarityBuilder();
////		//
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
//		 builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));

//		 PearsonCorrelationUserSimilarityBuilder pearsonCorrelationUserSimilarityBuilder = new PearsonCorrelationUserSimilarityBuilder();
//		 //
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
//		 builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));

		// UserSimilarityBuilder uncenteredCosineUserSimilarityBuilder = new UncenteredCosineUserSimilarityBuilder();
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(60)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(70)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(80)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(90)));
		// builders.add(new UserBasedRecommenderBuilder(uncenteredCosineUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));
		//
		//
		// builders.add(new SushiContentBasedRecommenderBuilder(sushiDataModel));

//		EuclideanDistanceItemSimilarityBuilder euclideanDistanceItemSimilarityBuilder = new EuclideanDistanceItemSimilarityBuilder();
//		builders.add(new ItemBasedRecommenderBuilder(euclideanDistanceItemSimilarityBuilder));
////		//
//		 PearsonCorrelationItemSimilarityBuilder pearsonCorrelationItemSimilarityBuilder = new PearsonCorrelationItemSimilarityBuilder();
//		builders.add(new ItemBasedRecommenderBuilder(pearsonCorrelationItemSimilarityBuilder));
////
//		builders.add(new UserAverageRecommenderBuilder());
//		builders.add(new ItemAverageRecommenderBuilder());
//		builders.add(new ItemUserAverageRecommenderBuilder());
//		builders.add(new ItemAndUserAverageRecommenderBuilder());
////
//		SushiItemSimilarityBuilder sushiItemSimilarityBuilder = new SushiItemSimilarityBuilder(sushiDataModel);
//		 builders.add(new ItemBasedRecommenderBuilder(sushiItemSimilarityBuilder));

//		 builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});//1.211
//		 builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});//1.199
//		 builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});//1.226
//		 builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});//1.242
//		 builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});//1.199
//		 
//
//		 builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});//1.211
//		 builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});//1.199
//		 builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});//1.226
//		 builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});//1.242
//		 builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});//1.199
//
//		 builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});//1.369
//		 builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});//1.390
//		 builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});//1.530
//		 builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});//1.503
//		 builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});//1.276
//
		 builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});//1.207
		 builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});//1.246
		 builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});//1.234
		 builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});//1.239
		 builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});//1.172

//		 // builders.add(new SushiCombinedGlobalClassificationRecommenderBuilder(sushiDataModel));
//
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 1, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 2, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 3, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 4, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 5, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 6, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 7, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 8, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 9, 10)));
//		 builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 10, 10)));

		// builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 10, 100)));
		// builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 100, 100)));

//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 1, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 2, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 3, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 4, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 5, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 6, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 7, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 8, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 9, 0.001, 10)));
//		 builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 10)));

		// builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 100)));
		// builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 1000)));
		// builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 100, 0.001, 10)));

		// builders.add(new SlopeOneRecommenderBuilder());

		StringBuilder sb = new StringBuilder();

		sb.append("minimum possible preference: " + dataModel.getMinPreference());
		sb.append("\n");
		sb.append("maximum possible preference: " + dataModel.getMaxPreference());
		sb.append("\n");

		double trainingPercentage = 0.7;
		double evaluationPercentage = 0.3;
		sb.append("Training percentage: " + trainingPercentage);
		sb.append("\n");
		sb.append("Evaluation percentage: " + evaluationPercentage);
		sb.append("\n");

		for (RecommenderBuilder builder : builders) {
			Date start = new Date();

			
			int totalEstimated = 0;
			int totalNotEstimated = 0;
			List<Double> evaluated = new ArrayList<>();
			
			for (int i = 0; i < 100; i++) {

//				AbstractDifferenceRecommenderFairEvaluator e = new WeightedRMSRecommenderFairEvaluator(5, 1);
//				AbstractDifferenceRecommenderFairEvaluator e = new RMSRecommenderFairEvaluator();
				
				AbstractDifferenceRecommenderFairListEvaluator e = new TauRecommenderFairListEvaluator();
				
				double evaluate = e.evaluate(builder, dataModel, trainingPercentage, evaluationPercentage);
				evaluated.add(evaluate);
				
				totalEstimated = e.getEstimateCounter().intValue();
				totalNotEstimated = e.getNoEstimateCounter().intValue();

			}
			Date end = new Date();

			sb.append("Using builder: " + builder.getName());
			sb.append("\n");
			sb.append("Estimated in: " + totalEstimated + " cases");
			sb.append("\n");
			sb.append("Not estimated in: " + totalNotEstimated + " cases");
			sb.append("\n");
			sb.append("Score: " + listToString(evaluated));
			sb.append("\n");
			sb.append("Time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
			sb.append("\n");
			sb.append("-------------------------\n");

			builder.freeReferences();
		}

		System.out.println(sb.toString());
	}

	private String listToString(List<Double> evaluate) {
		String s = "";
		NumberFormat formatter = new DecimalFormat("#0.000");   
		for (Double double1 : evaluate) {
			s += formatter.format(double1) + "\t";
		}
		return s;
	}
}
