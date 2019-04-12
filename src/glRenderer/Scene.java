package glRenderer;

import gui.GuiNavButtons;
import panorama.PanNode;
import panorama.Panorama;

public class Scene {
	private static PanNode activePanorama;
	private static Camera camera;
	private static boolean ready;
	
	public static void loadNewImage() {
		initScene();
		ready = true;
	}
	
	public static void loadNewMap() {
		// set starting pan
		activePanorama = PanNode.getHome();
		
		// prepare renderer
		initScene();
		
		// check if tour exists
		if(PanNode.tour.hasPath()) {
			TourManager.init(PanNode.tour.getPath(), activePanorama);
		}
		else {
			TourManager.stopTourManager();
		}
		
		// set ready flag
		ready = true;
	}
	
	/**
	 * Getters and Setters
	 */	
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
	
	/**
	 * Interfejs za navigaciju dugmicima
	 */
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
	
	/**
	 * Ucitava aktivnu panoramu na scenu
	 */
	public static void initScene() {
		activePanorama.loadPanorama();
		Renderer.setNewProjection();
		GuiNavButtons.setAvailableNavButtons(activePanorama);
	}
	
}