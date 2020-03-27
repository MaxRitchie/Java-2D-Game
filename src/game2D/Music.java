package game2D;

import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * 
 * @author Max Ritchie - 2636157
 * 
 *         Plays audio that has been passed in
 *
 */
public class Music {

	Clip clip = null;

	/**
	 * Plays audio that is passed in on repeat until the game has been closed
	 * 
	 * @param file an audio file that gets passed in to be played
	 */
	public void playMusic(String file) {
		try {
			// Creates a file instance to hold an audio file that has been passed into the
			// method
			File music = new File(file);
			// Creates an audio input stream that obtains an audio input steam provided from
			// the music file
			AudioInputStream audioInput = AudioSystem.getAudioInputStream(music);
			// Creates a clip to load in audio
			clip = AudioSystem.getClip();
			// Opens the clip with the format and audio data present in the provided audio
			// input stream
			clip.open(audioInput);
			// Starts the clip plays the audio stored within
			clip.start();
			// Keeps the audio in a infinite loop
			clip.loop(Clip.LOOP_CONTINUOUSLY);

			// Displays an error message to the console if the music file cannot be found
		} catch (FileNotFoundException e) {
			System.out.println("Music file could not be found");
			// Displays an error message to the console if the music could not play
		} catch (Exception e) {
			System.out.println("Error while playing music");
		}
	}

	public void stopMusic() {
		clip.close();
	}
}
