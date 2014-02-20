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
		
		int spaceDist = 50;
		
		while(!Button.ESCAPE.isDown()) {
			currentDist  = sonic.getDistance();
			if (touchFront.isPressed() || touchLeft.isPressed()) {
				touchedEver = true;
				atWall = true;
				if (Sound.getTime() < 1) {
					Sound.playSample(file1, 200);
				}
				// 45 = 12 turns
				for (int i=0; i<36; i++) {
					WallFollower.Right(INTERVAL);
				}
			} else {
				WallFollower.Forward(INTERVAL);
				// makes a cool sound!
				//WallFollower.Brake();
				timesStepped++;
				
				if (touchedEver) {
					if (currentDist>spaceDist && atWall) {
						atWall = false;
						// clear obstacle
						for (int i=0; i<10; i++) {
							WallFollower.Forward(INTERVAL);
						}
						// revise turning amount to be proportional to distance (see free space or angled wall)
						for (int i=0; i<40; i++) {
							WallFollower.Left(INTERVAL);
						}
					} else if (atWall) {
						if (currentDist<=spaceDist) {
							atWall = true;
							// do no comparison if undefined
							if (previousDist== -1) previousDist = currentDist;
							
							int tolerance = 0;
							
							if (timesStepped>4) {
								if (currentDist>previousDist+tolerance) {
									// diverging
									for (int i=0; i<1; i++) {
										WallFollower.Left(INTERVAL);
									} 
								} else if (currentDist<previousDist-tolerance) {
									// converging
									for (int i=0; i<1; i++) {
										WallFollower.Right(INTERVAL);
									}
								}
								timesStepped = 0;
								previousDist = currentDist;
							}
						}
					}
					/*if (timesStepped>10) {
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
					}*/
				}
			}

			//System.out.println(sonic.getDistance());
			
			
			//WallFollower.Right(INTERVAL);
			Thread.sleep(INTERVAL);
		}
	}
}