package panorama;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import frames.MapDrawPanel;

@SuppressWarnings("serial")
public class MapNode extends Rectangle {
	private PanNode parent;
	
	private Color textColor;
	private static final Color lineColor = new Color(0,0,0);
	private static final Color normalTextColor = new Color(255,255,255);
	private static final Color selectedTextColor = new Color(0,191,255);
	private static final Color homeTextColor = new Color(0,255,0);
	private static final Color activeTextColor = new Color(255,255,0);
	private static final Color fillColor = new Color(64,64,64);
	
	public static int width = MapDrawPanel.getGridSize() * 16;
	public static int height = MapDrawPanel.getGridSize() * 8;
	
	private int pressX;
	private int pressY;
	
	protected String panName;
	protected String audioName;
	private static Font panNameFont = new Font("Arial", Font.BOLD, 15);
	private static Font audioNameFont = new Font("Arial", Font.PLAIN, 15);
	
	private Point portLeft = new Point();
	private Point portRight = new Point();
	private Point portTop = new Point();
	private Point portBot = new Point();
	
	private boolean selected = false;
	
	public MapNode(PanNode parent, int x, int y) {
		super(x, y, width, height);
		this.parent = parent;
		calculatePorts(x,y);
		panName = setNameFromPath(parent.getPanoramaPath());
		System.out.println(panName);
	}
	
	public String setNameFromPath(String path) {
		String separator = System.getProperty("file.separator");
		int lastSeparatorIndex = path.lastIndexOf(separator);
		
		return path.substring(lastSeparatorIndex + 1);
	}
	
	public boolean isPressed(int x, int y, int oX, int oY) {
		if(this.contains(x - oX, y - oY)) {
			selected = true;
			pressX = x;
			pressY = y;
		}
		else {
			selected = false;
		}
		return selected;
	}
	
	public void dragNode(int dragX, int dragY) {
		int dx, dy;
		int newX, newY;
		if(selected) {
			dx = dragX - pressX;
			dy = dragY - pressY;
			newX = this.x + dx;
			newY = this.y + dy;
			
			setNewLocation(newX, newY);
			
			pressX = dragX;
			pressY = dragY;
		}
	}
	
	public void setNewLocation(int newX, int newY) {
		this.setLocation(newX, newY);
		calculatePorts(newX, newY);
	}
	
	private void calculatePorts(int x, int y) {
		portLeft.x = x;
		portLeft.y = y + height/2;
		
		portRight.x = x + width;
		portRight.y = y + height/2;
		
		portTop.x = x + width/2;
		portTop.y = y;
		
		portBot.x = x + width/2;
		portBot.y = y + height;
	}
	
	// Getters and Setters
	
	public Point getPortLeft() {
		return portLeft;
	}
	
	public Point getPortRight() {
		return portRight;
	}
	
	public Point getPortTop() {
		return portTop;
	}
	
	public Point getPortBot() {
		return portBot;
	}
	
	/* Drawing */
	
	/**
	 * Funkcija koja iscrtava cvor na mapi na kojoj vise cvorova mogu biti selektovana
	 * @param infoNode - info deo cvora koga iscrtavamo
	 * @param selected - daje informaciju koji cvor je selektovan na mapi
	 */
	public void drawNode(Graphics2D g, PanNode infoNode, boolean selected) {
		drawConnections(g, infoNode);
		drawShape(g);
		
		// draw text
		if(selected)
			textColor = selectedTextColor;
		else if(parent.isHome())
			textColor = homeTextColor;
		else
			textColor = normalTextColor;
		drawText(g);
	}
	
	/**
	 * Funkcija koja iscrtava cvor na mini mapi
	 */
	public void drawNode(Graphics2D g, PanNode infoNode) {
		drawConnections(g, infoNode);
		drawShape(g);
		
		// draw text
		if(this.selected)
			textColor = selectedTextColor;
		else if(parent.isActive())
			textColor = activeTextColor;
		else
			textColor = normalTextColor;
		drawText(g);
	}
	
	private void drawConnections(Graphics2D g, PanNode infoNode) {
		MapNode mNode;
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(lineColor);
		// Check there are connections
		// If there are connections check if they've been drawn.
		if(infoNode.getLeft() != null) {
			if(infoNode.getLeft().getDrawNum() > infoNode.getDrawNum()) {
				mNode = infoNode.getLeft().getMapNode();
				g.drawLine(portLeft.x, portLeft.y, mNode.getPortRight().x, mNode.getPortRight().y);
			}
		}
		if(infoNode.getRight() != null) {
			if(infoNode.getRight().getDrawNum() > infoNode.getDrawNum()) {
				mNode = infoNode.getRight().getMapNode();
				g.drawLine(portRight.x, portRight.y, mNode.getPortLeft().x, mNode.getPortLeft().y);
			}
		}
		if(infoNode.getTop() != null) {
			if(infoNode.getTop().getDrawNum() > infoNode.getDrawNum()) {
				mNode = infoNode.getTop().getMapNode();
				g.drawLine(portTop.x, portTop.y, mNode.getPortBot().x, mNode.getPortBot().y);
			}
		}
		if(infoNode.getBot() != null) {
			if(infoNode.getBot().getDrawNum() > infoNode.getDrawNum()) {
				mNode = infoNode.getBot().getMapNode();
				g.drawLine(portBot.x, portBot.y, mNode.getPortTop().x, mNode.getPortTop().y);
			}
		}
	}
	
	private void drawShape(Graphics2D g) {
		g.setColor(fillColor);
		g.fill(this);
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(lineColor);
		g.draw(this);
	}
	
	private void drawText(Graphics2D g) {
		g.setFont(panNameFont);
		g.setColor(textColor);
		
		// cut audio text if needed
		while(g.getFontMetrics().stringWidth(panName) > width-10) {
			panName = panName.substring(0, panName.length() - 1);
		}
		
		if(audioName == null) {
			int x = (int)this.getCenterX() - g.getFontMetrics().stringWidth(panName) / 2;
			int y = (int)this.getCenterY() + g.getFontMetrics().getHeight() / 4;
			g.drawString(panName, x, y);
		}
		else {			
			int x = (int)this.getCenterX() - g.getFontMetrics().stringWidth(panName) / 2;
			int y = (int)this.getCenterY() - g.getFontMetrics().getHeight() / 2;
			g.drawString(panName, x, y);
			
			// cut audio text if needed
			while(g.getFontMetrics().stringWidth(audioName) > width-10) {
				audioName = audioName.substring(0, audioName.length() - 1);
			}
			
			g.setFont(audioNameFont);
			
			x = (int)this.getCenterX() - g.getFontMetrics().stringWidth(audioName) / 2;
			y = (int)this.getCenterY() + g.getFontMetrics().getHeight();
			g.drawString(audioName, x, y);
		}
	}
	
}
