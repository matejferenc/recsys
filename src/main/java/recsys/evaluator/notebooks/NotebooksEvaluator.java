package recsys.evaluator.notebooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.average.ItemAndUserAverageRecommenderBuilder;
import recsys.average.ItemUserAverageRecommenderBuilder;
import recsys.dataset.NotebooksDataModelDataset;
import recsys.dataset.NotebooksDataset;
import recsys.evaluator.AbstractEvaluator;
import recsys.evaluator.builder.EuclideanDistanceItemSimilarityBuilder;
import recsys.evaluator.builder.EuclideanDistanceUserSimilarityBuilder;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.evaluator.builder.PearsonCorrelationUserSimilarityBuilder;
import recsys.itemAverage.ItemAverageRecommenderBuilder;
import recsys.itemBased.ItemBasedRecommenderBuilder;
import recsys.recommender.notebooks.NotebooksDataModel;
import recsys.recommender.sushi.recommender.NotebooksRecommenderBuilder;
import recsys.recommender.sushi.recommender.NotebooksUserSimilarity.Include;
import recsys.recommender.sushi.recommender.NotebooksUserSimilarityBuilder;
import recsys.slopeone.SlopeOneRecommenderBuilder;
import recsys.svd.SvdRecommenderBuilder;
import recsys.userAverage.UserAverageRecommenderBuilder;
import recsys.userBased.UserBasedRecommenderBuilder;

public class NotebooksEvaluator extends AbstractEvaluator {

	private static List<String> argsList;

	public static void main(String[] args) throws Exception {
		argsList = Arrays.asList(args);
		NotebooksEvaluator e = new NotebooksEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		List<RecommenderBuilder> builders = createRecommenderBuilders(dataModel);
		evaluateRecommenders(dataModel, builders, argsList);
	}

	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel) throws Exception, TasteException {
		List<RecommenderBuilder> builders = new ArrayList<>();
		
		NotebooksDataModel notebooksDataModel = new NotebooksDataModelDataset().build();
		
		EnumSet<Include> includeProperties = EnumSet.noneOf(Include.class);
		if (argsList.contains("hdd"))
			includeProperties.add(Include.HDD);
		if (argsList.contains("manufacturer"))
			includeProperties.add(Include.MANUFACTURER);
		if (argsList.contains("display"))
			includeProperties.add(Include.DISPLAY);
		if (argsList.contains("ram"))
			includeProperties.add(Include.RAM);
		if (argsList.contains("price"))
			includeProperties.add(Include.PRICE);

		if (argsList.contains("ntb")) {
			builders.add(new NotebooksRecommenderBuilder(notebooksDataModel, includeProperties));
		}
		
		if (argsList.contains("ubs")) {
			NotebooksUserSimilarityBuilder notebooksUserSimilarityBuilder = new NotebooksUserSimilarityBuilder(notebooksDataModel, includeProperties);
	
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(2)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(3)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(4)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(6)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(7)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(8)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(9)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
			builders.add(new UserBasedRecommenderBuilder(notebooksUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
		}
		
		if (argsList.contains("ubed")) {
			EuclideanDistanceUserSimilarityBuilder euclideanDistanceUserSimilarityBuilder = new EuclideanDistanceUserSimilarityBuilder();
	
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(2)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(3)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(4)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(6)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(7)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(8)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(9)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
		}

		if (argsList.contains("ubpc")) {
			PearsonCorrelationUserSimilarityBuilder pearsonCorrelationUserSimilarityBuilder = new PearsonCorrelationUserSimilarityBuilder();
	
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(2)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(3)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(4)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(6)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(7)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(8)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(9)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
		}

		if (argsList.contains("ibed")) {
			EuclideanDistanceItemSimilarityBuilder euclideanDistanceItemSimilarityBuilder = new EuclideanDistanceItemSimilarityBuilder();
			builders.add(new ItemBasedRecommenderBuilder(euclideanDistanceItemSimilarityBuilder));
		}
		if (argsList.contains("ua")) {
			builders.add(new UserAverageRecommenderBuilder());
		}
		if (argsList.contains("ia")) {
			builders.add(new ItemAverageRecommenderBuilder());
		}
		if (argsList.contains("iua")) {
			builders.add(new ItemUserAverageRecommenderBuilder());
		}
		if (argsList.contains("iaua")) {
			builders.add(new ItemAndUserAverageRecommenderBuilder());
		}
		
		if (argsList.contains("svdsgd")) {
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 1, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 2, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 3, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 4, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 5, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 6, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 7, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 8, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 9, 10)));
			builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 10, 10)));
		}
		
		if (argsList.contains("svdalswr")) {
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 1, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 2, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 3, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 4, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 5, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 6, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 7, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 8, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 9, 0.001, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 10)));
		}
		
//		builders.add(new NotebooksGlobalAndLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new J48();}});
//		builders.add(new NotebooksGlobalAndLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new RandomTree();}});
//		builders.add(new NotebooksGlobalAndLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new Logistic();}});
//		builders.add(new NotebooksGlobalAndLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
//		builders.add(new NotebooksGlobalAndLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new RandomForest();}});

//		 builders.add(new NotebooksGlobalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new J48();}});
//		 builders.add(new NotebooksGlobalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new RandomTree();}});
//		 builders.add(new NotebooksGlobalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new Logistic();}});
//		 builders.add(new NotebooksGlobalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
//		 builders.add(new NotebooksGlobalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new RandomForest();}});

//		 builders.add(new NotebooksLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new J48();}});
//		 builders.add(new NotebooksLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new RandomTree();}});
//		 builders.add(new NotebooksLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new Logistic();}});
//		 builders.add(new NotebooksLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
//		 builders.add(new NotebooksLocalClassificationRecommenderBuilder(notebooksDataModel) {public Classifier createClassifier() {return new RandomForest();}});

		
		if (argsList.contains("so")) {
			builders.add(new SlopeOneRecommenderBuilder());
		}
		return builders;
	}

}
