package main;

import frames.MainFrame;
import frames.MapDrawFrame;
import frames.MapViewFrame;
import glRenderer.Camera;
import glRenderer.DisplayManager;
import glRenderer.Renderer;
import glRenderer.Scene;
import glRenderer.TourManager;
import gui.GuiNavButtons;
import gui.GuiRenderer;
import input.InputManager;
import shaders.GuiShader;
import shaders.StaticShader;
import utils.Loader;

public class Main {
	public static void main(String[] args) {
		// init
		MainFrame mainFrame = new MainFrame("P360");
		MapDrawFrame mapFrame = mainFrame.getMapDrawingFrame();
		MapViewFrame mapView = mainFrame.getMapViewFrame();
		
		Camera camera = new Camera();
		StaticShader shader = new StaticShader();
		GuiShader guiShader = new GuiShader();
		Scene.setCamera(camera);
		GuiNavButtons.init();
		
		while(mainFrame.isRunning()) {
			// check for changes
			if(mainFrame.isNewImage() || mapView.isUpdated()) {
				Scene.loadNewImage();
			}
			if(mainFrame.isNewMap() || mapFrame.isUpdated()) {
				Scene.loadNewMap();
			}
			if(TourManager.hasPath() && TourManager.nextPano()) {
				TourManager.goNextPano();
			}
			
			if(Scene.isReady()) {
				// read inputs
				InputManager.readInput();
				GuiNavButtons.update();
				
				// render
				Renderer.prepare();
				shader.start();
				Renderer.render(shader);
				GuiRenderer.render(guiShader);
				shader.stop();
			}
			
			// update display
			DisplayManager.updateDisplay();
		}
		
		// Releasing resources
		shader.cleanUp();
		guiShader.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		
		// Everything is released exit
		System.exit(0);
	}
}
