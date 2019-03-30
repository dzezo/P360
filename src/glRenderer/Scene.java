package glRenderer;

import gui.GuiNavButtons;
import textures.PanNode;
import textures.Panorama;

public class Scene {
	private static PanNode activePanorama;
	private static Camera camera;
	private static boolean ready;
	
	public static void loadNewImage() {
		activePanorama.loadPanorama();
		Renderer.setNewProjection();
		GuiNavButtons.setAvailableNavButtons(activePanorama);
		ready = true;
	}
	
	public static void loadNewMap() {
		activePanorama = PanNode.getHome();
		activePanorama.loadPanorama();
		Renderer.setNewProjection();
		GuiNavButtons.setAvailableNavButtons(activePanorama);
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
			activePanorama.loadPanorama();
			Renderer.setNewProjection();
			GuiNavButtons.setAvailableNavButtons(activePanorama);
		}
	}
	
	public static void goRight() {
		if(activePanorama.getRight() !=null) {
			activePanorama = activePanorama.getRight();
			activePanorama.loadPanorama();
			Renderer.setNewProjection();
			GuiNavButtons.setAvailableNavButtons(activePanorama);
		}
	}
	
	public static void goTop() {
		if(activePanorama.getTop() !=null) {
			activePanorama = activePanorama.getTop();
			activePanorama.loadPanorama();
			Renderer.setNewProjection();
			GuiNavButtons.setAvailableNavButtons(activePanorama);
		}
	}
	
	public static void goBot() {
		if(activePanorama.getBot() !=null) {
			activePanorama = activePanorama.getBot();
			activePanorama.loadPanorama();
			Renderer.setNewProjection();
			GuiNavButtons.setAvailableNavButtons(activePanorama);
		}
	}
}