package recsys.movielens.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.evaluator.abstr.AbstractEvaluator;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
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

	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel, MovielensUserSimilarityFunction movielensUserSimilarityFunction) throws Exception, TasteException {
		List<RecommenderBuilder> builders = new ArrayList<>();
		
		UserSimilarityBuilder movieLensUserSimilarityBuilder = new MovieLensUserSimilarityBuilder(movieLensEnrichedModel, movielensUserSimilarityFunction);
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(5)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(10)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(15)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(20)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(25)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(30)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(35)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(40)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(45)));
		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(50)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
//		builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));
		
		return builders;
	}

}
