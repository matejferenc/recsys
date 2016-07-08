package recsys.movielens.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import recsys.movielens.similarity.MovielensUserSimilarityFunction;

public class MovielensUserSimilarityFunctionPool extends ArrayList<MovielensUserSimilarityFunction>{
	
	private List<Double> scores = new ArrayList<Double>();

	public void generateRandom(int populationSize) {
		for (int i = 0; i < populationSize; i++) {
			Double genresCoefficient = (double) (0.2 * (1 + Math.random()));
			Double directorsCoefficient = (double) (0.2 * (1 + Math.random()));
			Double actorsCoefficient = (double) (0.2 * (1 + Math.random()));
			Double actressesCoefficient = (double) (0.2 * (1 + Math.random()));
			Double keywordsCoefficient = (double) (0.2 * (1 + Math.random()));
			this.add(new MovielensUserSimilarityFunction(genresCoefficient, directorsCoefficient, actorsCoefficient, actressesCoefficient, keywordsCoefficient));
		}
	}

	public void registerScore(Double averageScore) {
		scores.add(averageScore);
	}

	public void nextGeneration() {
		Collections.sort(this, new Comparator<MovielensUserSimilarityFunction>() {
		    public int compare(MovielensUserSimilarityFunction left, MovielensUserSimilarityFunction right) {
		        return Double.compare(scores.indexOf(left), scores.indexOf(right));
		    }
		});
		List<MovielensUserSimilarityFunction> newGeneration = new ArrayList<MovielensUserSimilarityFunction>();
		newGeneration.add(this.get(0));
		newGeneration.add(this.get(1));
		newGeneration.add(this.get(2));
		newGeneration.add(this.get(3));
		newGeneration.add(this.get(4));
		newGeneration = mutate(newGeneration);
		this.clear();
		addAll(newGeneration);
		scores.clear();
	}

	private List<MovielensUserSimilarityFunction> mutate(List<MovielensUserSimilarityFunction> newGeneration) {
		List<MovielensUserSimilarityFunction> mutated = new ArrayList<MovielensUserSimilarityFunction>();
		for (int i = 0; i < newGeneration.size() - 1; i++) {
			for (int j = i + 1; j < newGeneration.size(); j++) {
				MovielensUserSimilarityFunction function = newGeneration.get(i);
				MovielensUserSimilarityFunction mutatedFunction = function.crossover(newGeneration.get(j)).mutate();
				mutated.add(mutatedFunction);
			}
		}
		return mutated;
	}

	public void printBest() {
		Collections.sort(this, new Comparator<MovielensUserSimilarityFunction>() {
		    public int compare(MovielensUserSimilarityFunction left, MovielensUserSimilarityFunction right) {
		        return Double.compare(scores.indexOf(left), scores.indexOf(right));
		    }
		});
		System.out.println("best function:");
		System.out.println(this.get(0).toString());
	}
	
}
