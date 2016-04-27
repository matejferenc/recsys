package recsys.movielens.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;

import recsys.IncludeAlgorithms;
import recsys.evaluator.abstr.AbstractEvaluator;
import recsys.evaluator.builder.NearestNUserNeighborhoodBuilder;
import recsys.movielens.dataset.MovieLensEnrichedModelDataset;
import recsys.movielens.dataset.Movielens1MDataset;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.recommender.builder.MovieLensContentBasedRecommenderBuilder;
import recsys.movielens.similarity.builder.MovieLensUserSimilarityBuilder;
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
import recsys.similarity.builder.PearsonCorrelationUserSimilarityBuilder;
import recsys.similarity.builder.UserSimilarityBuilder;

public class MovieLensEvaluator extends AbstractEvaluator {

	private List<String> argsList;

	public MovieLensEvaluator(List<String> argsList) {
		this.argsList = argsList;
	}
	
	public static void main(String[] args) throws Exception {
		List<String> argsList = Arrays.asList(args);
		MovieLensEvaluator e = new MovieLensEvaluator(argsList);
		e.evaluate();
	}

	@Override
	public void evaluate() throws Exception {
		DataModel dataModel = new Movielens1MDataset().build();
		
//		String genresPath = prop.getProperty("movielens-1m-movies.dat");
		// String genresPath = prop.getProperty("movielens-10m-movies.dat");
//		GenresDataModel genresModel = new GenresDataModel(new File(genresPath));
		
		List<RecommenderBuilder> builders = createRecommenderBuilders(dataModel);
		evaluateRecommenders(dataModel, builders, argsList);
	}

	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel) throws Exception, TasteException {
		EnumSet<IncludeAlgorithms> includeAlgorithms = IncludeAlgorithms.fromList(argsList);
		
		MovieLensEnrichedModel movieLensEnrichedModel = new MovieLensEnrichedModelDataset().build();
		
		List<RecommenderBuilder> builders = new ArrayList<>();
		
		if (includeAlgorithms.contains(IncludeAlgorithms.MOVIE_LENS_USER_SIMILARITY)) {
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
			builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(75)));
			builders.add(new UserBasedRecommenderBuilder(movieLensUserSimilarityBuilder, new NearestNUserNeighborhoodBuilder(100)));
		}
		
		if (includeAlgorithms.contains(IncludeAlgorithms.MOVIE_LENS_CONTENT_BASED)) {
			builders.add(new MovieLensContentBasedRecommenderBuilder(movieLensEnrichedModel));
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
		if (includeAlgorithms.contains(IncludeAlgorithms.USER_AVERAGE)) {
			builders.add(new UserAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_USER_AVERAGE)) {
			builders.add(new ItemUserAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_AND_USER_AVERAGE)) {
			builders.add(new ItemAndUserAverageRecommenderBuilder());
		}
		if (includeAlgorithms.contains(IncludeAlgorithms.ITEM_AVERAGE)) {
			builders.add(new ItemAverageRecommenderBuilder());
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
		
		if (includeAlgorithms.contains(IncludeAlgorithms.SLOPE_ONE)) {
			builders.add(new SlopeOneRecommenderBuilder());
		}
		
		return builders;
	}

}
