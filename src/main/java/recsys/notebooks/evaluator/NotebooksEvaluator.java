package recsys.notebooks.evaluator;

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
import recsys.notebooks.dataset.NotebooksDataModelDataset;
import recsys.notebooks.dataset.NotebooksDataset;
import recsys.notebooks.model.NotebooksDataModel;
import recsys.notebooks.recommender.builder.NotebooksRecommenderBuilder;
import recsys.notebooks.similarity.NotebooksUserSimilarity.Include;
import recsys.notebooks.similarity.builder.NotebooksUserSimilarityBuilder;
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

public class NotebooksEvaluator extends AbstractEvaluator {

	private static List<String> argsList;

	public static void main(String[] args) throws Exception {
		argsList = Arrays.asList(args);
		NotebooksEvaluator e = new NotebooksEvaluator();
		e.evaluate();
	}

	public void evaluate() throws Exception {
		DataModel dataModel = new NotebooksDataset().build();
		List<RecommenderBuilder> builders = createRecommenderBuilders(dataModel);
		evaluateRecommenders(dataModel, builders, argsList);
	}

	private List<RecommenderBuilder> createRecommenderBuilders(DataModel dataModel) throws Exception, TasteException {
		EnumSet<IncludeAlgorithms> includeAlgorithms = IncludeAlgorithms.fromList(argsList);
		
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

		if (includeAlgorithms.contains(IncludeAlgorithms.NOTEBOOKS)) {
			builders.add(new NotebooksRecommenderBuilder(notebooksDataModel, includeProperties));
		}
		
		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED_SIMILARITY)) {
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
		
		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED_EUCLIDEAN_DISTANCE)) {
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

		if (includeAlgorithms.contains(IncludeAlgorithms.USER_BASED_PEARSON_CORRELATION)) {
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

}
