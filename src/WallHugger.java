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
		
		int currentDist = -1; 
		int previousDist = -1;
		
		int timesStepped = 0;
		
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
				WallFollower.Forward(50);
				timesStepped++;
				
				currentDist  = sonic.getDistance();
				if (touchedEver) {
					if (currentDist>75 && atWall) {
						atWall = false;
						// clear obstacle
						for (int i=0; i<10; i++) {
							WallFollower.Forward(INTERVAL);
						}
						for (int i=0; i<55; i++) {
							WallFollower.Left(INTERVAL);
						}
					} else {
						if (currentDist<=75) {
							atWall = true;
							/*// do no comparison if undefined
							if (previousDist== -1) previousDist = currentDist;
							
							int tolerance = 0;
							
							if (currentDist>previousDist+tolerance) {
								// diverging
								for (int i=0; i<3; i++) {
									WallFollower.Left(INTERVAL);
								} 
							} else if (currentDist<previousDist-tolerance) {
								// converging
								for (int i=0; i<3; i++) {
									WallFollower.Right(INTERVAL);
								}
							}*/
						}
					}
					if (timesStepped>10) {
						LCD.clear();
						//LCD.drawInt(sonic.getDistance(),7,3);
						//System.out.println("touch = " + touchFront.isPressed());
						System.out.println("prevDist = " + previousDist);
						System.out.println("distance = " + currentDist);
						previousDist = currentDist;
						LCD.refresh();
						
						WallFollower.Brake();
						Button.waitForAnyPress();
						timesStepped = 0;
					}
				}
			}

			//System.out.println(sonic.getDistance());
			
			
			//WallFollower.Right(INTERVAL);
			Thread.sleep(INTERVAL);
		}
	}
}