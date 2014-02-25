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
	
	static int currentDist = -1;
	static int previousDist = -1;
	
	static int spaceDist = 40;
	
	static int timesStepped = 0;
	static int probeStep = 0;
	
	static int wallCheckFreq = 4;
	static int sweepLength = 10;
	
	static boolean roundingCorner = false;
	static boolean hitRecovering = false;
	
	static int timesWallDistantThisSweep = 0;
	//static int timesWallCloseThisSweep = 0;
	
	static boolean[] previousXProbes = new boolean[sweepLength];
	
	private static enum maneuvers {
		BACKWARD, LEFTREVERSE, RIGHT, FORWARD, LEFT
	}
	
	private static ArrayList<maneuvers> queuedManeuvers = new ArrayList<maneuvers>();
	
	public static void main(String [] args) throws Exception {
		//LightSensor cmps = new LightSensor(SensorPort.S1);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchFront = new TouchSensor(SensorPort.S2);
		TouchSensor touchLeft = new TouchSensor(SensorPort.S3);
		
		// init: last 10 probes say we are not looking into free space.
		for (int i=0; i<sweepLength; i++) {
			previousXProbes[i] = false;
		}
		
		file1 = new File("OwLoud.wav");

		//Thread.sleep(2000);
		
		while(!Button.ESCAPE.isDown()) {
			timesStepped++;
			probeStep++;
			
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

			 LCD.clear();
			 System.out.println("distance = " + currentDist);
			 LCD.refresh();
			
			//WallFollower.RightSteer(INTERVAL);
			Thread.sleep(INTERVAL);
		}
	}
	
	private static void findFirstWall() {
		// walk until we hit our first wall
		WallFollower.Forward(INTERVAL);
	}
	
	private static void probeSpace() {
		// must see into freespace this percent of the sweep.
		float spaceHitsProportion = 0.7f;
		
		// report whether this timestep we saw freespace. 
		previousXProbes[probeStep++ % sweepLength] = currentDist>spaceDist;
		
		// how many times we saw all the way into freespace ('hits')
		int hits = 0;
		for (int i=0; i<sweepLength; i++) {
			if (previousXProbes[i]) {
				hits++;
			}
		}
		float proportionHit = hits/sweepLength;
		
		boolean turnIntoSpace = false;
		
		if (proportionHit>=spaceHitsProportion) {
			for (int i=0; i<sweepLength; i++) {
				// re-init
				previousXProbes[i] = false;
			}
			turnIntoSpace = true;
		}
		
		// if for entire sweep wall has been close, then we are definitely in free space
		if (turnIntoSpace) {
			if (atWall) {
				// we have lost the wall we were on. assume corner passed.
				turnRoundCorner();
			} else {
				// we are still looking for a wall, having rounded corner.
				// should we keep going forward? or assume an acute turn, and keep turning a bit more?
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
		int wallMinBerth = 25;
		int wallMaxBerth = 29;
		
		int maxConverge = 40;
		int minConverge = 1;
		
		int multiplier = 7;
		
		int tolerance = 1;
		
		if (currentDist>wallMaxBerth || currentDist-previousDist>tolerance) {
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
		} else if (currentDist<wallMinBerth || previousDist-currentDist>tolerance) {
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
		
		int clearObstacleSteps = 5;
		for (int i=0; i<clearObstacleSteps; i++) {
			queuedManeuvers.add(maneuvers.FORWARD);
		}
		
		// clear obstacle
		//WallFollower.Forward(clearObstacleInterval);
		
		int leftTurnSteps = 20;
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
		
		int passCornerSteps = 18;
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
		
		int backoffSteps = 1;
		for (int i=0; i<backoffSteps; i++) {
			queuedManeuvers.add(maneuvers.BACKWARD);
		}
		
		int leftReverseSteps = 7;
		for (int i=0; i<leftReverseSteps; i++) {
			queuedManeuvers.add(maneuvers.LEFTREVERSE);
		}
		
		int rightTurnSteps = 13;
		for (int i=0; i<rightTurnSteps; i++) {
			queuedManeuvers.add(maneuvers.RIGHT);
		}
	}
}