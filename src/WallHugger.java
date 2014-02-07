import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

/**
 * For testing the HiTechnic color sensor (see lejos.nxt.addon.ColorHTSensor).
 * @author BB
 */
public class WallHugger {

	final static int INTERVAL = 200; // milliseconds
	
	public static void main(String [] args) throws Exception {
		LightSensor cmps = new LightSensor(SensorPort.S1);
		
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawInt(cmps.readValue(),7,3);
			LCD.refresh();
			Thread.sleep(INTERVAL);
		}
	}
}