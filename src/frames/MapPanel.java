package frames;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import panorama.MapNode;
import panorama.PanNode;

@SuppressWarnings("serial")
public abstract class MapPanel extends JComponent {
	protected Color panelColor = new Color(128,128,128);
	
	protected int originX = 0;
	protected int originY = 0;
	protected int mouseX;
	protected int mouseY;
	
	protected PanNode selectedNode1;
	protected PanNode selectedNode2;
	
	protected boolean isNodeSelected(MapNode node) {
		if(selectedNode1 != null && selectedNode1.getMapNode() == node)
			return true;
		if(selectedNode2 != null && selectedNode2.getMapNode() == node)
			return true;
		return false;
	}
	
	protected PanNode getSelectedNode(int x,int y) {
		PanNode selectedNode = null;
		PanNode start = PanNode.getHead();
		while(start != null) {
			MapNode node = start.getMapNode();
			if(node.isPressed(x, y, originX, originY)) {
				selectedNode = start;
			}
			start = start.getNext();
		}
		return selectedNode;
	}
	
	protected void dragPanel(int dragX, int dragY) {
		int dx, dy;
		dx = dragX - mouseX;
		dy = dragY - mouseY;
		originX += dx;
		originY += dy;
		mouseX = dragX;
		mouseY = dragY;
	}
	
	public int getOriginX() {
		return -originX;
	}
	
	public int getOriginY() {
		return -originY;
	}
	
	public PanNode getSelectedNode1() {
		return selectedNode1;
	}
	
	public PanNode getSelectedNode2() {
		return selectedNode2;
	}
	
	public void deselectNodes() {
		selectedNode1 = null;
		selectedNode2 = null;
	}
	
	public abstract void paint(Graphics g);
	
	public abstract void setOrigin(int oX, int oY);
}
