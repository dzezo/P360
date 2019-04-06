package glRenderer;

import frames.MainFrame;
import panorama.PanNode;

public class AudioManager implements Runnable {
	private MainFrame mainFrame;
	private boolean controlsEnabled = false;
	private boolean playText = true;
	
	private PanNode prevActivePano;
	
	public AudioManager(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public void run() {
		PanNode activePano = Scene.getActivePanorama();
		if(activePano == null) return;
		
		// Stop audio when switching scenes
		if(prevActivePano == null)
			prevActivePano = activePano;
		else if(!activePano.equals(prevActivePano)) {
			if(prevActivePano.isAudioPlaying())
				prevActivePano.stopAudio();
			prevActivePano = activePano;
		}
		
		// Enable/Disable audio controls
		if(activePano.hasAudio() && !controlsEnabled) {
			mainFrame.enableSoundControl(true);
			controlsEnabled = true;
		}
		else if(!activePano.hasAudio() && controlsEnabled) {
			mainFrame.enableSoundControl(false);
			controlsEnabled = false;
		}
		
		// Set play/pause text
		if(activePano.isAudioPlaying() && playText || !activePano.isAudioPlaying() && !playText) {
			mainFrame.setPlayPauseText(activePano.isAudioPlaying());
			playText = !playText;
		}
	}

}
