package frames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import panorama.MapNode;
import panorama.PanNode;

@SuppressWarnings("serial")
public class MapDrawPanel extends MapPanel {
	private static int gridSize = 10;
	
	private boolean dragAllowed = true;
	
	public MapDrawPanel() {
		// Listeners
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent press) {
				// getting mouse click location
				mouseX = press.getX();
				mouseY = press.getY();
				
				if(press.isControlDown()) {
					if(selectedNode1 == null) {
						selectedNode1 = getSelectedNode(mouseX,mouseY);
					}
					else if(selectedNode2 == null) {
						selectedNode2 = getSelectedNode(mouseX,mouseY);
						if(selectedNode1.equals(selectedNode2))
							selectedNode2 = null;
					}
				}
				else {
					selectedNode1 = null;
					selectedNode2 = null;
					selectedNode1 = getSelectedNode(mouseX,mouseY);
				}
			}
			
			public void mouseReleased(MouseEvent released) {
				// center after drag
				originX = centerOnGrid(originX);
				originY = centerOnGrid(originY);
				if(selectedNode1 != null) {
					int newX = selectedNode1.getMapNode().x;
					int newY = selectedNode1.getMapNode().y;
					newX = centerOnGrid(newX);
					newY = centerOnGrid(newY);
					selectedNode1.getMapNode().setNewLocation(newX, newY);
				}
				
				// allow drag
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
	
	private void drawGrid(Graphics2D g) {
		g.setStroke(new BasicStroke(0.2f));
		g.setColor(new Color(0,0,0));
		
		int x = -originX;
		int y = -originY;
		int endX = x + this.getWidth();
		int endY = y + this.getHeight();
		
		for(int i = 0; i < this.getHeight()/gridSize + 1; i++)
			g.drawLine(x, y + i*gridSize, endX, y + i*gridSize);
		for(int i = 0; i < this.getWidth()/gridSize + 1; i++)
			g.drawLine(x + i*gridSize, y, x + i*gridSize, endY);
	}
	
	private int centerOnGrid(int coord) {
        int q = coord / gridSize; 
        
        int n1 = gridSize * q; 
        int n2 = (coord * gridSize) > 0 ? (gridSize * (q + 1)) : (gridSize * (q - 1)); 
           
        if (Math.abs(coord - n1) < Math.abs(coord - n2)) 
            return n1; 
        else
        	return n2;  
	}
	
	public static int getGridSize() {
		return gridSize;
	}
	
	public void paint(Graphics g) {
		Graphics2D graphicSettings = (Graphics2D)g;
		graphicSettings.setColor(panelColor);
		graphicSettings.fillRect(0, 0, getWidth(), getHeight());
		
		graphicSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Translates the origin of the Graphics2D context to the point (x, y) in the current coordinate system.
		graphicSettings.translate(originX, originY);
		
		// Drawing grid
		drawGrid(graphicSettings);
		
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
	
	public void setOrigin(int oX, int oY) {
		originX = -oX;
		originY = -oY;
		
		originX = centerOnGrid(originX);
		originY = centerOnGrid(originY);
	}

}
