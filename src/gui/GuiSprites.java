package gui;

import org.lwjgl.util.vector.Vector2f;

public class GuiSprites {
	public static GuiSprite loading;
	public static GuiSprite cancel;
	
	public static void init() {
		loading = new GuiSprite("/sprites/loading.png", new Vector2f(0,0), new Vector2f(0.075f, 0.075f)) {};
		cancel = new GuiSprite("/sprites/cancel.png", new Vector2f(0,0), new Vector2f(0.075f, 0.075f)) {};
	}
}
