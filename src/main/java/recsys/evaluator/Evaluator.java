package recsys.evaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.ItemAverageRecommender;
import org.apache.mahout.cf.taste.impl.recommender.average.UserAverageRecommender;
import org.apache.mahout.cf.taste.impl.recommender.movielens.GenresDataModel;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.itemAverage.ItemAverageRecommenderBuilder;
import recsys.itemBased.ItemBasedRecommenderBuilder;
import recsys.movieLensContentBased.ContentBasedRecommenderBuilder;
import recsys.movieLensHetrecContentBased.HetrecContentBasedRecommenderBuilder;
import recsys.userAverage.UserAverageRecommenderBuilder;
import recsys.userBased.UserBasedRecommenderBuilder;

public class Evaluator {

	public static void main(String[] args) throws Exception {
		Evaluator e = new Evaluator();
		e.execute();
	}

	private void execute() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		// String path = prop.getProperty("movielens-10m-ratings.dat");
		String path = prop.getProperty("movielens-1m-ratings.dat");
		// String path = prop.getProperty("movielens-100k-ratings.data");
		// String path = prop.getProperty("user_ratedmovies-timestamps.dat");
		File dataFile = new File(path);
		// DataModel dataModel = new FileDataModel(dataFile, "\t");
		DataModel dataModel = new FileDataModel(dataFile, "::");

		String genresPath = prop.getProperty("movielens-1m-movies.dat");
		// String genresPath = prop.getProperty("movielens-10m-movies.dat");
		GenresDataModel genresModel = new GenresDataModel(new File(genresPath));

		List<RecommenderBuilder> builders = new ArrayList<>();

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

		EuclideanDistanceItemSimilarityBuilder euclideanDistanceItemSimilarityBuilder = new EuclideanDistanceItemSimilarityBuilder();
		builders.add(new ItemBasedRecommenderBuilder(euclideanDistanceItemSimilarityBuilder));
		builders.add(new UserAverageRecommenderBuilder());
		builders.add(new ItemAverageRecommenderBuilder());
		builders.add(new ContentBasedRecommenderBuilder(genresModel));

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
		}

		System.out.println(sb.toString());
	}
}
