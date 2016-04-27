package recsys.recommender.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.similarity.builder.ItemSimilarityBuilder;

public class ItemBasedRecommenderBuilder implements RecommenderBuilder {

	
	private ItemSimilarityBuilder itemSimilarityBuilder;
	private ItemSimilarity itemSimilarity;

	public ItemBasedRecommenderBuilder(ItemSimilarityBuilder itemSimilarityBuilder) {
		this.itemSimilarityBuilder = itemSimilarityBuilder;
	}
	
	@Override
	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		itemSimilarity = itemSimilarityBuilder.build(dataModel);
		Recommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
		return recommender;
	}

	@Override
	public String getName() {
		return "Item based recommender builder" + " with item similarity: " + itemSimilarity.getName();
	}
	
	@Override
	public String getShortName() {
		return "IB" + itemSimilarity.getShortName();
	}

	@Override
	public void freeReferences() {
	}
}
