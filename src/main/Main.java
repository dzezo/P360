package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import panorama.PanGraph;
import shaders.GuiShader;
import shaders.StaticShader;
import utils.AutoLoad;
import utils.ChooserUtils;
import utils.Loader;

public class Main {
	public static final File WORKING_DIR = new File("C:\\p360system");
	
	public static void main(String[] args) throws Exception {
		// set system look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// create working dir
		try {
			Files.createDirectory(Paths.get(WORKING_DIR.getPath()));
		} catch (IOException e) {
			System.out.println("Radni direktorijum postoji.");
		}
		
		// set working dir
		ChooserUtils.setWorkingDir(WORKING_DIR);
		
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
			
			Scene.setNextActivePanorama(PanGraph.getHome());
			
			TourManager.prepare(PanGraph.getHome());
			if(TourManager.hasPath())
				DisplayManager.setFullscreen();
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
				else if(ImageLoader.isCanceled()) {
					Scene.setNextActivePanorama(Scene.getActivePanorama());
					
					ImageLoader.resetLoader();
				}
			}
			
			// Prepare renderer
			Renderer.prepare();
			
			if(Scene.isReady()) {
				if(!ImageLoader.isLoading()) {
					InputManager.readInput();		
				}
				
				// move camera
				Scene.getCamera().rotateCamera();
				Scene.getCamera().autoPan();
				
				// update gui buttons
				GuiNavButtons.update();
				
				// render scene
				Renderer.render(shader);
			}
			
			// render gui graphics
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
