package glRenderer;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;

public class DisplayManager {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FPS_CAP = 60;
	
	private static boolean fullscreenRequest = false;
	private static boolean windowedRequest = false;
	
	public static void createDisplay(Canvas canvas) {
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);
		
		try{
			Display.setParent(canvas);
			Display.setInitialBackground(0.933f,0.933f,0.933f);
			Display.setTitle("P360");
			Display.create(new PixelFormat(), attribs);
		}
		catch(LWJGLException e){
			e.printStackTrace();
		}
		
		// Where to render on display
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}
	
	public static void updateDisplay(){
		if(fullscreenRequest) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) screenSize.getWidth();
			int height = (int) screenSize.getHeight();
			try {
				Display.setFullscreen(true);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			// Where to render on display
			GL11.glViewport(0, 0, width, height);
			// Request served
			fullscreenRequest = false;
		}
		
		if (windowedRequest) 
		{
			try {
				Display.setFullscreen(false);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			// Recalculate projection matrix
			Renderer.setNewProjection();
			// Where to render on display
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			// Request served
			windowedRequest = false;
		}
		
		if (Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		
		Display.sync(FPS_CAP);
		Display.update();
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
	
	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}
	
	public static void setFullscreen() {
		fullscreenRequest = true;
	}
	
	public static void setWindowed() {
		windowedRequest = true;
	}
	
	public static boolean isFullscreenRequested() {
		return fullscreenRequest;
	}
	
	public static Vector2f getNormalizedMouseCoords() {
		float normalizedX = -1.0f + 2.0f * (float)Mouse.getX() / (float)Display.getWidth();
		float normalizedY = 1.0f - 2.0f * (float)Mouse.getY() / (float)Display.getHeight();
		return new Vector2f(normalizedX, normalizedY);
	}
}
