package models;

public abstract class Body {
	protected int vaoID;
	
	protected float radius;
	protected float posCoords[];
	protected float texCoords[];
	protected int indices[];
	
	public int getVaoID() {
		return vaoID;
	}

	public int getIndicesCount() {
		return indices.length;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public abstract int getPrimitiveType();
}
