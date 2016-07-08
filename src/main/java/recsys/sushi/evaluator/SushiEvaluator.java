package recsys.sushi.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.IncludeAlgorithms;
import recsys.evaluator.abstr.AbstractEvaluator;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.recommender.builder.ItemAndUserAverageRecommenderBuilder;
import recsys.recommender.builder.ItemAverageRecommenderBuilder;
import recsys.recommender.builder.ItemBasedRecommenderBuilder;
import recsys.recommender.builder.ItemUserAverageRecommenderBuilder;
import recsys.recommender.builder.SlopeOneRecommenderBuilder;
import recsys.recommender.builder.SvdRecommenderBuilder;
import recsys.recommender.builder.UserAverageRecommenderBuilder;
import recsys.recommender.builder.UserBasedRecommenderBuilder;
import recsys.similarity.builder.EuclideanDistanceItemSimilarityBuilder;
import recsys.similarity.builder.EuclideanDistanceUserSimilarityBuilder;
import recsys.similarity.builder.PearsonCorrelationItemSimilarityBuilder;
import recsys.similarity.builder.PearsonCorrelationUserSimilarityBuilder;
import recsys.similarity.builder.UserSimilarityBuilder;
import recsys.sushi.dataset.SushiDataset;
import recsys.sushi.dataset.SushiItemDataModelDataset;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.recommender.builder.SushiAndUserClassificationRecommenderBuilder;
import recsys.sushi.recommender.builder.SushiGlobalAndLocalClassificationRecommenderBuilder;
import recsys.sushi.recommender.builder.SushiGlobalClassificationRecommenderBuilder;
import recsys.sushi.recommender.builder.SushiLocalClassificationRecommenderBuilder;
import recsys.sushi.similarity.builder.SushiItemSimilarityBuilder;
import recsys.sushi.similarity.builder.SushiUserSimilarityBuilder;
import recsys.sushi.similarity.builder.SushiUserWeightedSimilarityBuilder;
import recsys.sushi.similarity.builder.SushiUserWeightsSimilarityBuilder;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

/**
 * Evaluator of sushi dataset recommenders.
 */
public class SushiEvaluator extends AbstractEvaluator {
	
	private List<String> argsList;

	public SushiEvaluator(List<String> argsList) {
		this.argsList = argsList;
	}

	public static void main(String[] args) throws Exception {
		List<String> argsList = Arrays.asList(args);
		SushiEvaluator e = new SushiEvaluator(argsList);
		e.evaluate();
	}

	@Override
	public void evaluate() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		List<RecommenderBuilder> builders = createRecommenderBuilders(dataModel);
		evaluateRecommenders(dataModel, builders, argsList);
	}
	
	/**
	 * Creates RecommenderBuilders from the specified command line arguments.
	 * @param dataModel
	 * @return
	 * @throws Exception
	 */
	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel) throws Exception {
		List<RecommenderBuilder> builders = new ArrayList<>();
		SushiItemDataModel sushiDataModel = new SushiItemDataModelDataset().build();
		EnumSet<IncludeAlgorithms> includeAlgorithms = IncludeAlgorithms.fromList(argsList);
		EnumSet<IncludeProperties> includeProperties = IncludeProperties.fromList(argsList);
		IncludeUserSimilarityBuilder includeUserSimilarityBuilder = IncludeUserSimilarityBuilder.fromList(argsList);
		EnumSet<IncludeClassificators> includeClassificators = IncludeClassificators.fromList(argsList);

		assertCorrectIncludes(includeAlgorithms, includeProperties, includeClassificators);

		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED)) {

			UserSimilarityBuilder sushiUserSimilarityBuilder = null;
			if (includeUserSimilarityBuilder == IncludeUserSimilarityBuilder.SUSHI_USER_SIMILARITY_BUILDER) {
				sushiUserSimilarityBuilder = new SushiUserSimilarityBuilder(sushiDataModel, includeProperties);
			} else if (includeUserSimilarityBuilder == IncludeUserSimilarityBuilder.SUSHI_USER_WEIGHTS_SIMILARITY_BUILDER) {
				sushiUserSimilarityBuilder = new SushiUserWeightsSimilarityBuilder(sushiDataModel, includeProperties);
			} else if (includeUserSimilarityBuilder == IncludeUserSimilarityBuilder.SUSHI_USER_WEIGHTED_SIMILARITY_BUILDER) {
				sushiUserSimilarityBuilder = new SushiUserWeightedSimilarityBuilder(sushiDataModel, includeProperties);
			} else {
				throw new MissingArgumentException("When using " + IncludeAlgorithms.USER_BASED + " algorithm, you must choose one of the user similarities " + IncludeUserSimilarityBuilder.getAllNames());
			}

			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
			builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));
		}

		
		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED_EUCLIDEAN_DISTANCE)) {
			EuclideanDistanceUserSimilarityBuilder euclideanDistanceUserSimilarityBuilder = new EuclideanDistanceUserSimilarityBuilder();
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
			builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));
		}

		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED_PEARSON_CORRELATION)) {
			PearsonCorrelationUserSimilarityBuilder pearsonCorrelationUserSimilarityBuilder = new PearsonCorrelationUserSimilarityBuilder();
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
			builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));
		}
		
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_BASED_EUCLIDEAN_DISTANCE)) {
			EuclideanDistanceItemSimilarityBuilder euclideanDistanceItemSimilarityBuilder = new EuclideanDistanceItemSimilarityBuilder();
			builders.add(new ItemBasedRecommenderBuilder(euclideanDistanceItemSimilarityBuilder));
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_BASED_PEARSON_CORRELATION)) {
			PearsonCorrelationItemSimilarityBuilder pearsonCorrelationItemSimilarityBuilder = new PearsonCorrelationItemSimilarityBuilder();
			builders.add(new ItemBasedRecommenderBuilder(pearsonCorrelationItemSimilarityBuilder));
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.USER_AVERAGE)) {
			builders.add(new UserAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_AVERAGE)) {
			builders.add(new ItemAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_USER_AVERAGE)) {
			builders.add(new ItemUserAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_AND_USER_AVERAGE)) {
			builders.add(new ItemAndUserAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.SUSHI_ITEM_SIMILARITY)) {
			SushiItemSimilarityBuilder sushiItemSimilarityBuilder = new SushiItemSimilarityBuilder(sushiDataModel, includeProperties);
			builders.add(new ItemBasedRecommenderBuilder(sushiItemSimilarityBuilder));
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.GLOBAL_AND_LOCAL_CLASSIFICATION)) {
			if (includeClassificators.contains(IncludeClassificators.J48)) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_TREE)) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});
			}
			if (includeClassificators.contains(IncludeClassificators.LOGISTIC)) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});
			}
			if (includeClassificators.contains(IncludeClassificators.NAIVE_BAYES)) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_FOREST)) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});
			}
		}

		 if (includeAlgorithms.contains(IncludeAlgorithms.GLOBAL_CLASSIFICATION)) {
			if (includeClassificators.contains(IncludeClassificators.J48)) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_TREE)) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});
			}
			if (includeClassificators.contains(IncludeClassificators.LOGISTIC)) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});
			}
			if (includeClassificators.contains(IncludeClassificators.NAIVE_BAYES)) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_FOREST)) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});
			}
		}
		 
		if (includeAlgorithms.contains(IncludeAlgorithms.LOCAL_CLASSIFICATION)) {
			if (includeClassificators.contains(IncludeClassificators.J48)) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_TREE)) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});
			}
			if (includeClassificators.contains(IncludeClassificators.LOGISTIC)) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});
			}
			if (includeClassificators.contains(IncludeClassificators.NAIVE_BAYES)) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_FOREST)) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});
			}
		}

		if (includeAlgorithms.contains(IncludeAlgorithms.USER_GLOBAL_AND_LOCAL_CLASSIFICATION)) {
			if (includeClassificators.contains(IncludeClassificators.J48)) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_TREE)) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});
			}
			if (includeClassificators.contains(IncludeClassificators.LOGISTIC)) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});
			}
			if (includeClassificators.contains(IncludeClassificators.NAIVE_BAYES)) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
			}
			if (includeClassificators.contains(IncludeClassificators.RANDOM_FOREST)) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});
			}
		}

		if (includeAlgorithms.contains(IncludeAlgorithms.SVD_PLUS_PLUS)) {
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

		if (includeAlgorithms.contains(IncludeAlgorithms.SVD_ASWLR)) {
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 1, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 2, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 3, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 4, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 5, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 6, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 7, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 8, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 9, 0.001f, 10)));
			builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001f, 10)));
		}

		if (includeAlgorithms.contains(IncludeAlgorithms.SLOPE_ONE)) {
			builders.add(new SlopeOneRecommenderBuilder());
		}
	
		return builders;
	}

	/**
	 * Checking if all the necessary parameters are present as command line arguments when we use certain Recommenders.
	 * @param includeAlgorithms
	 * @param includeProperties
	 * @param includeClassificators
	 * @throws MissingArgumentException
	 */
	private void assertCorrectIncludes(EnumSet<IncludeAlgorithms> includeAlgorithms, EnumSet<IncludeProperties> includeProperties, EnumSet<IncludeClassificators> includeClassificators) throws MissingArgumentException {
		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED) || includeAlgorithms.contains(IncludeAlgorithms.SUSHI_ITEM_SIMILARITY)) {
			if (includeProperties.size() == 0) {
				throw new MissingArgumentException(
						"When using " + IncludeAlgorithms.USER_BASED.getShortName() + " or " + IncludeAlgorithms.SUSHI_ITEM_SIMILARITY.getShortName()
						+ " algorithm, you must choose at least one of properties " + 
						"[" + IncludeProperties.getAllNames() + "]");
			}
		}
		
		if (includeAlgorithms.contains(IncludeAlgorithms.GLOBAL_AND_LOCAL_CLASSIFICATION)
				|| includeAlgorithms.contains(IncludeAlgorithms.GLOBAL_CLASSIFICATION)
				|| includeAlgorithms.contains(IncludeAlgorithms.LOCAL_CLASSIFICATION)
				|| includeAlgorithms.contains(IncludeAlgorithms.USER_GLOBAL_AND_LOCAL_CLASSIFICATION)) {
			if (includeClassificators.size() == 0) {
				throw new MissingArgumentException("When using "
						+ IncludeAlgorithms.GLOBAL_AND_LOCAL_CLASSIFICATION.getShortName() + " or "
						+ IncludeAlgorithms.GLOBAL_CLASSIFICATION.getShortName() + " or "
						+ IncludeAlgorithms.LOCAL_CLASSIFICATION.getShortName() + " or "
						+ IncludeAlgorithms.USER_GLOBAL_AND_LOCAL_CLASSIFICATION.getShortName()
						+ " algorithm, you must choose at least one of classificators "
						+ "[" + IncludeClassificators.getAllNames() + "]");
			}
		}
	}

}
