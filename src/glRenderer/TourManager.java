package glRenderer;

import java.util.Set;

import panorama.PanNode;

public class TourManager implements Runnable{
	private static PanNode prevPano;
	
	private static Set<PanNode> visited;
	
	private static float startAngle;
	
	private static int pathIndex = 0;
	private static boolean goNext = false; 

	public void run() {
		PanNode activePano = Scene.getActivePanorama();
		// return if not set into motion
		if(activePano == null || activePano.getPath() == null || !Scene.getCamera().getAutoPan()) return;
		
		float currentAngle = Scene.getCamera().getYaw();
		
		// first on path
		if(prevPano == null) {
			prevPano = activePano;
			startAngle = currentAngle;
			if(activePano.hasAudio())
				activePano.playAudio();
		}
		
		if(Math.abs(startAngle - currentAngle) > 360.0f && !activePano.isAudioPlaying()) {
			goNext = true;
			//visited.add(prevPano);
			prevPano = activePano;
			startAngle = currentAngle;
			if(activePano.hasAudio())
				activePano.playAudio();
		}
	}
	
	public static int getPathIndex() {
		pathIndex = (pathIndex + 1)%(prevPano.getPath().length - 1);
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
