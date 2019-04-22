package main;

import javax.swing.UIManager;

import frames.MainFrame;
import frames.MapDrawFrame;
import frames.MapViewFrame;
import glRenderer.AudioManager;
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
import utils.AutoLoad;
import utils.Loader;

public class Main {
	public static void main(String[] args) throws Exception {
		// set system look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// init
		MainFrame mainFrame = new MainFrame("P360");
		MapDrawFrame mapFrame = mainFrame.getMapDrawingFrame();
		MapViewFrame mapView = mainFrame.getMapViewFrame();
		
		Camera camera = new Camera();
		StaticShader shader = new StaticShader();
		GuiShader guiShader = new GuiShader();
		
		Scene.setCamera(camera);
		GuiNavButtons.init();
		
		// load previously used map
		if(AutoLoad.load()) {
			Scene.loadNewMap();
			mainFrame.enableMapControl(true);
		}
		
		while(mainFrame.isRunning()) {
			// check for changes
			if(mainFrame.isNewImage() || mapView.isUpdated()) {
				Scene.loadNewImage();
			}
			if(mainFrame.isNewMap() || mapFrame.isUpdated()) {
				AudioManager.stopAudio();
				Scene.loadNewMap();
			}
			if(TourManager.hasPath() && TourManager.nextPano()) {
				TourManager.goNextPano();
			}
			
			if(Scene.isReady()) {
				// read inputs
				InputManager.readInput();
				
				// move camera
				Scene.getCamera().rotateCamera();
				Scene.getCamera().autoPan();
				
				// update gui
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
		
		// stop audio
		AudioManager.stopAudio();
		
		// Releasing resources
		shader.cleanUp();
		guiShader.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		
		// Everything is released exit
		System.exit(0);
	}
}
