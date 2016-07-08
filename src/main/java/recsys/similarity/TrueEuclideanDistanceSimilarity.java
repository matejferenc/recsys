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

package recsys.similarity;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.impl.similarity.AbstractSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;

import com.google.common.base.Preconditions;

public final class TrueEuclideanDistanceSimilarity extends AbstractSimilarity {

	/**
	 * @throws IllegalArgumentException
	 *             if {@link DataModel} does not have preference values
	 */
	public TrueEuclideanDistanceSimilarity(DataModel dataModel) throws TasteException {
		this(dataModel, Weighting.UNWEIGHTED);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if {@link DataModel} does not have preference values
	 */
	public TrueEuclideanDistanceSimilarity(DataModel dataModel, Weighting weighting) throws TasteException {
		super(dataModel, weighting, false);
		Preconditions.checkArgument(dataModel.hasPreferenceValues(), "DataModel doesn't have preference values");
	}

	@Override
	public double computeResult(int n, double sumXY, double sumX2, double sumY2, double sumXYdiff2) {
		return (n / (1.0 + Math.sqrt(sumXYdiff2)));
	}

	@Override
	public String getName() {
		return "Euclidian Distance Similarity";
	}

	@Override
	public String getShortName() {
		return "EDS";
	}

}
