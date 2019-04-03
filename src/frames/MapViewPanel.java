package frames;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import glRenderer.Scene;
import panorama.MapNode;
import panorama.PanNode;

@SuppressWarnings("serial")
public class MapViewPanel extends MapPanel{
	
	public MapViewPanel() {
		// Listeners
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent press) {
				// getting mouse click location
				mouseX = press.getX();
				mouseY = press.getY();
				
				// check if node is clicked, it will be null if none
				PanNode node = getSelectedNode(mouseX, mouseY);
				
				// if node is clicked and first click is not taken
				if(node != null && selectedNode1 == null)
					selectedNode1 = node;
				// if node is clicked but first click is taken
				else if(node != null)
					selectedNode2 = node;
				// panel click
				else if(node == null)
					deselectNodes();
				
				// do we have first and second click
				if(selectedNode1 != null && selectedNode2 != null) {
					// first and second node click were on the same node
					if(selectedNode2 == selectedNode1)	
						setNewActivePanorama();
					// if they are not the same treat second click as first 
					else{
						selectedNode1 = selectedNode2;
						selectedNode2 = null;
					}
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent drag) {
				if(selectedNode1 == null)
					dragPanel(drag.getX(), drag.getY());
			}			
		});
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
	
	public void setOrigin(int oX, int oY) {
		originX = -oX;
		originY = -oY;
	}
	
	private void setNewActivePanorama() {
		// set request for change
		Scene.setActivePanorama(selectedNode1);
		this.parent.updated = true;
		
		// hide map frame
		this.parent.setVisible(false);
	}

}
