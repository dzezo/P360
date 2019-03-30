package gui;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import glRenderer.DisplayManager;
import utils.Loader;

public abstract class GuiButton implements IButton {
	
	private GuiTexture guiTexture;
	private Vector2f originalScale;
	private boolean isHidden = true;
	private boolean isHovering = false;
	
	public GuiButton(String texturePath, Vector2f position, Vector2f scale) {
		guiTexture = new GuiTexture(Loader.loadTexture(texturePath), position, scale);
		originalScale = scale;
	}

	public void show(List<GuiTexture> list) {
		if(isHidden) {
			list.add(guiTexture);
			isHidden = false;
		}
	}

	public void hide(List<GuiTexture> list) {
		if(!isHidden) {
			list.remove(guiTexture);
			isHidden = true;
		}
	}

	public void playHoverAnimation(float scaleFactor) {
		guiTexture.setScale(new Vector2f(originalScale.x + scaleFactor, originalScale.y + scaleFactor));
	}

	public void resetScale() {
		guiTexture.setScale(originalScale);
	}

	public void update(boolean clicked) {
		if(!isHidden) {
			Vector2f location = guiTexture.getPosition();
			Vector2f scale = guiTexture.getScale();
			Vector2f mouseCoords = DisplayManager.getNormalizedMouseCoords();
			
			if(location.y + scale.y > -mouseCoords.y 
					&& location.y - scale.y < -mouseCoords.y
					&& location.x + scale.x > mouseCoords.x
					&& location.x - scale.x < mouseCoords.x) 
			{
				whileHovering(this);
				if(!isHovering) {
					isHovering = true;
					onStartHover(this);
				}
				if(clicked)
					onClick(this);
			}
			else {
				isHovering = false;
				onStopHover(this);
			}
		}
	}

}
