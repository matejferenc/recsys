/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package recsys.evaluator;

import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;

/**
 * <p>
 * A {@link org.apache.mahout.cf.taste.eval.RecommenderEvaluator} which computes the "root mean squared" difference between predicted and actual ratings for users. This is the square root of the average of this
 * difference, squared.
 * </p>
 */
public final class TopHalfRMSRecommenderEvaluator extends AbstractDifferenceRecommenderEvaluator {

	private RunningAverage average;
	private float half;

	public TopHalfRMSRecommenderEvaluator(DataModel dataModel) {
		half = (dataModel.getMinPreference() + dataModel.getMaxPreference()) / 2 + 0.01f;
	}

	@Override
	protected void reset() {
		average = new FullRunningAverage();
	}

	@Override
	protected void processOneEstimate(float estimatedPreference, Preference realPref) {
		if (realPref.getValue() > half) {
			double diff = realPref.getValue() - estimatedPreference;
			average.addDatum(diff * diff);
		}
	}

	@Override
	protected double computeFinalEvaluation() {
		return Math.sqrt(average.getAverage());
	}

	@Override
	public String toString() {
		return "Top Half RMS Recommender Evaluator";
	}

}
