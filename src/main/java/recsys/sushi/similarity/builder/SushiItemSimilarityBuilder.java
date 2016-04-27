package recsys.sushi.similarity.builder;

import java.util.EnumSet;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.similarity.builder.ItemSimilarityBuilder;
import recsys.sushi.evaluator.IncludeProperties;
import recsys.sushi.model.SushiItemDataModel;
import recsys.sushi.similarity.SushiItemSimilarity;

public class SushiItemSimilarityBuilder implements ItemSimilarityBuilder {

	private final SushiItemDataModel sushiDataModel;
	private EnumSet<IncludeProperties> includeProperties;
	
	public SushiItemSimilarityBuilder(SushiItemDataModel sushiDataModel, EnumSet<IncludeProperties> includeProperties) {
		this.sushiDataModel = sushiDataModel;
		this.includeProperties = includeProperties;
	}

	@Override
	public ItemSimilarity build(DataModel dataModel) throws TasteException {
		return new SushiItemSimilarity(sushiDataModel, includeProperties);
	}

}
