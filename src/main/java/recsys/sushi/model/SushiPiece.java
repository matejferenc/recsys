package recsys.sushi.model;

public class SushiPiece {

	private int style;
	
	private int majorGroup;
	
	private int minorGroup;
	
	private double oiliness;
	
	private double eatingFrequency;
	
	public SushiPiece(int style, int majorGroup, int minorGroup, double oiliness, double eatingFrequency, double price, double sellingFrequency) {
		super();
		this.style = style;
		this.majorGroup = majorGroup;
		this.minorGroup = minorGroup;
		this.oiliness = oiliness;
		this.eatingFrequency = eatingFrequency;
		this.price = price;
		this.sellingFrequency = sellingFrequency;
	}

	private double price;
	
	private double sellingFrequency;

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getMajorGroup() {
		return majorGroup;
	}

	public void setMajorGroup(int majorGroup) {
		this.majorGroup = majorGroup;
	}

	public int getMinorGroup() {
		return minorGroup;
	}

	public void setMinorGroup(int minorGroup) {
		this.minorGroup = minorGroup;
	}

	public double getOiliness() {
		return oiliness;
	}

	public void setOiliness(double oiliness) {
		this.oiliness = oiliness;
	}

	public double getEatingFrequency() {
		return eatingFrequency;
	}

	public void setEatingFrequency(double eatingFrequency) {
		this.eatingFrequency = eatingFrequency;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getSellingFrequency() {
		return sellingFrequency;
	}

	public void setSellingFrequency(double sellingFrequency) {
		this.sellingFrequency = sellingFrequency;
	}
}
