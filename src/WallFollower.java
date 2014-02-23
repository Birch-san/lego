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
			useTurningSpeed(true);
			Motor.A.backward();
			Motor.B.forward();
			Motor.C.forward();
			Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	// all motors forward, in different amounts
	public static void LeftSteer(int time){
		try {
			useForwardSteeringSpeed(true);
			Motor.A.forward();
			Motor.B.forward();
			Motor.C.forward();
			Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void LeftReverse(int time){
		try {
			useTurningSpeed(true);
			Motor.A.forward();
			Motor.B.backward();
			Motor.C.backward();
			Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	public static void Right(int time){
		try {
			useTurningSpeed(false);
			Motor.A.forward();
			Motor.B.forward();
			Motor.C.backward();
			Thread.sleep(time);
		} catch (Exception e) {}
	}
	
	// all motors forward, in different amounts
	public static void RightSteer(int time){
		try {
			useForwardSteeringSpeed(false);
			Motor.A.forward();
			Motor.B.forward();
			Motor.C.forward();
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
	private static void setMotorSpeeds(float leftSpeed, float middleSpeed, float rightSpeed) {
		Motor.A.setSpeed(leftSpeed);
		Motor.B.setSpeed(middleSpeed);
		Motor.C.setSpeed(rightSpeed);
	}
	
	private static int baseSpeed = 400;
	
	private static void useDrivingSpeed() {
		setMotorSpeeds(baseSpeed, baseSpeed);
	}
	
	private static void useTurningSpeed(boolean goingLeft) {
		//setMotorSpeeds(baseSpeed, baseSpeed/4);
		
		float middleSpeed = baseSpeed/3;
		float turnRatio = 1.5f;
		
		if (goingLeft) {
			setMotorSpeeds(baseSpeed, middleSpeed, baseSpeed*turnRatio);
		} else {
			setMotorSpeeds(baseSpeed*turnRatio, middleSpeed, baseSpeed);
		}
	}
	
	private static void useForwardSteeringSpeed(boolean goingLeft) {
		int baseSpeed = 225;
		
		float middleSpeed = baseSpeed;
		float turnRatio = 4.0f;
		
		if (goingLeft) {
			setMotorSpeeds(baseSpeed, middleSpeed, baseSpeed*turnRatio);
		} else {
			setMotorSpeeds(baseSpeed*turnRatio, middleSpeed, baseSpeed);
		}
	}
}