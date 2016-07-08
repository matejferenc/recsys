package recsys.movielens.similarity;

import java.util.Random;

public class MovielensUserSimilarityFunction {
	
	private final Double genresCoefficient;
	private final Double directorsCoefficient;
	private final Double actorsCoefficient;
	private final Double actressesCoefficient;
	private final Double keywordsCoefficient;

	public MovielensUserSimilarityFunction(Double genresCoefficient, Double directorsCoefficient, Double actorsCoefficient, Double actressesCoefficient, Double keywordsCoefficient) {
		this.genresCoefficient = genresCoefficient;
		this.directorsCoefficient = directorsCoefficient;
		this.actorsCoefficient = actorsCoefficient;
		this.actressesCoefficient = actressesCoefficient;
		this.keywordsCoefficient = keywordsCoefficient;
	}
	
	public Double calculateSimilarity(Double genresSimilarity, Double directorsSimilarity, Double actorsSimilarity, Double actressesSimilarity, Double keywordsSimilarity) {
		return genresCoefficient * genresSimilarity +
				directorsCoefficient * directorsSimilarity +
				actorsCoefficient * actorsSimilarity +
				actressesCoefficient * actressesSimilarity +
				keywordsCoefficient * keywordsSimilarity;
	}
	
	public MovielensUserSimilarityFunction mutate() {
		Double genresCoefficient = (double) (this.genresCoefficient * (Math.random() + 0.5));
		Double directorsCoefficient = (double) (this.directorsCoefficient * (Math.random() + 0.5));
		Double actorsCoefficient = (double) (this.actorsCoefficient * (Math.random() + 0.5));
		Double actressesCoefficient = (double) (this.actressesCoefficient * (Math.random() + 0.5));
		Double keywordsCoefficient = (double) (this.keywordsCoefficient * (Math.random() + 0.5));
		return new MovielensUserSimilarityFunction(genresCoefficient, directorsCoefficient, actorsCoefficient, actressesCoefficient, keywordsCoefficient);
	}
	
	public MovielensUserSimilarityFunction crossover(MovielensUserSimilarityFunction other) {
		Random random = new Random();
		Double genresCoefficient = random.nextBoolean() ? this.genresCoefficient : other.genresCoefficient;
		Double directorsCoefficient = random.nextBoolean() ? this.directorsCoefficient : other.directorsCoefficient;
		Double actorsCoefficient = random.nextBoolean() ? this.actorsCoefficient : other.actorsCoefficient;
		Double actressesCoefficient = random.nextBoolean() ? this.actressesCoefficient : other.actressesCoefficient;
		Double keywordsCoefficient = random.nextBoolean() ? this.keywordsCoefficient : other.keywordsCoefficient;
		return new MovielensUserSimilarityFunction(genresCoefficient, directorsCoefficient, actorsCoefficient, actressesCoefficient, keywordsCoefficient);
	}

	@Override
	public String toString() {
		return "MovielensUserSimilarityFunction [genresCoefficient=" + genresCoefficient +
				", directorsCoefficient=" + directorsCoefficient + 
				", actorsCoefficient=" + actorsCoefficient + 
				", actressesCoefficient=" + actressesCoefficient + 
				", keywordsCoefficient=" + keywordsCoefficient + "]";
	}
	
	
	
}
