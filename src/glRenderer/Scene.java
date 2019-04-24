package glRenderer;

import gui.GuiNavButtons;
import panorama.PanNode;
import panorama.Panorama;

public class Scene {
	private static PanNode activePanorama;
	private static PanNode nextActivePanorama;
	private static Camera camera;
	private static boolean ready;
	
	public static void loadNewImage(PanNode newImage) {
		// reset ready flag
		ready = false;
		
		// set image to load
		activePanorama = newImage;
		
		// prepare renderer
		initScene();
		
		// set ready flag
		ready = true;
	}
	
	public static void loadNewMap() {
		// reset ready flag
		ready = false;
				
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
	
	public static void setNextActivePanorama(PanNode nextPanorama) {
		nextActivePanorama = nextPanorama;
	}
	
	public static PanNode getNextActivePanorama() {
		return nextActivePanorama;
	}
	
	/**
	 * Interfejs za navigaciju dugmicima
	 */
	public static void goLeft() {
		if(activePanorama.getLeft() !=null) {
			loadNewImage(activePanorama.getLeft());
		}
	}
	
	public static void goRight() {
		if(activePanorama.getRight() !=null) {
			loadNewImage(activePanorama.getRight());
		}
	}
	
	public static void goTop() {
		if(activePanorama.getTop() !=null) {
			loadNewImage(activePanorama.getTop());
		}
	}
	
	public static void goBot() {
		if(activePanorama.getBot() !=null) {
			loadNewImage(activePanorama.getBot());
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