package recsys.movielens.similarity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import recsys.movielens.model.movielens.MovieLensEnrichedModel;

public class MovieLensItemSimilarity implements ItemSimilarity {
	
	private MovieLensEnrichedModel movieLensModel;

	public MovieLensItemSimilarity(MovieLensEnrichedModel movieLensEnrichedModel) {
		this.movieLensModel = movieLensEnrichedModel;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public double itemSimilarity(Integer itemID1, Integer itemID2) throws TasteException {
		Double similarity = calculateGenresSimilarity(itemID1, itemID2) + calculateDirectorsSimilarity(itemID1, itemID2) +
				calculateActorsSimilarity(itemID1, itemID2) + calculateActressesSimilarity(itemID1, itemID2);
		return similarity / 4;
	}
	
	private Double calculateGenresSimilarity(Integer itemID1, Integer itemID2) {
		return calculatePropertySetSimilarity(movieLensModel.getItemGenres(itemID1), movieLensModel.getItemGenres(itemID2));
	}

	private Double calculateDirectorsSimilarity(Integer itemID1, Integer itemID2) {
		return calculatePropertySetSimilarity(movieLensModel.getItemImdbDirectors(itemID1), movieLensModel.getItemImdbDirectors(itemID2));
	}

	private Double calculateActorsSimilarity(Integer itemID1, Integer itemID2) {
		return calculatePropertySetSimilarity(movieLensModel.getItemImdbActors(itemID1), movieLensModel.getItemImdbActors(itemID2));
	}

	private Double calculateActressesSimilarity(Integer itemID1, Integer itemID2) {
		return calculatePropertySetSimilarity(movieLensModel.getItemImdbActresses(itemID1), movieLensModel.getItemImdbActresses(itemID2));
	}

	private <T> Double calculatePropertySetSimilarity(Set<T> set1, Set<T> set2) {
		Set<T> commonPropertyIds = getCommonPropertyIds(set1, set2);
		int min = Math.min(set1.size(), set2.size());
		return ((double) commonPropertyIds.size()) / min;
	}

	private <T> Set<T> getCommonPropertyIds(Set<T> allPropertyIds1, Set<T> allPropertyIds2) {
		Set<T> common = new HashSet<>();
		for (T propertyId : allPropertyIds1) {
			if (allPropertyIds2.contains(propertyId)) {
				common.add(propertyId);
			}
		}
		return common;
	}

	@Override
	public double[] itemSimilarities(Integer itemID1, Integer[] itemID2s) throws TasteException {
		int length = itemID2s.length;
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = itemSimilarity(itemID1, itemID2s[i]);
		}
		return result;
	}

	@Override
	public Integer[] allSimilarItemIDs(Integer itemID) throws TasteException {
		return null;
	}

	@Override
	public String getName() {
		return "MovieLens Item Similarity";
	}

	@Override
	public String getShortName() {
		return "MIS";
	}

}
