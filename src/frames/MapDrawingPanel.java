package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

import textures.MapNode;
import textures.PanNode;

@SuppressWarnings("serial")
public class MapDrawingPanel extends JComponent {
	private Color panelColor = new Color(128,128,128);
	
	private int originX = 0;
	private int originY = 0;
	private int x;
	private int y;
	
	private PanNode selectedNode1;
	private PanNode selectedNode2;
	
	private boolean dragAllowed = true;
	
	public MapDrawingPanel() {
		// Listeners
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent press) {
				// getting mouse click location
				x = press.getX();
				y = press.getY();
				
				if(press.isControlDown()) {
					if(selectedNode1 == null) {
						selectedNode1 = getSelectedNode(x,y);
					}
					else if(selectedNode2 == null) {
						selectedNode2 = getSelectedNode(x,y);
						if(selectedNode1.equals(selectedNode2))
							selectedNode2 = null;
					}
				}
				else {
					selectedNode1 = null;
					selectedNode2 = null;
					selectedNode1 = getSelectedNode(x,y);
				}
			}
			
			public void mouseReleased(MouseEvent released) {
				dragAllowed = true;
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent drag) {
				// cannot drag if control is down
				if(drag.isControlDown())
					dragAllowed = false;
				
				if(dragAllowed) {
					if(selectedNode1 != null) {
						MapNode node = selectedNode1.getMapNode();
						node.dragNode(drag.getX(), drag.getY());
					}
					else {
						dragPanel(drag.getX(), drag.getY());
					}
				}
			}			
		});
	}
	
	private PanNode getSelectedNode(int x,int y) {
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
	
	private boolean isNodeSelected(MapNode node) {
		if(selectedNode1 != null)
			if(selectedNode1.getMapNode() == node)
				return true;
		if(selectedNode2 != null)
			if(selectedNode2.getMapNode() == node)
				return true;
		return false;
	}
	
	private void dragPanel(int dragX, int dragY) {
		int dx, dy;
		dx = dragX - x;
		dy = dragY - y;
		originX += dx;
		originY += dy;
		x = dragX;
		y = dragY;
	}
	
	public void setOrigin(int oX, int oY) {
		originX = -oX;
		originY = -oY;
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
	
	public void paint(Graphics g) {
		Graphics2D graphicSettings = (Graphics2D)g;
		graphicSettings.setColor(panelColor);
		graphicSettings.fillRect(0, 0, getWidth(), getHeight());
		
		graphicSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Translates the origin of the Graphics2D context to the point (x, y) in the current coordinate system.
		graphicSettings.translate(originX, originY);
		// Drawing starts from the head (root), and draw number zero is assigned to it.
		int drawNum = 0;
		PanNode start = PanNode.getHead();
		while(start != null) {
			start.setDrawNum(drawNum++);
			MapNode node = start.getMapNode();
			node.drawNode(graphicSettings, start, isNodeSelected(node));
			start = start.getNext();
		}
	}
}
