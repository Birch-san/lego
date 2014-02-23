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
	
	static int spaceDist = 240;
	
	static int wallCheckFreq = 4;
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchFront = new TouchSensor(SensorPort.S2);
		TouchSensor touchLeft = new TouchSensor(SensorPort.S3);
		
		file1 = new File("OwLoud.wav");

		Thread.sleep(2000);
		
		while(!Button.ESCAPE.isDown()) {
			currentDist  = sonic.getDistance();
			// touch is highest priority; interrupts all
			if (touchFront.isPressed() || touchLeft.isPressed()) {
				beHit();
			} else {
				explore();
			}
			
			//WallFollower.RightSteer(INTERVAL);
			Thread.sleep(INTERVAL);
		}
	}
	
	private static void explore() {
		WallFollower.Forward(INTERVAL);
		timesStepped++;
		
		if (touchedEver) {
			if (currentDist>spaceDist && atWall) {
				turnRoundCorner();
			} else if (atWall) {
				// already on a wall? follow it!
				if (currentDist<=spaceDist) {
					followWall();
				}
			} else if (!atWall) {
				// not on a wall any more, but found one now? follow it!
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
		
		if (timesStepped>wallCheckFreq) {
			pronateToWall();
		}
	}
	
	private static void pronateToWall() {
		int wallMinBerth = 33;
		int wallMaxBerth = 37;
		
		int maxConverge = 40;
		int minConverge = 1;
		
		int multiplier = 7;
		
		int tolerance = 0;
		
		if (currentDist>wallMaxBerth) {
			// we are too far from wall; converge
			Sound.playNote(Sound.XYLOPHONE, 2093, 7);
			
			int convergeSteps = wallCheckFreq;
			/*
			// how obtuse did wall become since last sweep?
			int convergeSteps = currentDist-previousDist;
			convergeSteps *= multiplier;
			// constrain
			convergeSteps = Math.max(minConverge, convergeSteps);
			convergeSteps = Math.min(maxConverge, convergeSteps);
			*/
			
			//for (int i=0; i<convergeSteps; i++) {
				WallFollower.LeftSteer(INTERVAL*convergeSteps);
			//}
		} else if (currentDist<wallMinBerth) {
			Sound.playNote(Sound.XYLOPHONE, 1760, 7);
			// we are too close to wall; diverge
			
			int divergeSteps = wallCheckFreq;
			
			/*
			// how acute did wall become since last sweep?
			int divergeSteps = previousDist-currentDist;
			divergeSteps *= multiplier;
			// constrain
			divergeSteps = Math.max(minConverge, divergeSteps);
			divergeSteps = Math.min(maxConverge, divergeSteps);
			*/
			
			//for (int i=0; i<divergeSteps; i++) {
				WallFollower.RightSteer(INTERVAL*divergeSteps);
			//}
		}
		
		/*if (currentDist>previousDist+tolerance) {
			// diverging from wall
			
		} else if (currentDist<previousDist-tolerance) {
			// converging to wall
		}*/
		
		timesStepped = 0;
		previousDist = currentDist;
	}
	
	private static void turnIntoSpace() {
		atWall = false;
		
		Sound.playNote(Sound.FLUTE, 440, 7);
		
		int clearObstacleInterval = INTERVAL*10; 
		// clear obstacle
		WallFollower.Forward(clearObstacleInterval);
		
		int leftTurnInterval = INTERVAL*40;
		
		// revise turning amount to be proportional to distance (see free space or angled wall)
		WallFollower.Left(leftTurnInterval);
	}
	
	private static void turnRoundCorner() {
		turnIntoSpace();
		
		int passCornerInterval = INTERVAL*36;
		// clear obstacle
		WallFollower.Forward(passCornerInterval);
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