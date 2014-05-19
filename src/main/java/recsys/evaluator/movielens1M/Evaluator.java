package recsys.evaluator.movielens1M;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.dataset.MovieLensEnrichedModelDataset;
import recsys.dataset.Movielens1M;
import recsys.dataset.SushiDataset;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.evaluator.builder.UserSimilarityBuilder;
import recsys.recommender.movielens.GenresDataModel;
import recsys.recommender.movielens.model.movielens.MovieLensEnrichedDataModelLoader;
import recsys.recommender.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.recommender.movielens.recommender.MovieLensContentBasedRecommenderBuilder;
import recsys.recommender.movielens.recommender.MovieLensUserSimilarityBuilder;
import recsys.userBased.UserBasedRecommenderBuilder;

public class Evaluator {

	public static void main(String[] args) throws Exception {
		Evaluator e = new Evaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		// String path = prop.getProperty("user_ratedmovies-timestamps.dat");
		// DataModel dataModel = new FileDataModel(dataFile, "\t");

		DataModel dataModel = new Movielens1M().build();
		
//		String genresPath = prop.getProperty("movielens-1m-movies.dat");
		// String genresPath = prop.getProperty("movielens-10m-movies.dat");
//		GenresDataModel genresModel = new GenresDataModel(new File(genresPath));
		
		MovieLensEnrichedModel movieLensEnrichedModel = new MovieLensEnrichedModelDataset().build();
		
		List<RecommenderBuilder> builders = new ArrayList<>();
		
		UserSimilarityBuilder movieLensUserSimilarityBuilder = new MovieLensUserSimilarityBuilder(movieLensEnrichedModel);
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
		
//		builders.add(new MovieLensContentBasedRecommenderBuilder(movieLensEnrichedModel));

//		EuclideanDistanceUserSimilarityBuilder euclideanDistanceUserSimilarityBuilder = new EuclideanDistanceUserSimilarityBuilder();
//
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
//		builders.add(new UserBasedRecommenderBuilder(euclideanDistanceUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
//
//		PearsonCorrelationUserSimilarityBuilder pearsonCorrelationUserSimilarityBuilder = new PearsonCorrelationUserSimilarityBuilder();
//
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
//		builders.add(new UserBasedRecommenderBuilder(pearsonCorrelationUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
//
//		EuclideanDistanceItemSimilarityBuilder euclideanDistanceItemSimilarityBuilder = new EuclideanDistanceItemSimilarityBuilder();
//		builders.add(new ItemBasedRecommenderBuilder(euclideanDistanceItemSimilarityBuilder));
//		builders.add(new UserAverageRecommenderBuilder());
//		builders.add(new ItemAverageRecommenderBuilder());
//		builders.add(new ContentBasedRecommenderBuilder(genresModel));
		
//		builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 1, 10)));
//		builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 2, 10)));
//		builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 3, 10)));
//		builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 4, 10)));
//		builders.add(new SvdRecommenderBuilder(new SVDPlusPlusFactorizer(dataModel, 5, 10)));
		
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 1, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 2, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 3, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 4, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 5, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 6, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 7, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 8, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 9, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 10, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 20, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 30, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 40, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 50, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 60, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 70, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 80, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 90, 0.001, 10)));
//		builders.add(new SvdRecommenderBuilder(new ALSWRFactorizer(dataModel, 100, 0.001, 10)));
		
		
//		builders.add(new SlopeOneRecommenderBuilder());
		
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

			int repeats = 10;
			int totalEstimated = 0;
			int totalNotEstimated = 0;
			double totalEvaluationResult = 0;

			for (int i = 0; i < repeats; i++) {
				// RecommenderEvaluator e = new AverageAbsoluteDifferenceRecommenderEvaluator();
				AbstractDifferenceRecommenderEvaluator e = new RMSRecommenderEvaluator();
//				AbstractDifferenceRecommenderEvaluator e = new TopHalfRMSRecommenderEvaluator(dataModel);
				double evaluate = e.evaluate(builder, null, dataModel, trainingPercentage, evaluationPercentage);
				totalEvaluationResult += evaluate;
				
				totalEstimated += e.getEstimateCounter().intValue();
				totalNotEstimated += e.getNoEstimateCounter().intValue();
			}
			Date end = new Date();

			sb.append("Using builder: " + builder.getName());
			sb.append("\n");
			sb.append("Estimated in: " + totalEstimated + " cases");
			sb.append("\n");
			sb.append("Not estimated in: " + totalNotEstimated + " cases");
			sb.append("\n");
			sb.append("Score: " + totalEvaluationResult / repeats);
			sb.append("\n");
			sb.append("Time: " + (end.getTime() - start.getTime()) / 1000 + " seconds");
			sb.append("\n");
			sb.append("-------------------------\n");
			
			builder.freeReferences();
		}

		System.out.println(sb.toString());
	}
}
