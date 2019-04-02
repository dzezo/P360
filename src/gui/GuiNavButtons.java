package gui;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import glRenderer.DisplayManager;
import glRenderer.Scene;
import panorama.PanNode;

public class GuiNavButtons {
	private static GuiButton navTop;
	private static GuiButton navBot;
	private static GuiButton navRight;
	private static GuiButton navLeft;
	
	private static boolean navTopAvail = false;
	private static boolean navBotAvail = false;
	private static boolean navRightAvail = false;
	private static boolean navLeftAvail = false;
	
	private static boolean areHidden = false;
	
	private static long currentTime;
	private static long lastShowTime;
	private static final long hideLatency = 3000; // in milis
	
	private static float btnLocation = 0.95f;
	private static float btnArea = 0.875f;
	private static float btnScaleX = 0.07f;
	private static float btnScaleY = 0.07f;
	
	public static void init() {
		navTop = new GuiButton("/nav/top.png", new Vector2f(0,btnLocation), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goTop();
			}
			
			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navBot = new GuiButton("/nav/bot.png", new Vector2f(0,-btnLocation), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goBot();
			}
			
			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navLeft = new GuiButton("/nav/left.png", new Vector2f(-btnLocation,0), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goLeft();	
			}

			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navRight = new GuiButton("/nav/right.png", new Vector2f(btnLocation,0), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goRight();
			}

			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
	}
	
	public static void hideAll() {
		if(!areHidden) {
			List<GuiTexture> guis = GuiRenderer.getGuiList();
			
			if(navTopAvail)
				navTop.hide(guis);
			
			if(navBotAvail)
				navBot.hide(guis);
			
			if(navRightAvail)
				navRight.hide(guis);
			
			if(navLeftAvail)
				navLeft.hide(guis);
			
			areHidden = true;
		}
	}
	
	public static void showAll() {
		lastShowTime = System.currentTimeMillis();
		if(areHidden) {
			List<GuiTexture> guis = GuiRenderer.getGuiList();
			
			if(navTopAvail)
				navTop.show(guis);
			
			if(navBotAvail)
				navBot.show(guis);
			
			if(navRightAvail)
				navRight.show(guis);
			
			if(navLeftAvail)
				navLeft.show(guis);
			
			areHidden = false;
		}
	}
	
	public static boolean areHidden() {
		return areHidden;
	}
	
	public static void setAvailableNavButtons(PanNode node) {
		List<GuiTexture> guis = GuiRenderer.getGuiList();
		lastShowTime = System.currentTimeMillis();
		
		if(node.getTop() != null) {
			navTop.show(guis);
			navTopAvail = true;
		}
		else {
			navTop.hide(guis);
			navTopAvail = false;
		}
		
		if(node.getBot() != null) {
			navBot.show(guis);
			navBotAvail = true;
		}
		else {
			navBot.hide(guis);
			navBotAvail = false;
		}
		
		if(node.getRight() != null) {
			navRight.show(guis);
			navRightAvail = true;
		}
		else {
			navRight.hide(guis);
			navRightAvail = false;
		}
		
		if(node.getLeft() != null) {
			navLeft.show(guis);
			navLeftAvail = true;
		}
		else {
			navLeft.hide(guis);
			navLeftAvail = false;
		}
	}
	
	public static boolean isMouseNear() {
		Vector2f mouseCoord = DisplayManager.getNormalizedMouseCoords();
		if(Math.abs(mouseCoord.x) > btnArea || Math.abs(mouseCoord.y) > btnArea)
			return true;
		else
			return false;
	}
	
	public static void update() {
		currentTime = System.currentTimeMillis();
		if(currentTime >= lastShowTime + hideLatency)
			hideAll();
		
		boolean clicked = false;
		while(Mouse.next())
			if(Mouse.getEventButton() == 0)
				if(Mouse.getEventButtonState()) 
					clicked = true;
		
		navTop.update(clicked);
		navBot.update(clicked);
		navRight.update(clicked);
		navLeft.update(clicked);
	}
}
