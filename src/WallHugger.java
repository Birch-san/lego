import lejos.nxt.Button;
import lejos.nxt.LCD;
//import lejos.nxt.LightSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.SensorPort;

import java.io.File;

import lejos.nxt.Sound;

import java.util.ArrayList;

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
	
	static boolean roundingCorner = false;
	static boolean hitRecovering = false;
	
	private static enum maneuvers {
		BACKWARD, LEFTREVERSE, RIGHT, FORWARD, LEFT
	}
	
	private static ArrayList<maneuvers> queuedManeuvers = new ArrayList<maneuvers>();
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchFront = new TouchSensor(SensorPort.S2);
		TouchSensor touchLeft = new TouchSensor(SensorPort.S3);
		
		file1 = new File("OwLoud.wav");

		Thread.sleep(2000);
		
		while(!Button.ESCAPE.isDown()) {
			timesStepped++;
			
			currentDist  = sonic.getDistance();
			
			// touch is highest priority; interrupts all
			if (touchFront.isPressed() || touchLeft.isPressed()) {
				beHit();
			}
			
			if (hitRecovering) {
				boolean finished = continueCurrentManeuver();
				if (finished) {
					hitRecovering = false;
				}
			} else if (roundingCorner) {
				boolean finished = continueCurrentManeuver();
				if (finished) {
					roundingCorner = false;
				}
			} else {
				// if we've found first wall 
				if (touchedEver) {
					// check if we need to round corner
					probeSpace();
					// otherwise check if we need to follow wall
					if (!roundingCorner) {
						probeClose();
					}
				} else {
					findFirstWall();
				}
			}
			
			//WallFollower.RightSteer(INTERVAL);
			Thread.sleep(INTERVAL);
		}
	}
	
	private static void findFirstWall() {
		// walk until we hit our first wall
		WallFollower.Forward(INTERVAL);
	}
	
	private static void probeSpace() {
		if (currentDist>spaceDist) {
			if (atWall) {
				// we have lost the wall we were on. assume corner passed.
				turnRoundCorner();
			} else {
				// we are still looking for a wall, having rounded corner.
			}
		}
	}
	
	private static void probeClose() {
		// if there's a wall nearby, follow it
		if (currentDist<=spaceDist) {
			followWall();
		}
	}
	
	private static void followWall() {
		atWall = true;
		// do no comparison if undefined
		if (previousDist== -1) previousDist = currentDist;
		
		pronateToWall();
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
			
			//int convergeSteps = wallCheckFreq;
			/*
			// how obtuse did wall become since last sweep?
			int convergeSteps = currentDist-previousDist;
			convergeSteps *= multiplier;
			// constrain
			convergeSteps = Math.max(minConverge, convergeSteps);
			convergeSteps = Math.min(maxConverge, convergeSteps);
			*/
			
			//for (int i=0; i<convergeSteps; i++) {
				WallFollower.LeftSteer(INTERVAL);
			//}
		} else if (currentDist<wallMinBerth) {
			Sound.playNote(Sound.XYLOPHONE, 1760, 7);
			// we are too close to wall; diverge
			
			//int divergeSteps = wallCheckFreq;
			
			/*
			// how acute did wall become since last sweep?
			int divergeSteps = previousDist-currentDist;
			divergeSteps *= multiplier;
			// constrain
			divergeSteps = Math.max(minConverge, divergeSteps);
			divergeSteps = Math.min(maxConverge, divergeSteps);
			*/
			
			//for (int i=0; i<divergeSteps; i++) {
				WallFollower.RightSteer(INTERVAL);
			//}
		} else {
			// we are within the right range from wall; just go forward
			WallFollower.Forward(INTERVAL);
		}
		
		/*if (currentDist>previousDist+tolerance) {
			// diverging from wall
			
		} else if (currentDist<previousDist-tolerance) {
			// converging to wall
		}*/
		
		if (timesStepped>wallCheckFreq) {
			timesStepped = 0;
			previousDist = currentDist;
		}
	}
	
	private static void turnIntoSpace() {
		atWall = false;
		
		Sound.playNote(Sound.FLUTE, 440, 7);
		
		int clearObstacleSteps = 10; 
		for (int i=0; i<clearObstacleSteps; i++) {
			queuedManeuvers.add(maneuvers.FORWARD);
		}
		
		// clear obstacle
		//WallFollower.Forward(clearObstacleInterval);
		
		int leftTurnSteps = 40;
		for (int i=0; i<leftTurnSteps; i++) {
			queuedManeuvers.add(maneuvers.LEFT);
		}
		
		// revise turning amount to be proportional to distance (see free space or angled wall)
		//WallFollower.Left(leftTurnInterval);
	}
	
	private static void turnRoundCorner() {
		// actually hit recovering is higher priority, so this should never occur..
		hitRecovering = false;
		roundingCorner = true;
		queuedManeuvers = new ArrayList<maneuvers>();
		
		turnIntoSpace();
		
		int passCornerSteps = 36;
		// clear obstacle
		for (int i=0; i<passCornerSteps; i++) {
			queuedManeuvers.add(maneuvers.FORWARD);
		}
	}
	
	private static boolean continueCurrentManeuver() {
		if (queuedManeuvers.size()>0) {
			maneuvers currentMove = queuedManeuvers.remove(0);
			if (currentMove == maneuvers.FORWARD) {
				WallFollower.Forward(INTERVAL);
			} else if (currentMove == maneuvers.LEFT) {
				WallFollower.Left(INTERVAL);
			} else if (currentMove == maneuvers.BACKWARD) {
				WallFollower.Backward(INTERVAL);
			} else if (currentMove == maneuvers.LEFTREVERSE) {
				WallFollower.LeftReverse(INTERVAL);
			}
		}
		// tell whether maneuvers remain
		return queuedManeuvers.size()==0;
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
		roundingCorner = false;
		hitRecovering = true;
		queuedManeuvers = new ArrayList<maneuvers>();
		
		int backoffSteps = 10;
		for (int i=0; i<backoffSteps; i++) {
			queuedManeuvers.add(maneuvers.BACKWARD);
		}
		
		int leftReverseSteps = 20;
		for (int i=0; i<leftReverseSteps; i++) {
			queuedManeuvers.add(maneuvers.LEFTREVERSE);
		}
		
		int rightTurnSteps = 20;
		for (int i=0; i<rightTurnSteps; i++) {
			queuedManeuvers.add(maneuvers.RIGHT);
		}
	}
}