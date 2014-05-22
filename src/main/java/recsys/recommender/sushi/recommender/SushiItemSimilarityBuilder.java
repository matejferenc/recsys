package recsys.recommender.sushi.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.evaluator.builder.ItemSimilarityBuilder;
import recsys.recommender.sushi.model.SushiDataModel;

public class SushiItemSimilarityBuilder implements ItemSimilarityBuilder {

	private final SushiDataModel sushiDataModel;
	
	public SushiItemSimilarityBuilder(SushiDataModel sushiDataModel) {
		this.sushiDataModel = sushiDataModel;
	}

	@Override
	public ItemSimilarity build(DataModel dataModel) throws TasteException {
		return new SushiItemSimilarity(sushiDataModel);
	}

}
