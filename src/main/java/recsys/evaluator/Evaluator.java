package recsys.evaluator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.MissingArgumentException;

import recsys.evaluator.abstr.AbstractEvaluator;
import recsys.movielens.evaluator.MovieLensEvaluator;
import recsys.notebooks.evaluator.NotebooksEvaluator;
import recsys.sushi.evaluator.SushiEvaluator;

public class Evaluator {

	private static List<String> argsList;

	public static void main(String[] args) throws Exception {
		argsList = Arrays.asList(args);
		AbstractEvaluator e = null;
		if (argsList.contains("sushi")) {
			e = new SushiEvaluator(argsList);
		} else if (argsList.contains("movieLens")) {
			e = new MovieLensEvaluator(argsList);
		} else if (argsList.contains("notebooks")) {
			e = new NotebooksEvaluator();
		} else {
			throw new MissingArgumentException("You need to specify one of datasets [sushi, movieLens, notebooks].");
		}
		e.evaluate();
	}
}
