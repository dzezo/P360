package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import frames.MainFrame;
import glRenderer.DisplayManager;
import glRenderer.Scene;
import gui.GuiNavButtons;

public class InputManager {
	// Keyboard controls
	public static final int K_LEFT = Keyboard.KEY_LEFT;
	public static final int K_RIGHT = Keyboard.KEY_RIGHT;
	public static final int K_UP = Keyboard.KEY_UP;
	public static final int K_DOWN = Keyboard.KEY_DOWN;
	public static final int K_LPAN = Keyboard.KEY_A;
	public static final int K_RPAN = Keyboard.KEY_D;
	public static final int K_TPAN = Keyboard.KEY_W;
	public static final int K_BPAN = Keyboard.KEY_S;
	public static final int K_FSCREEN = Keyboard.KEY_F;
	public static final int K_PAN = Keyboard.KEY_P;
	public static final int K_MAP = Keyboard.KEY_M;
	
	// Gamepad controls
	private static Controller controller;
	private static boolean readAxis;
	public static int GP_YAXIS;
	public static int GP_XAXIS;
	public static final int GP_LPAN = 3;
	public static final int GP_RPAN = 1;
	public static final int GP_TPAN = 0;
	public static final int GP_BPAN = 2;
	public static final int GP_FSCREEN = 6;
	public static final int GP_PAN = 7;
	
	// Mouse movement config
	private	static final float mouseSensitivity = 0.1f;
	
	// Mouse double click
	private static boolean click = false;
	private static long clickTime;
	private static final long doubleClickLatency = 300; // in milis
	
	// Keyboard movement config
	private static final float yawSpeed = 0.5f;
	private static final float pitchSpeed = 0.25f;
	
	// time of last interaction in milis
	public static long lastInteractTime;
	
	public static void readInput() {
		readKeyboard();
		readMouse();
		if(controller != null)
			readControler();
	}
	
	public static void setController(Controller c) {
		controller = c;
		if(controller.getAxisCount() != 0) {
			for(int i=0; i<controller.getAxisCount(); i++) {
				String axisName = controller.getAxisName(i);
				if(axisName.startsWith("Y"))
					GP_YAXIS = i;
				else if(axisName.startsWith("X"))
					GP_XAXIS = i;
			}
			readAxis = true;
		}
		else
			readAxis = false;
	}
	
	private static void readKeyboard() {
		// Spammable keys
		if(Keyboard.isKeyDown(K_LEFT)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().yaw(-yawSpeed);
		}
		if(Keyboard.isKeyDown(K_RIGHT)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().yaw(yawSpeed);
		}
		if(Keyboard.isKeyDown(K_UP)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().pitch(-pitchSpeed);
		}
		if(Keyboard.isKeyDown(K_DOWN)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().pitch(pitchSpeed);
		}
		
		// Non-spammable keys
		if(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				int key = Keyboard.getEventKey();
				if(DisplayManager.isFullscreen() && !isInteractKey(key)) {
					DisplayManager.setWindowed();
				}
				else if(!DisplayManager.isFullscreen() && key == K_FSCREEN) {
					DisplayManager.setFullscreen();
				}
				else if(key == K_LPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goLeft();
				}
				else if(key == K_RPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goRight();
				}
				else if(key == K_TPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goTop();
				}
				else if(key == K_BPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goBot();
				}
				else if(key == K_PAN) {
					Scene.getCamera().setAutoPan();
				}
				else if(key == K_MAP) {
					if(DisplayManager.isFullscreen()) {
						DisplayManager.setWindowed();
					}
					MainFrame.showMap();
				}
			}
		}				
	}

	private static void readMouse() {
		// detect mouse drag
		if(Mouse.isButtonDown(0) && !GuiNavButtons.isMouseOver()) {
			float pitchDelta = Mouse.getDY() * mouseSensitivity;
			float yawDelta = Mouse.getDX() * mouseSensitivity;
			
			Scene.getCamera().setRotationVelocity(yawDelta, pitchDelta);
			
			// Last time user interacted
			lastInteractTime = System.currentTimeMillis();
		}
		
		// detect left click
		if(Mouse.next() && Mouse.getEventButtonState() && Mouse.isButtonDown(0)) {
			// double click condition
			if (click && clickTime + doubleClickLatency > System.currentTimeMillis()){
				if(DisplayManager.isFullscreen())
					DisplayManager.setWindowed();
				else
					DisplayManager.setFullscreen();
				
				// reset for next detection
				click = false;
			}
			else {
				click = true;
				clickTime = System.currentTimeMillis();
				
				// detect if click happened on gui
				if(!GuiNavButtons.areHidden())
					GuiNavButtons.click();
			}
		}
		else if (click && clickTime + doubleClickLatency < System.currentTimeMillis()) {
			click = false;
		}
	}
	
	private static void readControler() {
		float valueY, valueX;
		if(!controller.poll()) {
			controller = null;
			System.out.println("device disconnected");
			return;
		}
		if(readAxis) {
			valueY = controller.getAxisValue(GP_YAXIS);
			valueX = controller.getAxisValue(GP_XAXIS);
		}
		else {
			valueY = 0;
			valueX = 0;
		}
		if(valueY == 0)
			valueY = controller.getPovY();
		if(valueX == 0)
			valueX = controller.getPovX();
		
		if(valueY != 0) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().pitch(valueY * pitchSpeed);
		}
		if(valueX != 0) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().yaw(valueX * yawSpeed);
		}
		if(Controllers.next() && Controllers.getEventSource() == controller) {
			if(Controllers.isEventButton() && Controllers.getEventButtonState()) {
				if(controller.isButtonPressed(GP_LPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goLeft();
				}
				else if(controller.isButtonPressed(GP_RPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goRight();
				}
				else if(controller.isButtonPressed(GP_TPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goTop();
				}
				else if(controller.isButtonPressed(GP_BPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goBot();
				}
				else if(controller.isButtonPressed(GP_FSCREEN)) {
					if(DisplayManager.isFullscreen())
						DisplayManager.setWindowed();
					else
						DisplayManager.setFullscreen();
				}
				else if(controller.isButtonPressed(GP_PAN)) {
					Scene.getCamera().setAutoPan();
				}
			}
		}
	}
	
	private static boolean isInteractKey(int key) {
		return 
				key == K_LEFT || 
				key == K_RIGHT || 
				key == K_UP || 
				key == K_DOWN ||
				key == K_LPAN || 
				key == K_RPAN || 
				key == K_TPAN || 
				key == K_BPAN ||
				key == K_PAN ||
				key == K_MAP;
	}
	
}
