package panorama;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

@SuppressWarnings("serial")
public class PanAudio implements Serializable {
	private File audioFile;
	
	private AudioInputStream audioInput;
	
	private Clip audioClip;
	private long audioClipTimePos = 0;
	
	private boolean isPlaying = false;
	
	public PanAudio(String loc) {
		audioFile = new File(loc);
	}
	
	public void play() {
		try {
			audioInput = AudioSystem.getAudioInputStream(audioFile);
			audioClip = AudioSystem.getClip();	
			audioClip.open(audioInput);
			audioClip.setMicrosecondPosition(audioClipTimePos);
			audioClip.start();
			isPlaying = true;
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		audioClipTimePos = audioClip.getMicrosecondPosition();
		audioClip.stop();
		isPlaying = false;
		audioClip.close();
	}
	
	public void stop() {
		audioClipTimePos = 0;
		audioClip.stop();
		isPlaying = false;
		audioClip.close();
	}
	
	private boolean audioReachedEnd() {
		return (audioClip.getMicrosecondPosition() == audioClip.getMicrosecondLength());
	}
	
	public String getLocation() {
		return audioFile.getPath();
	}
	
	public boolean isPlaying() {
		if(isPlaying && audioReachedEnd())
			this.stop();
		return this.isPlaying;
	}
	
}
