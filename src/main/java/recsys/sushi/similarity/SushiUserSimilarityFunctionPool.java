package recsys.sushi.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SushiUserSimilarityFunctionPool extends ArrayList<SushiUserSimilarityFunction>{
	
	private List<Double> scores = new ArrayList<Double>();
	
	private Map<SushiUserSimilarityFunction, Double> history = new HashMap<SushiUserSimilarityFunction, Double>();

	public void generateRandom(int populationSize) {
		for (int i = 0; i < populationSize; i++) {
			Double styleCoefficient = (double) (0.2 * (1 + Math.random()));
			Double majorGroupCoefficient = (double) (0.2 * (1 + Math.random()));
			Double minorGroupCoefficient = (double) (0.2 * (1 + Math.random()));
			Double oilinessCoefficient = (double) (0.2 * (1 + Math.random()));
			Double priceCoefficient = (double) (0.2 * (1 + Math.random()));
			this.add(new SushiUserSimilarityFunction(styleCoefficient, majorGroupCoefficient, minorGroupCoefficient, oilinessCoefficient, priceCoefficient));
		}
	}

	public void registerScore(Double averageScore) {
		scores.add(averageScore);
	}

	public void nextGeneration() {
		List<SushiUserSimilarityFunction> newGeneration = new ArrayList<SushiUserSimilarityFunction>();
		newGeneration.add(this.get(0));
		newGeneration.add(this.get(1));
		newGeneration.add(this.get(2));
		newGeneration.add(this.get(3));
		newGeneration.addAll(mutate(newGeneration));
		this.clear();
		addAll(newGeneration);
		scores.clear();
	}

	private List<SushiUserSimilarityFunction> mutate(List<SushiUserSimilarityFunction> newGeneration) {
		List<SushiUserSimilarityFunction> mutated = new ArrayList<SushiUserSimilarityFunction>();
		for (int i = 0; i < newGeneration.size() - 1; i++) {
			for (int j = i + 1; j < newGeneration.size(); j++) {
				SushiUserSimilarityFunction function = newGeneration.get(i);
				SushiUserSimilarityFunction mutatedFunction = function.crossover(newGeneration.get(j)).mutate();
				mutated.add(mutatedFunction);
			}
		}
		return mutated;
	}

	public void printBest() {
		System.out.println("best function:");
		System.out.println(this.get(0).toString());
	}

	public void sort() {
		for (int i = 0; i < this.size(); i++) {
			history.put(this.get(i), scores.get(i));
		}
		Collections.sort(this, new Comparator<SushiUserSimilarityFunction>() {
		    public int compare(SushiUserSimilarityFunction left, SushiUserSimilarityFunction right) {
		    	return Double.compare(scores.get(indexOf(left)), scores.get(indexOf(right)));
		    }
		});
	}
	
	public Double getFromHistory(SushiUserSimilarityFunction function) {
		return history.get(function);
	}
	
}
