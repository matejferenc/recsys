package recsys.sushi.recommender;

import org.apache.mahout.cf.taste.model.Preference;

public class DummyPreference implements Preference{

	@Override
	public long getUserID() {
		return 0;
	}

	@Override
	public long getItemID() {
		return 0;
	}

	@Override
	public float getValue() {
		return -100000;
	}

	@Override
	public void setValue(float value) {
	}

}
