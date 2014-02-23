import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

public class WallFollower
{
	public static void main (String[] args){
		LCD.drawString("Motor Test: Forward",0,0);
		Forward(150);
		LCD.drawString("Motor Test: Backward",0,0);
		Backward(150);
		LCD.drawString("Motor Test: Left",0,0);
		Left(150);
		LCD.drawString("Motor Test: Right",0,0);
		Right(150);
		Button.waitForAnyPress();
	}
	
	public static void Forward(int time){
		try {
			useDrivingSpeed();
		Motor.A.forward();
		Motor.B.forward();
		//Motor.B.stop();
		Motor.C.forward();
		Thread.sleep(time);
				}
		catch (Exception e) {}
	}
	
	public static void Backward(int time){
		try {
			useDrivingSpeed();
		Motor.A.backward();
		Motor.B.backward();
		Motor.C.backward();
		Thread.sleep(time);
				}
		catch (Exception e) {}
	}
	
	public static void TwerkLeft(int time){
		try {
		Motor.A.backward();
		Motor.B.backward();
		Motor.C.forward();
		Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void TwerkRight(int time){
		try {
		Motor.A.forward();
		Motor.B.backward();
		Motor.C.backward();
		Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void Left(int time){
		try {
			useTurningSpeedWithFlankRatio();
		Motor.A.backward();
		Motor.B.forward();
		Motor.C.forward();
		Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void LeftReverse(int time){
		try {
			useTurningSpeedWithFlankRatio();
			Motor.A.forward();
			Motor.B.backward();
			Motor.C.backward();
		Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void Right(int time){
		try {
			useTurningSpeedWithFlankRatio();
		Motor.A.forward();
		Motor.B.forward();
		Motor.C.backward();
		Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void Brake() {
		Motor.A.stop(true);
		Motor.B.stop(true);
		Motor.C.stop(true);
	}
	
	// both flanks at same speed, middle can go at independent speed
	private static void setMotorSpeeds(int flankSpeed, int middleSpeed) {
		Motor.A.setSpeed(flankSpeed);
		Motor.B.setSpeed(middleSpeed);
		Motor.C.setSpeed(flankSpeed);
	}
	
	// flanks at different speeds, middle can go at independent speed
	private static void setMotorSpeeds(int flankSpeed, float middleSpeed, float flankRatio) {
		Motor.A.setSpeed(flankSpeed);
		Motor.B.setSpeed(middleSpeed);
		
		float widerFlankSpeed = flankSpeed*flankRatio; 
		Motor.C.setSpeed(widerFlankSpeed);
	}
	
	private static int baseSpeed = 400;
	
	private static void useDrivingSpeed() {
		setMotorSpeeds(baseSpeed, baseSpeed);
	}
	
	private static void useTurningSpeed() {
		setMotorSpeeds(baseSpeed, baseSpeed/4);
	}
	
	private static void useTurningSpeedWithFlankRatio() {
		float middleSpeed = baseSpeed/3;
		
		setMotorSpeeds(baseSpeed, middleSpeed, 1.5f);
	}
}