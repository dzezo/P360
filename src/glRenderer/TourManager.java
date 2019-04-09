package glRenderer;

import panorama.PanNode;

public class TourManager implements Runnable{
	private static PanNode prevPano;
	
	private static float startAngle;
	
	private static int pathIndex = 0;
	private static boolean goNext = false; 

	public void run() {
		PanNode activePano = Scene.getActivePanorama();
		// return if not set into motion
		if(activePano == null || activePano.getPath() == null || !Scene.getCamera().getAutoPan()) return;
		
		float currentAngle = Scene.getCamera().getYaw();
		
		// first pano condition
		if(prevPano == null) {
			prevPano = activePano;
			startAngle = currentAngle;
			if(activePano.hasAudio())
				activePano.playAudio();
		}
		
		// next pano condition
		if(Math.abs(startAngle - currentAngle) > 360.0f && !activePano.isAudioPlaying()) {
			goNext = true;
			prevPano = activePano;
			startAngle = currentAngle;
			if(activePano.hasAudio())
				activePano.playAudio();
		}
	}
	
	public static int getPathIndex() {
		pathIndex = (pathIndex + 1)%prevPano.getPath().length;
		System.out.println(pathIndex);
		return pathIndex;
	}
	
	public static boolean goNext() {
		if(goNext) {
			goNext = false;
			return true;
		}
		else
			return false;
	}
}
