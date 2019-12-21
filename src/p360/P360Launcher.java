package p360;

import static utils.ConfigData.WORKING_DIR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import frames.MainFrame;
import frames.MapDrawFrame;
import frames.MapViewFrame;
import glRenderer.AudioManager;
import glRenderer.Camera;
import glRenderer.DisplayManager;
import glRenderer.Renderer;
import glRenderer.Scene;
import gui.GuiNavButtons;
import gui.GuiRenderer;
import gui.GuiSprites;
import input.InputManager;
import loaders.IconLoader;
import loaders.ImageLoader;
import shaders.GuiShader;
import shaders.StaticShader;
import utils.AutoLoad;
import utils.ChooserUtils;
import utils.ConfigData;
import utils.Loader;

public class P360Launcher {
	
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
		ChooserUtils.setWorkingDir();
		
		// init
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				MapDrawFrame.getInstance();
				MapViewFrame.getInstance();
				MainFrame.getInstance();
			}
		});
		// Attach OpenGL display to mainframe
		DisplayManager.createDisplay(MainFrame.getInstance().getCanvas());
		
		Camera camera = new Camera();
		StaticShader shader = new StaticShader();
		GuiShader guiShader = new GuiShader();
		
		Scene.setCamera(camera);
		GuiNavButtons.init();
		GuiSprites.init();
		
		// load previously used map
		ImageLoader imageLoader = ImageLoader.getInstance();
		IconLoader iconLoader = IconLoader.getInstance();
		AutoLoad.load();
		
		while(MainFrame.getInstance().isRunning()) {
			// check for changes
			if(Scene.changeRequested()) {
				if(imageLoader.isLoaded()) {
					Scene.loadNewActivePanorama(Scene.getQueuedPanorama());
					imageLoader.resetLoader();
				}
				else if(!imageLoader.isLoading()){
					AudioManager.stopAudio();
					Scene.unloadActivePanorama();
					imageLoader.loadImage(Scene.getQueuedPanorama().getPanoramaPath());
				}
				else if(imageLoader.isCanceled()) {
					Scene.dequeuePanorama();
					imageLoader.resetLoader();
				}
			}
			
			// Prepare renderer
			Renderer.prepare();
			
			if(Scene.isReady()) {
				if(!imageLoader.isLoading()) {
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
			GuiSprites.update();
			GuiRenderer.render(guiShader);
			
			// update display
			DisplayManager.serveRequests();
			DisplayManager.updateDisplay();
			
		}
		
		// stop audio
		AudioManager.stopAudio();
		
		// stop loaders
		imageLoader.doStop();
		iconLoader.doStop();
		
		// Releasing resources
		shader.cleanUp();
		guiShader.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		
		// Save config data
    	ConfigData.updateConfigFile();
    	
    	// Disposing frames
    	SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				MapViewFrame.getInstance().cleanUp();
		    	MapDrawFrame.getInstance().cleanUp();
		    	MainFrame.getInstance().cleanUp();
			}
		});
		
		// Everything is released exit
		System.exit(0);
	}
}
