package recsys.sushi.model;

/**
 * Information about one sushi product.
 *
 */
public class SushiPiece {

	private int style;
	
	private int majorGroup;
	
	private int minorGroup;
	
	private Double oiliness;
	
	private Double eatingFrequency;
	
	public SushiPiece(int style, int majorGroup, int minorGroup, Double oiliness, Double eatingFrequency, Double price, Double sellingFrequency) {
		super();
		this.style = style;
		this.majorGroup = majorGroup;
		this.minorGroup = minorGroup;
		this.oiliness = oiliness;
		this.eatingFrequency = eatingFrequency;
		this.price = price;
		this.sellingFrequency = sellingFrequency;
	}

	private Double price;
	
	private Double sellingFrequency;

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

	public Double getOiliness() {
		return oiliness;
	}

	public void setOiliness(Double oiliness) {
		this.oiliness = oiliness;
	}

	public Double getEatingFrequency() {
		return eatingFrequency;
	}

	public void setEatingFrequency(Double eatingFrequency) {
		this.eatingFrequency = eatingFrequency;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getSellingFrequency() {
		return sellingFrequency;
	}

	public void setSellingFrequency(Double sellingFrequency) {
		this.sellingFrequency = sellingFrequency;
	}
}
