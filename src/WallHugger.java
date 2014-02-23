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

	static boolean touchedEver = false;
	static boolean atWall = false;
	
	static File file1;
	
	static int timesStepped = 0;
	
	static int currentDist = -1;
	static int previousDist = -1;
	
	static int spaceDist = 120;
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchFront = new TouchSensor(SensorPort.S2);
		TouchSensor touchLeft = new TouchSensor(SensorPort.S3);
		
		file1 = new File("OwLoud.wav");

		Thread.sleep(2000);
		
		while(!Button.ESCAPE.isDown()) {
			currentDist  = sonic.getDistance();
			if (touchFront.isPressed() || touchLeft.isPressed()) {
				beHit();
			} else {
				explore();
			}
			Thread.sleep(INTERVAL);
		}
	}
	
	private static void explore() {
		WallFollower.Forward(INTERVAL);
		timesStepped++;
		
		if (touchedEver) {
			if (currentDist>spaceDist && atWall) {
				turnIntoSpace();
			} else if (atWall) {
				if (currentDist<=spaceDist) {
					followWall();
				}
			}
		}
	}
	
	private static void followWall() {
		atWall = true;
		// do no comparison if undefined
		if (previousDist== -1) previousDist = currentDist;
		
		if (timesStepped>4) {
			pronateToWall();
		}
	}
	
	private static void pronateToWall() {
		int tolerance = 0;
		
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
	
	private static void turnIntoSpace() {
		atWall = false;
		
		Sound.playNote(Sound.FLUTE, 440, 7);
		
		int clearObstacleInterval = INTERVAL*10; 
		// clear obstacle
		WallFollower.Forward(clearObstacleInterval);
		
		int leftTurnInterval = INTERVAL*36;
		
		// revise turning amount to be proportional to distance (see free space or angled wall)
		WallFollower.Left(leftTurnInterval);
	}
	
	private static void beHit() {
		touchedEver = true;
		atWall = true;
		if (Sound.getTime() < 1) {
			Sound.playSample(file1, 200);
		}
		
		threePointTurnRight();
	}
	
	private static void threePointTurnRight() {

		int backoffInterval = INTERVAL*10;
		
		WallFollower.Backward(backoffInterval);
		
		int leftReverseInterval = INTERVAL*20;
		
		WallFollower.LeftReverse(leftReverseInterval);
		
		int rightTurnInterval = INTERVAL*20;
		//int rightTurnIterations = 36;
		
		WallFollower.Right(rightTurnInterval);
	}
}