import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

public class WallFollower
{
	public static void main (String[] args){
		try {
		LCD.drawString("Hello World",0,0);
		Button.waitForAnyPress();
		Motor.B.forward();
		LCD.drawString("Went Forward",0,0);
		Thread.sleep(500);
		Motor.B.stop();
		LCD.drawString("Stopped",0,0);
		Thread.sleep(500);
		Button.waitForAnyPress();
		}
		catch (Exception e) {}
	}
}