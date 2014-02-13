import lejos.nxt.Button;
import lejos.nxt.LCD;
//import lejos.nxt.LightSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.SensorPort;

import java.io.File;

import lejos.nxt.Sound;

/**
 * For testing the HiTechnic color sensor (see lejos.nxt.addon.ColorHTSensor).
 * @author BB
 */
public class WallHugger {

	final static int INTERVAL = 200; // milliseconds
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchFront = new TouchSensor(SensorPort.S3);
		
		File file1 = new File("OwLoud.wav");

		Thread.sleep(2000);
		
		//File file2 = new File("PetrolLoud.wav");
		//Sound.playSample(file2, 100);

		
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			//LCD.drawInt(sonic.readValue(),7,3);
			//System.out.println("touch = " + touchFront.isPressed());
			//System.out.println("distance = " + sonic.getDistance());
			//LCD.refresh();
			WallFollower.Right(INTERVAL);
			if (Sound.getTime() < 1) {
				Sound.playSample(file1, 200);
			}
			Thread.sleep(INTERVAL);
		}
	}
}