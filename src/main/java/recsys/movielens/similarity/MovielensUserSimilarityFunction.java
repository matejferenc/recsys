package recsys.movielens.similarity;

import java.util.Random;

public class MovielensUserSimilarityFunction {
	
	private final double genresCoefficient;
	private final double directorsCoefficient;
	private final double actorsCoefficient;
	private final double actressesCoefficient;
	private final double keywordsCoefficient;

	public MovielensUserSimilarityFunction(double genresCoefficient, double directorsCoefficient, double actorsCoefficient, double actressesCoefficient, double keywordsCoefficient) {
		this.genresCoefficient = genresCoefficient;
		this.directorsCoefficient = directorsCoefficient;
		this.actorsCoefficient = actorsCoefficient;
		this.actressesCoefficient = actressesCoefficient;
		this.keywordsCoefficient = keywordsCoefficient;
	}
	
	public double calculateSimilarity(double genresSimilarity, double directorsSimilarity, double actorsSimilarity, double actressesSimilarity, double keywordsSimilarity) {
		return genresCoefficient * genresSimilarity +
				directorsCoefficient * directorsSimilarity +
				actorsCoefficient * actorsSimilarity +
				actressesCoefficient * actressesSimilarity +
				keywordsCoefficient * keywordsSimilarity;
	}
	
	public MovielensUserSimilarityFunction mutate() {
		double genresCoefficient = this.genresCoefficient * (Math.random() + 0.5);
		double directorsCoefficient = this.directorsCoefficient * (Math.random() + 0.5);
		double actorsCoefficient = this.actorsCoefficient * (Math.random() + 0.5);
		double actressesCoefficient = this.actressesCoefficient * (Math.random() + 0.5);
		double keywordsCoefficient = this.keywordsCoefficient * (Math.random() + 0.5);
		return new MovielensUserSimilarityFunction(genresCoefficient, directorsCoefficient, actorsCoefficient, actressesCoefficient, keywordsCoefficient);
	}
	
	public MovielensUserSimilarityFunction crossover(MovielensUserSimilarityFunction other) {
		Random random = new Random();
		double genresCoefficient = random.nextBoolean() ? this.genresCoefficient : other.genresCoefficient;
		double directorsCoefficient = random.nextBoolean() ? this.directorsCoefficient : other.directorsCoefficient;
		double actorsCoefficient = random.nextBoolean() ? this.actorsCoefficient : other.actorsCoefficient;
		double actressesCoefficient = random.nextBoolean() ? this.actressesCoefficient : other.actressesCoefficient;
		double keywordsCoefficient = random.nextBoolean() ? this.keywordsCoefficient : other.keywordsCoefficient;
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
