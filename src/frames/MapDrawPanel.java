package frames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingUtilities;

import panorama.PanGraph;
import panorama.PanMap;
import panorama.PanNode;

@SuppressWarnings("serial")
public class MapDrawPanel extends MapPanel {
	private static int gridSize = 10;
	
	public MapDrawPanel() {
		// Listeners
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent press) {
				// getting mouse click location
				mouseClick.setLocation(press.getX(), press.getY());
				
				// control + click
				if(press.isControlDown()) {
					if(selectedNode1 == null) {
						selectedNode1 = getSelectedNode();
					}
					else if(selectedNode2 == null) {
						selectedNode2 = getSelectedNode();
						if(selectedNode1.equals(selectedNode2))
							selectedNode2 = null;
					}
				}
				// right click
				else if(press.getButton() == MouseEvent.BUTTON3) {
					// read right click location
					PanNode prevSelected = selectedNode2;
					selectedNode2 = getSelectedNode();
					
					// if left and right click where on the same node, reset right click selection
					if(selectedNode1 != null && selectedNode1.equals(selectedNode2))
						selectedNode2 = null;
					
					// if second right click was on the same node as first one, connect/disconnect nodes
					if(doubleRightClickEvent(prevSelected))
						if(selectedNode1.isConnectedTo(selectedNode2))
							PanGraph.disconnectNode(selectedNode1, selectedNode2);
						else
							PanGraph.connectNodes(selectedNode1, selectedNode2);
				}
				// left click
				else if(press.getButton() == MouseEvent.BUTTON1) {
					selectedNode1 = null;
					selectedNode2 = null;
					selectedNode1 = getSelectedNode();
					
					// Panel click
					if(selectedNode1 == null) {
						setPanelDragAllowed(true);
					}
				}
			}
			
			public void mouseReleased(MouseEvent released) {
				// center after drag
				origin.setLocation(centerOnGrid(origin.x), centerOnGrid(origin.y));
				if(selectedNode1 != null) {
					int newX = selectedNode1.getMapNode().x;
					int newY = selectedNode1.getMapNode().y;
					newX = centerOnGrid(newX);
					newY = centerOnGrid(newY);
					selectedNode1.getMapNode().setNewLocation(newX, newY);
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent drag) {
				// cannot drag if control is down
				if(drag.isControlDown())
					setPanelDragAllowed(false);
				else
					setPanelDragAllowed(true);
				
				// draging is only possible with left click
				if(isPanelDragAllowed() && SwingUtilities.isLeftMouseButton(drag)) {
					if(selectedNode1 != null) {
						PanMap node = selectedNode1.getMapNode();
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
		
		int x = -origin.x;
		int y = -origin.y;
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
		graphicSettings.translate(origin.x, origin.y);
		
		// Calculating drawing panel dimensions
		panelRect.setBounds(-origin.x, -origin.y, this.getWidth(), this.getHeight());
		
		// Drawing grid
		drawGrid(graphicSettings);
		
		// Drawing starts from the head (root), and id zero is assigned to it.
		int id = 0;
		PanNode start = PanGraph.getHead();
		while(start != null) {
			start.setID(id++);
			PanMap node = start.getMapNode();
			node.drawNodeOnEditor(graphicSettings, panelRect, isNodeSelected(node));
			start = start.getNext();
		}
	}
	
	public void setOrigin(int oX, int oY) {	
		origin.setLocation(centerOnGrid(-oX), centerOnGrid(-oY));
	}

	/**
	 * Funkcija koja proverava da li se desni klik desio dva puta uzastopno na istom cvoru
	 * @param prevSelected - prethodni desni klik
	 * @return true - desni klik je registrovan dva puta na istom cvoru
	 */
	private boolean doubleRightClickEvent(PanNode prevSelected) {
		if(selectedNode1 == null 
				|| prevSelected == null 
				|| !prevSelected.equals(selectedNode2)) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
}
