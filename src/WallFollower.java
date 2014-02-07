//Build 1A14

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

public class WallFollower
{
	public static void main (String[] args){
		LCD.drawString("Motor Test: Forward",0,0);
		Forward(500);
		Button.waitForAnyPress();
		LCD.drawString("Motor Test: Backward",0,0);
		Backward(500);
		Button.waitForAnyPress();
		LCD.drawString("Motor Test: Left",0,0);
		Left(500);
		Button.waitForAnyPress();
		LCD.drawString("Motor Test: Right",0,0);
		Right(500);
		Button.waitForAnyPress();
	}
	
	public static void Backward(int time){
		try {
		Motor.A.forward();
		Motor.B.backward();
		Motor.C.forward();
		Thread.sleep(time);
		Stop();
				}
		catch (Exception e) {
		LCD.drawString("Error",0,0);
		Stop();
		}
	}
	
	public static void Forward(int time){
		try {
		Motor.A.backward();
		Motor.B.forward();
		Motor.C.backward();
		Thread.sleep(time);
		Stop();
				}
		catch (Exception e) {
				LCD.drawString("Error",0,0);
		Stop();
		}
	}
	
	public static void Left(int time){
		try {
		Motor.A.backward();
		Motor.B.backward();
		Motor.C.forward();
		Thread.sleep(time);
		Stop();
		} catch (Exception e) {		LCD.drawString("Error",0,0);
		Stop();}
	}
	
	public static void Right(int time){
		try {
		Motor.A.forward();
		Motor.B.backward();
		Motor.C.backward();
		Thread.sleep(time);
		Stop();
		} catch (Exception e) {		LCD.drawString("Error",0,0);
		Stop();}
	}
	
	public static void Stop(){
		Motor.A.stop(true);
		Motor.B.stop(true);
		Motor.C.stop();
	}
}