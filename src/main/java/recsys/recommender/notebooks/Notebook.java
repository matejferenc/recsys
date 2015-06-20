package recsys.recommender.notebooks;

public class Notebook {

	private int hdd;
	
	private int display;
	
	private int price;
	
	private int ram;
	
	private String producer;
	
	public Notebook(int hdd, int display, int price, int ram, String producer) {
		super();
		this.hdd = hdd;
		this.display = display;
		this.price = price;
		this.ram = ram;
		this.producer = producer;
	}

	public int getHdd() {
		return hdd;
	}

	public void setHdd(int hdd) {
		this.hdd = hdd;
	}

	public int getDisplay() {
		return display;
	}

	public void setDisplay(int display) {
		this.display = display;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}


}
