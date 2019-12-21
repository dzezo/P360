package panorama;

import java.io.Serializable;

public class PanGraphSize implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected int NORTH;
	protected int SOUTH;
	protected int WEST;
	protected int EAST;
	
	public PanGraphSize() {
		NORTH = SOUTH = WEST = EAST = 0;
	}
	
	public int getCenterX() {
		return (WEST + EAST) / 2;
	}
	
	public int getCenterY() {
		return (NORTH + SOUTH) / 2;
	}
	
	public void adjustBounds(PanNode node) {
		WEST = EAST = node.getMapNode().x;
		NORTH = SOUTH = node.getMapNode().y;
	}
	
	public void updateSize(PanNode node) {
		int x = node.getMapNode().x;
		int y = node.getMapNode().y;
		
		if(x <= WEST) WEST = x;
		if(x >= EAST) EAST = x;
		if(y <= NORTH) NORTH = y;
		if(y >= SOUTH) SOUTH = y;
	}
}
