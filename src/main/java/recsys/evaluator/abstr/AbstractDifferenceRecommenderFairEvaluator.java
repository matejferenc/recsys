package recsys.evaluator.abstr;

import java.util.Collection;
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
public abstract class AbstractDifferenceRecommenderFairEvaluator extends AbstractRecommenderFairEvaluator {
	
	protected AbstractDifferenceRecommenderFairEvaluator(DataModel dataModel) {
		super(dataModel);
	}

	protected abstract void processOneEstimate(Double estimatedPreference, Preference realPref);

	protected double getEvaluation(FastByIDMap<PreferenceArray> testPrefs, Recommender recommender) throws TasteException {
		reset();
		Collection<Callable<Void>> estimateCallables = Lists.newArrayList();
		for (Map.Entry<Integer, PreferenceArray> entry : testPrefs.entrySet()) {
			estimateCallables.add(new PreferenceEstimateCallable(recommender, entry.getKey(), entry.getValue(), noEstimateCounter, estimateCounter));
		}
		log.info("Beginning evaluation of {} users", estimateCallables.size());
		RunningAverageAndStdDev timing = new FullRunningAverageAndStdDev();
		execute(estimateCallables, noEstimateCounter, estimateCounter, timing);
		return computeFinalEvaluation();
	}

	public final class PreferenceEstimateCallable implements Callable<Void> {

		private final Recommender recommender;
		private final int testUserID;
		private final PreferenceArray prefs;
		private final AtomicInteger noEstimateCounter;
		private AtomicInteger estimateCounter;

		public PreferenceEstimateCallable(Recommender recommender, int testUserID, PreferenceArray prefs, AtomicInteger noEstimateCounter, AtomicInteger estimateCounter) {
			this.recommender = recommender;
			this.testUserID = testUserID;
			this.prefs = prefs;
			this.noEstimateCounter = noEstimateCounter;
			this.estimateCounter = estimateCounter;
		}

		@Override
		public Void call() throws TasteException {
			for (Preference realPref : prefs) {
				Double estimatedPreference = Double.NaN;
				try {
					// only takes relevant neighbors into account:
					estimatedPreference = recommender.estimatePreference(testUserID, realPref.getItemID());
				} catch (NoSuchUserException nsue) {
					// It's possible that an item exists in the test data but not training data in which case
					// NSUE will be thrown. Just ignore it and move on.
					log.info("User exists in test data but not training data: {}", testUserID);
				} catch (NoSuchItemException nsie) {
					log.info("Item exists in test data but not training data: {}", realPref.getItemID());
				}
				if (Double.isNaN(estimatedPreference)) {
					noEstimateCounter.incrementAndGet();
				} else {
					int counter = estimateCounter.incrementAndGet();
					if (counter % 1000 == 0) {
						log.info("Successfully estimated: {} preferences", counter);
					}
					estimatedPreference = capEstimatedPreference(estimatedPreference);
					processOneEstimate(estimatedPreference, realPref);
					processEasiestAndHardestItems(estimatedPreference, realPref);
				}
				if (estimateCounter.get() % 1000 == 0) {
					log.info("Recommended {} preferences", estimateCounter.get());
				}
			}
			return null;
		}

	}
	
}
