package recsys.sushi.similarity;

import java.util.Random;

import recsys.movielens.similarity.MovielensUserSimilarityFunction;

public class SushiUserSimilarityFunction {

	private final double styleCoefficient;
	private final double majorGroupCoefficient;
	private final double minorGroupCoefficient;
	private final double oilinessCoefficient;
	private final double priceCoefficient;

	public SushiUserSimilarityFunction(double styleCoefficient, double majorGroupCoefficient, double minorGroupCoefficient, double oilinessCoefficient, double priceCoefficient) {
		this.styleCoefficient = styleCoefficient;
		this.majorGroupCoefficient = majorGroupCoefficient;
		this.minorGroupCoefficient = minorGroupCoefficient;
		this.oilinessCoefficient = oilinessCoefficient;
		this.priceCoefficient = priceCoefficient;
	}

	public double calculateSimilarity(double styleSimilarity, double majorGroupSimilarity, double minorGroupSimilarity, double oilinessSimilarity, double priceSimilarity) {
		return styleCoefficient * styleSimilarity +
				majorGroupCoefficient * majorGroupSimilarity +
				minorGroupCoefficient * minorGroupSimilarity +
				oilinessCoefficient * oilinessSimilarity +
				priceCoefficient * priceSimilarity;
	}
	
	public SushiUserSimilarityFunction mutate() {
		double styleCoefficient = this.styleCoefficient * (Math.random() + 0.5);
		double majorGroupCoefficient = this.majorGroupCoefficient * (Math.random() + 0.5);
		double minorGroupCoefficient = this.minorGroupCoefficient * (Math.random() + 0.5);
		double oilinessCoefficient = this.oilinessCoefficient * (Math.random() + 0.5);
		double priceCoefficient = this.priceCoefficient * (Math.random() + 0.5);
		return new SushiUserSimilarityFunction(styleCoefficient, majorGroupCoefficient, minorGroupCoefficient, oilinessCoefficient, priceCoefficient);
	}
	
	public SushiUserSimilarityFunction crossover(SushiUserSimilarityFunction other) {
		Random random = new Random();
		double styleCoefficient = random.nextBoolean() ? this.styleCoefficient : other.styleCoefficient;
		double majorGroupCoefficient = random.nextBoolean() ? this.majorGroupCoefficient : other.majorGroupCoefficient;
		double minorGroupCoefficient = random.nextBoolean() ? this.minorGroupCoefficient : other.minorGroupCoefficient;
		double oilinessCoefficient = random.nextBoolean() ? this.oilinessCoefficient : other.oilinessCoefficient;
		double priceCoefficient = random.nextBoolean() ? this.priceCoefficient : other.priceCoefficient;
		return new SushiUserSimilarityFunction(styleCoefficient, majorGroupCoefficient, minorGroupCoefficient, oilinessCoefficient, priceCoefficient);
	}

	@Override
	public String toString() {
		return "SushiUserSimilarityFunction [styleCoefficient=" + styleCoefficient + ", majorGroupCoefficient=" + majorGroupCoefficient + ", minorGroupCoefficient=" + minorGroupCoefficient + ", oilinessCoefficient="
				+ oilinessCoefficient + ", priceCoefficient=" + priceCoefficient + "]";
	}
	
	
}
