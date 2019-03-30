package gui;

import java.util.List;

public interface IButton {
	
	public void onClick(IButton button);
	
	public void onStartHover(IButton button);
	
	public void onStopHover(IButton button);
	
	public void whileHovering(IButton button);
	
	/**
	 * Shows GuiButton
	 * That's done by ADDING button to a list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void show(List<GuiTexture> guiTextureList);
	
	/**
	 * Shows GuiButton
	 * That's done by REMOVING button to a list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void hide(List<GuiTexture> guiTextureList);
	
	public void playHoverAnimation(float scaleFactor);
	
	public void resetScale();
	
	public void update(boolean clicked);
	
}
