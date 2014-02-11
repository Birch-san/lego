import lejos.nxt.Button;
import lejos.nxt.LCD;
//import lejos.nxt.LightSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.SensorPort;

/**
 * For testing the HiTechnic color sensor (see lejos.nxt.addon.ColorHTSensor).
 * @author BB
 */
public class WallHugger {

	final static int INTERVAL = 200; // milliseconds
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touch = new TouchSensor(SensorPort.S2);
		
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			//LCD.drawInt(sonic.readValue(),7,3);
			System.out.println("touch = " + touch.isPressed());
			System.out.println("distance = " + sonic.getDistance());
			LCD.refresh();
			Thread.sleep(INTERVAL);
		}
	}
}