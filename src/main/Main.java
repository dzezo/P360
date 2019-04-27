package main;

import javax.swing.UIManager;

import frames.MainFrame;
import glRenderer.AudioManager;
import glRenderer.Camera;
import glRenderer.DisplayManager;
import glRenderer.Renderer;
import glRenderer.Scene;
import glRenderer.TourManager;
import gui.GuiNavButtons;
import gui.GuiRenderer;
import gui.GuiSprites;
import input.InputManager;
import panorama.ImageLoader;
import panorama.PanNode;
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
		
		Camera camera = new Camera();
		StaticShader shader = new StaticShader();
		GuiShader guiShader = new GuiShader();
		
		Scene.setCamera(camera);
		GuiNavButtons.init();
		GuiSprites.init();
		
		// load previously used map
		if(AutoLoad.load()) {
			mainFrame.enableMapControl(true);
			mainFrame.enableFullScreen();
			
			Scene.setNextActivePanorama(PanNode.getHome());
			TourManager.prepare(PanNode.getHome());
		}
		
		while(mainFrame.isRunning()) {
			// check for changes
			if(Scene.getActivePanorama() != Scene.getNextActivePanorama()) {		
				if(Scene.getNextActivePanorama().isLoaded() || ImageLoader.isLoaded()) {
					mainFrame.enableFullScreen();
							
					Scene.loadNewImage(Scene.getNextActivePanorama());
					
					ImageLoader.resetLoader();
				}
				else if(!ImageLoader.isLoading()){
					AudioManager.stopAudio();		
					ImageLoader.loadImage(Scene.getNextActivePanorama().getPanoramaPath());
				}		
			}
			
			if(Scene.isReady() && !ImageLoader.isLoading()) {
				// read input
				InputManager.readInput();
				
				// move camera
				Scene.getCamera().rotateCamera();
				Scene.getCamera().autoPan();
				
				// update gui buttons
				GuiNavButtons.update();
				
				// render
				Renderer.prepare();
				Renderer.render(shader);
				
			}
			
			// render gui
			GuiRenderer.render(guiShader);
			
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
