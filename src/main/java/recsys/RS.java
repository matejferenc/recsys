package recsys;

import java.util.Date;
import java.util.Properties;

public abstract class RS {

	private long startTime;
	private long endTime;
	protected Properties prop;
	
	public void run() throws Exception{
		start();
		execute();
		finish();
	}
	
	public abstract void execute() throws Exception;
	
	public void start() throws Exception {
		prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

		startTime = new Date().getTime();
	}
	
	public void finish() {
		endTime = new Date().getTime();
		System.out.println("cas behu: " + (endTime - startTime) / 1000 + "s");
	}
}
