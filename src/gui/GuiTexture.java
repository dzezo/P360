package gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GuiTexture {
	private int texture;
	private Vector2f position;
	private Vector3f rotation;
	private Vector2f scale;
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		super();
		this.texture = texture;
		this.position = position;
		this.rotation = new Vector3f(0, 0, 0);
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(float rx, float ry, float rz) {
		this.rotation.set(rx, ry, rz);
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setScale(float sx, float sy) {
		this.scale.set(sx, sy);
	}
	
}
