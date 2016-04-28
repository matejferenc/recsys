package recsys.sushi.model;

import recsys.model.NumericPreference;
import recsys.model.SetPreference;

/**
 * Model of a single person who rated sushi products.
 *
 */
public class SushiUser {
	
	private int gender;
	
	private int age;
	
	private int prefectureIDUntil15;
	
	private int regionIDUntil15;
	
	private int eastWestIDUntil15;
	
	private int prefectureIDCurrent;
	
	private int regionIDCurrent;
	
	private int eastWestIDCurrent;

	// will only have ids: 0, 1
	private SetPreference stylePreferences;

	// will only have ids: 0, 1
	private SetPreference majorGroupPreferences;

	// will only have ids: 0 - 11
	private SetPreference minorGroupPreferences;

	private NumericPreference oilinessPreferences;

	private NumericPreference eatingFrequencyPreferences;

	private NumericPreference pricePreferences;

	private NumericPreference sellingFrequencyPreferences;

	public SushiUser() {
		stylePreferences = new SetPreference();
		majorGroupPreferences = new SetPreference();
		minorGroupPreferences = new SetPreference();
		oilinessPreferences = new NumericPreference();
		eatingFrequencyPreferences = new NumericPreference();
		pricePreferences = new NumericPreference();
		sellingFrequencyPreferences = new NumericPreference();
	}

	public SetPreference getStylePreferences() {
		return stylePreferences;
	}

	public SetPreference getMajorGroupPreferences() {
		return majorGroupPreferences;
	}

	public SetPreference getMinorGroupPreferences() {
		return minorGroupPreferences;
	}

	public NumericPreference getOilinessPreferences() {
		return oilinessPreferences;
	}

	public NumericPreference getEatingFrequencyPreferences() {
		return eatingFrequencyPreferences;
	}

	public NumericPreference getPricePreferences() {
		return pricePreferences;
	}

	public NumericPreference getSellingFrequencyPreferences() {
		return sellingFrequencyPreferences;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getPrefectureIDUntil15() {
		return prefectureIDUntil15;
	}

	public void setPrefectureIDUntil15(int prefectureIDUntil15) {
		this.prefectureIDUntil15 = prefectureIDUntil15;
	}

	public int getRegionIDUntil15() {
		return regionIDUntil15;
	}

	public void setRegionIDUntil15(int regionIDUntil15) {
		this.regionIDUntil15 = regionIDUntil15;
	}

	public int getPrefectureIDCurrent() {
		return prefectureIDCurrent;
	}

	public void setPrefectureIDCurrent(int prefectureIDCurrent) {
		this.prefectureIDCurrent = prefectureIDCurrent;
	}

	public int getRegionIDCurrent() {
		return regionIDCurrent;
	}

	public void setRegionIDCurrent(int regionIDCurrent) {
		this.regionIDCurrent = regionIDCurrent;
	}

	public void setStylePreferences(SetPreference stylePreferences) {
		this.stylePreferences = stylePreferences;
	}

	public void setMajorGroupPreferences(SetPreference majorGroupPreferences) {
		this.majorGroupPreferences = majorGroupPreferences;
	}

	public void setMinorGroupPreferences(SetPreference minorGroupPreferences) {
		this.minorGroupPreferences = minorGroupPreferences;
	}

	public void setOilinessPreferences(NumericPreference oilinessPreferences) {
		this.oilinessPreferences = oilinessPreferences;
	}

	public void setEatingFrequencyPreferences(NumericPreference eatingFrequencyPreferences) {
		this.eatingFrequencyPreferences = eatingFrequencyPreferences;
	}

	public void setPricePreferences(NumericPreference pricePreferences) {
		this.pricePreferences = pricePreferences;
	}

	public void setSellingFrequencyPreferences(NumericPreference sellingFrequencyPreferences) {
		this.sellingFrequencyPreferences = sellingFrequencyPreferences;
	}

	public int getEastWestIDUntil15() {
		return eastWestIDUntil15;
	}

	public void setEastWestIDUntil15(int eastWestIDUntil15) {
		this.eastWestIDUntil15 = eastWestIDUntil15;
	}

	public int getEastWestIDCurrent() {
		return eastWestIDCurrent;
	}

	public void setEastWestIDCurrent(int eastWestIDCurrent) {
		this.eastWestIDCurrent = eastWestIDCurrent;
	}

}
