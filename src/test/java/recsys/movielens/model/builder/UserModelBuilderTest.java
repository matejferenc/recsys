package recsys.movielens.model.builder;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.Pair;
import org.junit.Test;

import recsys.evaluator.DatasetSplitter;
import recsys.movielens.dataset.MovieLensEnrichedModelDataset;
import recsys.movielens.dataset.Movielens1MDataset;
import recsys.movielens.model.movielens.MovieLensEnrichedModel;
import recsys.movielens.model.movielens.UserModel;

public class UserModelBuilderTest {

	@Test
	public void testCreatingUserModel() throws Exception {
		MovieLensEnrichedModel movieLensEnrichedModel = new MovieLensEnrichedModelDataset().build();
		DataModel dataModel = new Movielens1MDataset().build();
		DatasetSplitter splitter = new DatasetSplitter(dataModel, 0.25, 0.3333);
		Pair<FastByIDMap<PreferenceArray>, FastByIDMap<PreferenceArray>> pair = splitter.next();
		FastByIDMap<PreferenceArray> trainingDataset = pair.getFirst();
		DataModel trainingModel = new GenericDataModel(trainingDataset);
		UserModelBuilder userModelBuilder = new UserModelBuilder(trainingModel, movieLensEnrichedModel);
		System.gc();
		UserModel userModel = userModelBuilder.build();
	}
}
