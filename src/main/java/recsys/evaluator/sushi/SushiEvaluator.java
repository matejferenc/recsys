package recsys.evaluator.sushi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.average.ItemAndUserAverageRecommenderBuilder;
import recsys.average.ItemUserAverageRecommenderBuilder;
import recsys.dataset.SushiDataset;
import recsys.dataset.SushiItemDataModelDataset;
import recsys.evaluator.AbstractEvaluator;
import recsys.evaluator.builder.EuclideanDistanceItemSimilarityBuilder;
import recsys.evaluator.builder.EuclideanDistanceUserSimilarityBuilder;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.evaluator.builder.PearsonCorrelationItemSimilarityBuilder;
import recsys.evaluator.builder.PearsonCorrelationUserSimilarityBuilder;
import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.itemAverage.ItemAverageRecommenderBuilder;
import recsys.itemBased.ItemBasedRecommenderBuilder;
import recsys.recommender.sushi.model.SushiItemDataModel;
import recsys.recommender.sushi.recommender.Include;
import recsys.recommender.sushi.recommender.SushiAndUserClassificationRecommenderBuilder;
import recsys.recommender.sushi.recommender.SushiGlobalAndLocalClassificationRecommenderBuilder;
import recsys.recommender.sushi.recommender.SushiGlobalClassificationRecommenderBuilder;
import recsys.recommender.sushi.recommender.SushiItemSimilarityBuilder;
import recsys.recommender.sushi.recommender.SushiLocalClassificationRecommenderBuilder;
import recsys.recommender.sushi.recommender.SushiUserSimilarityBuilder;
import recsys.recommender.sushi.recommender.SushiUserWeightedSimilarityBuilder;
import recsys.recommender.sushi.recommender.SushiUserWeightsSimilarityBuilder;
import recsys.svd.SvdRecommenderBuilder;
import recsys.userAverage.UserAverageRecommenderBuilder;
import recsys.userBased.UserBasedRecommenderBuilder;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class SushiEvaluator extends AbstractEvaluator {
	
	private static List<String> argsList;

	public static void main(String[] args) throws Exception {
		argsList = Arrays.asList(args);
		SushiEvaluator e = new SushiEvaluator();
		e.execute();
	}

	private void execute() throws Exception {
		DataModel dataModel = new SushiDataset().build();

		List<RecommenderBuilder> builders = createRecommenderBuilders(dataModel);
		evaluateRecommenders(dataModel, builders, argsList);
	}
	
	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel) throws Exception, TasteException {
		List<RecommenderBuilder> builders = new ArrayList<>();
		
		SushiItemDataModel sushiDataModel = new SushiItemDataModelDataset().build();
		
		if (argsList.contains("ub")) {
			EnumSet<Include> includeProperties = EnumSet.noneOf(Include.class);
			if (argsList.contains("style"))
				includeProperties.add(Include.STYLE);
			if (argsList.contains("major"))
				includeProperties.add(Include.MAJOR);
			if (argsList.contains("minor"))
				includeProperties.add(Include.MINOR);
			if (argsList.contains("oiliness"))
				includeProperties.add(Include.OILINESS);
			if (argsList.contains("price"))
				includeProperties.add(Include.PRICE);
			if (argsList.contains("gender"))
				includeProperties.add(Include.GENDER);
			if (argsList.contains("age"))
				includeProperties.add(Include.AGE);
			if (argsList.contains("region15"))
				includeProperties.add(Include.REGION15);
			if (argsList.contains("regionCurrent"))
				includeProperties.add(Include.REGION_CURRENT);
			if (argsList.contains("prefecture15"))
				includeProperties.add(Include.PREFECTURE15);
			if (argsList.contains("prefectureCurrent"))
				includeProperties.add(Include.PREFECTURE_CURRENT);
			if (argsList.contains("eastWest15"))
				includeProperties.add(Include.EAST_WEST15);
			if (argsList.contains("eastWestCurrent"))
				includeProperties.add(Include.EAST_WEST_CURRENT);
			
			if (includeProperties.size() == 0) {
				throw new MissingArgumentException(
						"when using 'ub' algorithm, you must choose at least one of properties [style, major, minor, oiliness, price, gender, age, region15, recionCurrent, prefecture15, prefectureCurrent, eastWest15, eastWestCurrent]");
			}

			UserSimilarityBuilder sushiUserSimilarityBuilder = null;
			if (argsList.contains("s")) {
				sushiUserSimilarityBuilder = new SushiUserSimilarityBuilder(sushiDataModel, includeProperties);
			} else if (argsList.contains("w")) {
				sushiUserSimilarityBuilder = new SushiUserWeightsSimilarityBuilder(sushiDataModel, includeProperties);
			} else if (argsList.contains("wd")) {
				sushiUserSimilarityBuilder = new SushiUserWeightedSimilarityBuilder(sushiDataModel, includeProperties);
			} else {
				throw new MissingArgumentException("When using 'ub' algorithm, you must choose one of the user similarities [s, w, wd].");
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

		
		if (argsList.contains("ubed")) {
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

		if (argsList.contains("ubpc")) {
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
		
		if (argsList.contains("ubed")) {
//			builders.add(new SushiContentBasedRecommenderBuilder(sushiDataModel));
		}

		if (argsList.contains("ibed")) {
			EuclideanDistanceItemSimilarityBuilder euclideanDistanceItemSimilarityBuilder = new EuclideanDistanceItemSimilarityBuilder();
			builders.add(new ItemBasedRecommenderBuilder(euclideanDistanceItemSimilarityBuilder));
		}

		if (argsList.contains("ibpc")) {
			PearsonCorrelationItemSimilarityBuilder pearsonCorrelationItemSimilarityBuilder = new PearsonCorrelationItemSimilarityBuilder();
			builders.add(new ItemBasedRecommenderBuilder(pearsonCorrelationItemSimilarityBuilder));
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
		if (argsList.contains("ibs")) {
			SushiItemSimilarityBuilder sushiItemSimilarityBuilder = new SushiItemSimilarityBuilder(sushiDataModel);
			builders.add(new ItemBasedRecommenderBuilder(sushiItemSimilarityBuilder));
		}

		if (argsList.contains("g")) {
			if (argsList.contains("j48")) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});
			}
			if (argsList.contains("randomTree")) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});
			}
			if (argsList.contains("logistic")) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});
			}
			if (argsList.contains("naiveBayes")) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
			}
			if (argsList.contains("randomForest")) {
				builders.add(new SushiGlobalAndLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});
			}
		}

		 if (argsList.contains("g")) {
			if (argsList.contains("j48")) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});//1.211
			}
			if (argsList.contains("randomTree")) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});//1.199
			}
			if (argsList.contains("logistic")) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});//1.226
			}
			if (argsList.contains("naiveBayes")) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});//1.242
			}
			if (argsList.contains("randomForest")) {
				builders.add(new SushiGlobalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});//1.199
			}
		}
		 
		if (argsList.contains("l")) {
			if (argsList.contains("j48")) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});//1.369
			}
			if (argsList.contains("randomTree")) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});//1.390
			}
			if (argsList.contains("logistic")) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});//1.530
			}
			if (argsList.contains("naiveBayes")) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});//1.503
			}
			if (argsList.contains("randomForest")) {
				builders.add(new SushiLocalClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});//1.276
			}
		}

		if (argsList.contains("glu")) {
			if (argsList.contains("j48")) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new J48();}});
			}
			if (argsList.contains("randomTree")) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomTree();}});
			}
			if (argsList.contains("logistic")) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new Logistic();}});
			}
			if (argsList.contains("naiveBayes")) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new NaiveBayes();}});
			}
			if (argsList.contains("randomForest")) {
				builders.add(new SushiAndUserClassificationRecommenderBuilder(sushiDataModel) {public Classifier createClassifier() {return new RandomForest();}});
			}
		}

//		 // builders.add(new SushiCombinedGlobalClassificationRecommenderBuilder(sushiDataModel));
//
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

		// builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 10, 100)));
		// builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 100, 100)));

		if (argsList.contains("svdaswlr")) {
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

		// builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 100)));
		// builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 1000)));
		// builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 100, 0.001, 10)));

		// builders.add(new SlopeOneRecommenderBuilder());
	
		 return builders;
	}

}
