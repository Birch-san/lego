import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

public class WallFollower
{
	public static void main (String[] args){
		LCD.drawString("Motor Test: Forward",0,0);
		Forward(150);
		Button.waitForAnyPress();
		LCD.drawString("Motor Test: Backward",0,0);
		Backward(150);
		Button.waitForAnyPress();
		LCD.drawString("Motor Test: Left",0,0);
		Left(150);
		Button.waitForAnyPress();
		LCD.drawString("Motor Test: Right",0,0);
		Right(150);
		Button.waitForAnyPress();
	}
	
	public static void Forward(int time){
		try {
		Motor.A.forward();
		Motor.B.backward();
		Motor.C.forward();
		Thread.sleep(time);
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
				}
		catch (Exception e) {}
	}
	
	public static void Backward(int time){
		try {
		Motor.A.backward();
		Motor.B.forward();
		Motor.C.backward();
		Thread.sleep(time);
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
				}
		catch (Exception e) {}
	}
	
	public static void Left(int time){
		try {
		Motor.A.backward();
		Motor.B.backward();
		Motor.C.forward();
		Thread.sleep(time);
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
		} catch (Exception e) {}
	}
	
	public static void Right(int time){
		try {
		Motor.A.forward();
		Motor.B.backward();
		Motor.C.backward();
		Thread.sleep(time);
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
		} catch (Exception e) {}
	}
}