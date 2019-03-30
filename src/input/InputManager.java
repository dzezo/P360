package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

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
	private static float pitchDelta;
	private static float yawDelta;
	private	static final float mouseSensitivity = 0.1f;
	private	static final float dampingFactor = 0.1f; // range from 1 to 0
	private	static final float terminateDamping = 0.01f; // when to stop damping
	// Keyboard movement config
	private static final float yawSpeed = 0.5f;
	private static final float pitchSpeed = 0.25f;
	// Viewer AutoPan config
	private static boolean autoPanEnabled = true;
	private static final float autoPanSpeed = 0.05f;
	private	static final float pitchDampingFactor = 0.005f; // range from 1 to 0
	private static final long autoPanLatency = 3500; // in milis
	private static long lastInteractTime;
	private static long currentTime;
	
	public static void readInput() {
		readKeyboard();
		readMouse();
		if(controller != null)
			readControler();
		autoPan();
	}
	
	public static void setAutoPan() {
		autoPanEnabled = !autoPanEnabled;
		lastInteractTime = 0;
		MainFrame.autoPan(autoPanEnabled);
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
		// Keyboard
		// User interact cases
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
		
		if(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				int key = Keyboard.getEventKey();
				if(Display.isFullscreen() && !isInteractKey(key))
					DisplayManager.setWindowed();
				if(!Display.isFullscreen() && key == K_FSCREEN)
					DisplayManager.setFullscreen();
				if(key == K_LPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goLeft();
				}
				if(key == K_RPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goRight();
				}
				if(key == K_TPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goTop();
				}
				if(key == K_BPAN) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goBot();
				}
				if(key == K_PAN)
					setAutoPan();
			}
		}				
	}
	
	private static void readMouse() {
		// Mouse
		// Reading user interaction
		if(GuiNavButtons.isMouseNear()) {
			GuiNavButtons.showAll();
		}
		if(Mouse.isButtonDown(0)) {
			pitchDelta = Mouse.getDY() * mouseSensitivity;
			yawDelta = Mouse.getDX() * mouseSensitivity;
			// Last time user interacted
			lastInteractTime = System.currentTimeMillis();
		}
		// Detecting interaction
		if(Math.abs(yawDelta) > 0) {
			Scene.getCamera().yaw(-yawDelta);
			if(Math.abs(yawDelta) < terminateDamping)
				yawDelta = 0;
			else
				yawDelta *= (1-dampingFactor);
		}
		if(Math.abs(pitchDelta) > 0) {
			Scene.getCamera().pitch(pitchDelta);
			if(Math.abs(pitchDelta) < terminateDamping)
				pitchDelta = 0;
			else
				pitchDelta *= (1-dampingFactor);
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
					if(Display.isFullscreen())
						DisplayManager.setWindowed();
					else
						DisplayManager.setFullscreen();
				}
				else if(controller.isButtonPressed(GP_PAN)) {
					setAutoPan();
				}
			}
		}
	}
	
	private static void autoPan() {
		float pitch;
		if (autoPanEnabled) {
			// Auto Pan can begin if not disabled
			currentTime = System.currentTimeMillis();
			if(currentTime >= (lastInteractTime + autoPanLatency)) {
				// User is not interacting
				// Bring down camera pitch if it's not leveled
				pitch = Scene.getCamera().getPitch();
				if(pitch != 0) {
					if(Math.abs(pitch) > terminateDamping) {
						pitch *= (1 - pitchDampingFactor);
						Scene.getCamera().setPitch(pitch);
					}
					else
						Scene.getCamera().setPitch(0);
				}
				// Pan
				Scene.getCamera().yaw(autoPanSpeed);
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
				key == K_PAN;
	}
	
}
