package textures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

@SuppressWarnings("serial")
public class MapNode extends Rectangle {
	private static Color lineColor = new Color(0,0,0);
	private static Color normalColor = new Color(255,255,255);
	private static Color selectedColor = new Color(0,191,255);
	private static Color homeColor = new Color(0,255,0);
	private static Color fillColor = new Color(64,64,64);
	private Color mainColor;
	
	private static int width = 150;
	private static int height = 75;
	private int prevX;
	private int prevY;
	
	private String name;
	private static Font nameFont = new Font("Arial", Font.BOLD, 15);
	
	private Point portLeft = new Point();
	private Point portRight = new Point();
	private Point portTop = new Point();
	private Point portBot = new Point();
	
	private boolean selected = false;
	private boolean home = false;
	
	public MapNode(String panName, int x, int y) {
		super(x, y, width, height);
		calculatePorts(x,y);
		name = panName;
	}
	
	public static int getW() {
		return width;
	}
	
	public static int getH() {
		return height;
	}
	
	public boolean isPressed(int x, int y, int oX, int oY) {
		if(this.contains(x - oX, y - oY)) {
			selected = true;
			prevX = x;
			prevY = y;
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
			dx = dragX - prevX;
			dy = dragY - prevY;
			newX = this.x + dx;
			newY = this.y + dy;
			this.setLocation(newX, newY);
			calculatePorts(newX, newY);
			prevX = dragX;
			prevY = dragY;
		}
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setHome(boolean b) {
		this.home = b;
	}
	
	// Drawing
	
	public void drawNode(Graphics2D g, PanNode infoNode, boolean selected) {
		drawConnections(g, infoNode);
		drawShape(g, selected);
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
	
	private void drawShape(Graphics2D g, boolean selected) {
		if(selected)
			mainColor = selectedColor;
		else if(home)
			mainColor = homeColor;
		else
			mainColor = normalColor;
		// Drawing rectangle
		g.setColor(fillColor);
		g.fill(this);
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(lineColor);
		g.draw(this);
		// Drawing text
		g.setFont(nameFont);
		g.setColor(mainColor);
		// Cuting name string if it is too big
		while(g.getFontMetrics().stringWidth(name) > width-10) {
			name = name.substring(0, name.length() - 1);
		}
		// Calculating text starting position
		int x = (int)this.getCenterX() - g.getFontMetrics().stringWidth(name) / 2;
		int y = (int)this.getCenterY() + g.getFontMetrics().getHeight() / 4;
		g.drawString(name, x, y);
	}
	
}
