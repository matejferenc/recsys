package recsys.sushi.evaluator;

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
import recsys.evaluator.splitter.RandomDatasetSplitter;
import recsys.movielens.dataset.Movielens1MDataset;
import recsys.recommender.builder.UserBasedRecommenderBuilder;
import recsys.similarity.builder.UserSimilarityBuilder;
import recsys.sushi.dataset.SushiDataset;
import recsys.sushi.dataset.SushiItemDataModelDataset;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.similarity.SushiUserSimilarityFunction;
import recsys.sushi.similarity.SushiUserSimilarityFunctionPool;
import recsys.sushi.similarity.builder.SushiUserSimilarityBuilder;

public class SushiUserSimilarityFunctionCalculator extends AbstractEvaluator {

	private static final int GROUPS_COUNT = 5;
	private List<String> argsList;

	public SushiUserSimilarityFunctionCalculator(List<String> argsList) {
		this.argsList = argsList;
	}

	public static void main(String[] args) throws Exception {
		List<String> argsList = Arrays.asList(args);
		SushiUserSimilarityFunctionCalculator e = new SushiUserSimilarityFunctionCalculator(argsList);
		e.evaluate();
	}

	@Override
	public void evaluate() throws Exception {
		DataModel dataModel = new SushiDataset().build();
		SushiUserSimilarityFunctionPool pool = new SushiUserSimilarityFunctionPool();
		pool.generateRandom(10);
		while (true) {
			for (SushiUserSimilarityFunction function : pool) {
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

		sb.append("Starting cross validation using " + GROUPS_COUNT + " groups");
		sb.append("\n");

		sb.append("Builder\t");
		sb.append("Estimated cases\t");
		sb.append("Non-Estimated cases\t");
		sb.append("Time[s]\t");
		sb.append("Score\t\n");
		
		double totalScore = 0;
		for (RecommenderBuilder builder : builders) {
			Date start = new Date();

			int estimated = 0;
			int notEstimated = 0;
			List<Double> evaluated = new ArrayList<>();

			AbstractRecommenderFairEvaluator evaluator = createEvaluator(dataModel, metrics);
			
			RandomDatasetSplitter splitter = new RandomDatasetSplitter(dataModel, GROUPS_COUNT);

			while (splitter.hasNext()) {
				Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> pair = splitter.next();
				
				FastByIDMap<PreferenceArray> trainingDataset = pair.getFirst();
				FastByIDMap<PreferenceArray> testDataset = pair.getSecond();
				double score = evaluator.evaluate(builder, trainingDataset, testDataset);
				evaluated.add(score);
			}

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
			sb.append(formatter.format(average(evaluated)));
			sb.append("\t\t");
			sb.append(listOfDoublesToString(evaluated));
			sb.append("\n");

			totalScore += average(evaluated);
			builder.freeReferences();
		}

		System.out.println(sb.toString());
		return totalScore / builders.size();
	}

	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel, SushiUserSimilarityFunction sushiUserSimilarityFunction) throws Exception, TasteException {
		List<RecommenderBuilder> builders = new ArrayList<>();
		SushiItemDataModel sushiDataModel = new SushiItemDataModelDataset().build();
		UserSimilarityBuilder sushiUserSimilarityBuilder = new SushiUserSimilarityBuilder(sushiDataModel, sushiUserSimilarityFunction);
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
		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(125)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(150)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(175)));
//		builders.add(new UserBasedRecommenderBuilder(sushiUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(200)));
		return builders;
	}

}
