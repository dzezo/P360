package glRenderer;

import frames.MainFrame;
import gui.GuiNavButtons;
import panorama.PanNode;
import panorama.Panorama;

public class Scene {
	private static PanNode activePanorama;
	private static Camera camera;
	private static boolean ready;
	
	/**
	 * Postavlja odabranu/aktivnu panoramu na scenu
	 */
	private static void initScene() {
		if(activePanorama.hasAudio()) {
			MainFrame.enableSoundControl(true);
		}
		else
			MainFrame.enableSoundControl(false);
		activePanorama.loadPanorama();
		Renderer.setNewProjection();
		GuiNavButtons.setAvailableNavButtons(activePanorama);
	}
	
	public static void loadNewImage() {
		initScene();
		ready = true;
	}
	
	public static void loadNewMap() {
		activePanorama = PanNode.getHome();
		initScene();
		ready = true;
	}
	
	public static boolean isReady() {
		return ready;
	}
	
	public static void setReady(boolean b) {
		ready = b;
	}
	
	public static Panorama getPanorama() {
		return activePanorama.getPanorama();
	}
	
	public static Camera getCamera() {
		return camera;
	}
	
	public static void setCamera(Camera cam) {
		camera = cam;
	}
	
	public static PanNode getActivePanorama() {
		return activePanorama;
	}
	
	public static void setActivePanorama(PanNode newActivePanorama) {
		activePanorama = newActivePanorama;
	}
	
	public static void goLeft() {
		if(activePanorama.getLeft() !=null) {
			activePanorama = activePanorama.getLeft();
			initScene();
		}
	}
	
	public static void goRight() {
		if(activePanorama.getRight() !=null) {
			activePanorama = activePanorama.getRight();
			initScene();
		}
	}
	
	public static void goTop() {
		if(activePanorama.getTop() !=null) {
			activePanorama = activePanorama.getTop();
			initScene();
		}
	}
	
	public static void goBot() {
		if(activePanorama.getBot() !=null) {
			activePanorama = activePanorama.getBot();
			initScene();
		}
	}
}