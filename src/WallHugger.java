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

	final static int INTERVAL = 50; // milliseconds
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchFront = new TouchSensor(SensorPort.S2);
		TouchSensor touchLeft = new TouchSensor(SensorPort.S3);
		
		File file1 = new File("OwLoud.wav");

		Thread.sleep(2000);
		
		//File file2 = new File("PetrolLoud.wav");
		//Sound.playSample(file2, 100);

		boolean touchedEver = false;
		boolean atWall = false;
		
		while(!Button.ESCAPE.isDown()) {
			if (touchFront.isPressed() || touchLeft.isPressed()) {
				touchedEver = true;
				atWall = true;
				if (Sound.getTime() < 1) {
					Sound.playSample(file1, 200);
				}
				// 45 = 12 turns
				for (int i=0; i<42; i++) {
					WallFollower.Right(INTERVAL);
				}
			} else {
				WallFollower.Forward(INTERVAL);
				if (touchedEver) {
					if (sonic.getDistance()>75 && atWall) {
						atWall = false;
						// clear obstacle
						for (int i=0; i<10; i++) {
							WallFollower.Forward(INTERVAL);
						}
						for (int i=0; i<55; i++) {
							WallFollower.Left(INTERVAL);
						}
					} else {
						if (sonic.getDistance()<=75) {
							atWall = true;
						}
					}
				}
			}

			//System.out.println(sonic.getDistance());
			
			LCD.clear();
			LCD.drawInt(sonic.getDistance(),7,3);
			System.out.println("touch = " + touchFront.isPressed());
			System.out.println("distance = " + sonic.getDistance());
			LCD.refresh();
			//WallFollower.Right(INTERVAL);
			Thread.sleep(INTERVAL);
		}
	}
}