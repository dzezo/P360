package main;

import frames.MainFrame;
import frames.MapDrawingFrame;
import glRenderer.Camera;
import glRenderer.DisplayManager;
import glRenderer.Renderer;
import glRenderer.Scene;
import gui.GuiNavButtons;
import gui.GuiRenderer;
import input.InputManager;
import shaders.GuiShader;
import shaders.StaticShader;
import utils.Loader;

public class Main {
	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame("P360");
		MapDrawingFrame mapFrame = mainFrame.getMapFrame();
		Camera camera = new Camera();
		StaticShader shader = new StaticShader();
		GuiShader guiShader = new GuiShader();
		Scene.setCamera(camera);
		GuiNavButtons.init();
		
		while(mainFrame.isRunning()) {
			if(mainFrame.isNewImage()) {
				Scene.loadNewImage();
			}
			if(mainFrame.isNewMap() || mapFrame.isMapUpdated()) {
				Scene.loadNewMap();
			}
			if(Scene.isReady()) {
				InputManager.readInput();
				GuiNavButtons.update();
				Renderer.prepare();
				shader.start();
				Renderer.render(shader);
				GuiRenderer.render(guiShader);
				shader.stop();
			}
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
