package recsys.movielens.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;

import recsys.evaluator.abstr.AbstractEvaluator;
import recsys.evaluator.abstr.AbstractRecommenderFairEvaluator;
import recsys.evaluator.abstr.IncludeMetrics;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.evaluator.splitter.FairDatasetSplitter;
import recsys.movielens.dataset.MovieLensEnrichedModelDataset;
import recsys.movielens.dataset.Movielens1MDataset;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.similarity.MovielensUserSimilarityFunction;
import recsys.movielens.similarity.builder.MovieLensUserSimilarityBuilder;
import recsys.recommender.builder.UserBasedRecommenderBuilder;
import recsys.similarity.builder.UserSimilarityBuilder;

public class MovielensUserSimilarityFunctionCalculator extends AbstractEvaluator {

	private List<String> argsList;
	private MovieLensEnrichedModel movieLensEnrichedModel;

	public MovielensUserSimilarityFunctionCalculator(List<String> argsList) {
		this.argsList = argsList;
	}

	public static void main(String[] args) throws Exception {
		List<String> argsList = Arrays.asList(args);
		MovielensUserSimilarityFunctionCalculator e = new MovielensUserSimilarityFunctionCalculator(argsList);
		e.evaluate();
	}

	@Override
	public void evaluate() throws Exception {
		DataModel dataModel = new Movielens1MDataset().build();
		movieLensEnrichedModel = new MovieLensEnrichedModelDataset().build();
		MovielensUserSimilarityFunctionPool pool = new MovielensUserSimilarityFunctionPool();
		pool.generateRandom(10);
		while (true) {
			for (MovielensUserSimilarityFunction function : pool) {
				List<RecommenderBuilder> builders = createRecommenderBuilders(dataModel, function);
				double averageScore = evaluateRecommenders(dataModel, builders, argsList);
				System.out.println("AVERAGE SCORE OF EVOLUTION ALGORITHM: " + averageScore);
				pool.registerScore(averageScore);
			}
			pool.printBest();
			pool.nextGeneration();
		}
	}
	
	protected double evaluateRecommenders(DataModel dataModel, List<RecommenderBuilder> builders, List<String> argsList) throws MissingArgumentException, TasteException {
		StringBuilder sb = new StringBuilder();
		IncludeMetrics metrics = IncludeMetrics.fromList(argsList);

		sb.append("minimum possible preference: " + dataModel.getMinPreference());
		sb.append("\n");
		sb.append("maximum possible preference: " + dataModel.getMaxPreference());
		sb.append("\n");

		sb.append("Starting cross validation using " + Math.round(1 / (testingPercentage * evaluationPercentage)) + " groups");
		sb.append("\n");
		sb.append("Testing percentage: " + testingPercentage);
		sb.append("\n");
		sb.append("Evaluation percentage: " + evaluationPercentage);
		sb.append("\n");

		sb.append("Builder\t");
		sb.append("Estimated cases\t");
		sb.append("Non-Estimated cases\t");
		sb.append("Time[s]\t");
		sb.append("Score\t\n");
		
		double score = 0;

		for (RecommenderBuilder builder : builders) {
			Date start = new Date();

			int estimated = 0;
			int notEstimated = 0;
			List<Double> evaluated = new ArrayList<>();

			AbstractRecommenderFairEvaluator evaluator = createEvaluator(dataModel, metrics);
			
			FairDatasetSplitter splitter = new FairDatasetSplitter(dataModel, testingPercentage, evaluationPercentage);

			Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> pair = splitter.next();
			
			FastByIDMap<PreferenceArray> trainingDataset = pair.getFirst();
			FastByIDMap<PreferenceArray> testDataset = pair.getSecond();
			score = evaluator.evaluate(builder, trainingDataset, testDataset);

			estimated = evaluator.getEstimateCounter().intValue();
			notEstimated = evaluator.getNoEstimateCounter().intValue();
			Date end = new Date();

			sb.append(builder.getShortName());
			sb.append("\t");
			sb.append(estimated);
			sb.append("\t");
			sb.append(notEstimated);
			sb.append("\t");
			sb.append((end.getTime() - start.getTime()) / 1000);
			sb.append("\t");
			sb.append(formatter.format(score));
			sb.append("\t\t");
			sb.append(listOfDoublesToString(evaluated));
			sb.append("\n");

			builder.freeReferences();
		}

		System.out.println(sb.toString());
		return score / builders.size();
	}

	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel, MovielensUserSimilarityFunction movielensUserSimilarityFunction) throws Exception, TasteException {
		List<RecommenderBuilder> builders = new ArrayList<>();

		UserSimilarityBuilder movieLensUserSimilarityBuilder = new MovieLensUserSimilarityBuilder(movieLensEnrichedModel, movielensUserSimilarityFunction);
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
		// builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));

		return builders;
	}

}
