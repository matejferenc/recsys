package recsys.evaluator.abstr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.common.RunningAverageAndStdDev;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;

import com.google.common.collect.Lists;

/**
 * Abstract superclass of a couple implementations, providing shared functionality.
 */
public abstract class AbstractDifferenceRecommenderFairListEvaluator extends AbstractRecommenderFairEvaluator {

	protected AbstractDifferenceRecommenderFairListEvaluator(DataModel dataModel) {
		super(dataModel);
	}

	protected abstract void processOneEstimateList(List<Float> estimated, List<Float> real);
	
	protected double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException {
		reset();
		Collection<Callable<Void>> estimateCallables = Lists.newArrayList();
		for (Map.Entry<Long, PreferenceArray> entry : testPrefs.entrySet()) {
			estimateCallables.add(new PreferenceEstimateCallable(recommender, entry.getKey(), entry.getValue(), noEstimateCounter, estimateCounter));
		}
		log.info("Beginning evaluation of {} users", estimateCallables.size());
		RunningAverageAndStdDev timing = new FullRunningAverageAndStdDev();
		execute(estimateCallables, noEstimateCounter, estimateCounter, timing);
		return computeFinalEvaluation();
	}

	public final class PreferenceEstimateCallable implements Callable<Void> {

		private final Recommender recommender;
		private final long testUserID;
		private final PreferenceArray prefs;
		private final AtomicInteger noEstimateCounter;
		private AtomicInteger estimateCounter;

		public PreferenceEstimateCallable(Recommender recommender, long testUserID, PreferenceArray prefs, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter) {
			this.recommender = recommender;
			this.testUserID = testUserID;
			this.prefs = prefs;
			this.noEstimateCounter = noEstimateCounter;
			this.estimateCounter = estimateCounter;
		}

		@Override
		public Void call() throws TasteException {
			List<Float> estimated = new ArrayList<>();
			List<Float> real = new ArrayList<>();
			for (Preference realPref : prefs) {
				float estimatedPreference = Float.NaN;
				try {
					estimatedPreference = recommender.estimatePreference(testUserID, realPref.getItemID());
				} catch (NoSuchUserException nsue) {
					// It's possible that an item exists in the test data but not training data in which case
					// NSEE will be thrown. Just ignore it and move on.
					log.info("User exists in test data but not training data: {}", testUserID);
				} catch (NoSuchItemException nsie) {
					log.info("Item exists in test data but not training data: {}", realPref.getItemID());
				}
				if (Float.isNaN(estimatedPreference)) {
					noEstimateCounter.incrementAndGet();
				} else {
					int counter = estimateCounter.incrementAndGet();
					if (counter % 1000 == 0) {
						log.info("Successfully estimated: {} preferences", counter);
					}
					estimatedPreference = capEstimatedPreference(estimatedPreference);
					estimated.add(estimatedPreference);
					real.add(realPref.getValue());
				}
				if (estimateCounter.get() % 1000 == 0) {
					log.info("Recommended {} preferences", estimateCounter.get());
				}
			}
			if (estimated.size() > 1) {
				processOneEstimateList(estimated, real);
			}
			return null;
		}

	}

}
