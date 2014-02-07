import java.io.File;

import lejos.nxt.Sound;

public class WallHugger {
	public static void main(String [] options) throws Exception {
		
		// Calculate number of WAV files
		File file = new File("CheeseLoud.wav");
		Sound.playSample(file, 100);

		Thread.sleep(2000);
		
		Sound.playSample(new File("PetrolLoud.wav"), 100);
		
		Thread.sleep(2000);
	}
}
