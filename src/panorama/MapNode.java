package panorama;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

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
	
	/* port position */
	private Point portLeft = new Point();
	private Point portRight = new Point();
	private Point portTop = new Point();
	private Point portBot = new Point();
	
	/* arrow positon */
	private Point leftArrow = new Point();
	private Point rightArrow = new Point();
	private Point topArrow = new Point();
	private Point botArrow = new Point();
	
	/* arrow flag */
	private boolean b_leftArrow = false;
	private boolean b_rightArrow = false;
	private boolean b_topArrow = false;
	private boolean b_bottomArrow = false;
	
	private boolean selected = false;
	
	public MapNode(PanNode parent, int x, int y) {
		super(x, y, width, height);
		this.parent = parent;
		
		calculatePorts(x,y);
		
		panName = setNameFromPath(parent.getPanoramaPath());
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
		// calculating port location
		portLeft.x = x;
		portLeft.y = y + height/2;
		
		portRight.x = x + width;
		portRight.y = y + height/2;
		
		portTop.x = x + width/2;
		portTop.y = y;
		
		portBot.x = x + width/2;
		portBot.y = y + height;
		
		// assigning arrow location
		leftArrow.setLocation(portLeft.x, portLeft.y);
		rightArrow.setLocation(portRight.x, portRight.y);
		topArrow.setLocation(portTop.x, portTop.y);
		botArrow.setLocation(portBot.x, portBot.y);
		
		// offseting port location if arrow is present
		if(b_leftArrow)
			portLeft.x -= MapDrawPanel.getGridSize();
		if(b_rightArrow)
			portRight.x += MapDrawPanel.getGridSize();
		if(b_topArrow)
			portTop.y -= MapDrawPanel.getGridSize();
		if(b_bottomArrow)
			portBot.y += MapDrawPanel.getGridSize();
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

	/* Arrows */
	public void setArrow(PanNode neighbour, boolean set) {
		if(parent.getTop() != null 
				&& parent.getTop().equals(neighbour)) 
		{
			setTopArrow(set);
		}
		else if(parent.getLeft() != null 
				&& parent.getLeft().equals(neighbour)) 
		{
			setLeftArrow(set);
		}
		else if(parent.getBot() != null 
				&& parent.getBot().equals(neighbour)) 
		{
			setBotArrow(set);
		}
		else if(parent.getRight() != null)
		{
			setRightArrow(set);
		}
	}
	
	public void clearArrows() {
		setTopArrow(false);
		setRightArrow(false);
		setBotArrow(false);
		setLeftArrow(false);
	}
	
	private void setLeftArrow(boolean b) {
		b_leftArrow = b;
		calculatePorts(this.x, this.y);
	}
	
	private void setRightArrow(boolean b) {
		b_rightArrow = b;
		calculatePorts(this.x, this.y);
	}
	
	private void setTopArrow(boolean b) {
		b_topArrow = b;
		calculatePorts(this.x, this.y);
	}
	
	private void setBotArrow(boolean b) {
		b_bottomArrow = b;
		calculatePorts(this.x, this.y);
	}
	
	/* Drawing */
	
	/**
	 * Funkcija koja iscrtava cvor na mapi na kojoj vise cvorova mogu biti selektovana
	 * @param selected - daje informaciju koji cvor je selektovan na mapi
	 */
	public void drawNode(Graphics2D g, boolean selected) {
		drawArrow(g);	
		drawConnections(g);
		
		drawShape(g);
		
		// draw text
		textColor = normalTextColor;
		if(selected)
			textColor = selectedTextColor;
		else if(parent.isHome())
			textColor = homeTextColor;
		drawText(g);
	}
	
	/**
	 * Funkcija koja iscrtava cvor na mini mapi
	 */
	public void drawNode(Graphics2D g) {
		drawArrow(g);
		drawConnections(g);
		
		drawShape(g);
		
		// draw text
		textColor = normalTextColor;
		if(this.selected)
			textColor = selectedTextColor;
		else if(parent.isActive())
			textColor = activeTextColor;
		drawText(g);
	}
	
	private void drawConnections(Graphics2D g) {
		MapNode mNode;
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(lineColor);
		// Check there are connections
		// If there are connections check if they've been drawn.
		if(parent.getLeft() != null && parent.getLeft().getDrawNum() > parent.getDrawNum()) {
			mNode = parent.getLeft().getMapNode();
			g.drawLine(portLeft.x, portLeft.y, mNode.getPortRight().x, mNode.getPortRight().y);
		}
		if(parent.getRight() != null && parent.getRight().getDrawNum() > parent.getDrawNum()) {
			mNode = parent.getRight().getMapNode();
			g.drawLine(portRight.x, portRight.y, mNode.getPortLeft().x, mNode.getPortLeft().y);
		}
		if(parent.getTop() != null && parent.getTop().getDrawNum() > parent.getDrawNum()) {
			mNode = parent.getTop().getMapNode();
			g.drawLine(portTop.x, portTop.y, mNode.getPortBot().x, mNode.getPortBot().y);
		}
		if(parent.getBot() != null && parent.getBot().getDrawNum() > parent.getDrawNum()) {
			mNode = parent.getBot().getMapNode();
			g.drawLine(portBot.x, portBot.y, mNode.getPortTop().x, mNode.getPortTop().y);
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
		
		// draw tourNum text
		String tour = "";
		Iterator<Integer> iterator = parent.tourNum.iterator();
		while(iterator.hasNext()) {
			String tourNum = String.valueOf(iterator.next());
			
			if(!iterator.hasNext()) {
				tour = tour.concat(String.valueOf(tourNum));
			}
			else {
				tour = tour.concat(String.valueOf(tourNum) + ", ");
			}
		}
		
		g.drawString(tour, this.x + 5, this.y + 15);
		
		// cut name text if needed
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
			int y = (int)this.getCenterY();
			g.drawString(panName, x, y);
			
			// cut audio text if needed
			while(g.getFontMetrics().stringWidth(audioName) > width-10) {
				audioName = audioName.substring(0, audioName.length() - 1);
			}
			
			g.setFont(audioNameFont);
			
			x = (int)this.getCenterX() - g.getFontMetrics().stringWidth(audioName) / 2;
			y = (int)this.getCenterY() + 25;
			g.drawString(audioName, x, y);
		}
	}
	
	private void drawArrow(Graphics2D g) {
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(lineColor);
		if(b_leftArrow && parent.getLeft() != null) {
			g.drawLine(leftArrow.x, leftArrow.y, leftArrow.x - 6, leftArrow.y + 3);
			g.drawLine(leftArrow.x, leftArrow.y, leftArrow.x - MapDrawPanel.getGridSize(), leftArrow.y);
			g.drawLine(leftArrow.x, leftArrow.y, leftArrow.x - 6, leftArrow.y - 3);
		}
		if(b_rightArrow && parent.getRight() != null) {
			g.drawLine(rightArrow.x, rightArrow.y, rightArrow.x + 6, rightArrow.y + 3);
			g.drawLine(rightArrow.x, rightArrow.y, rightArrow.x + MapDrawPanel.getGridSize(), rightArrow.y);
			g.drawLine(rightArrow.x, rightArrow.y, rightArrow.x + 6, rightArrow.y - 3);
		}
		if(b_topArrow && parent.getTop() != null) {
			g.drawLine(topArrow.x, topArrow.y, topArrow.x + 3, topArrow.y - 6);
			g.drawLine(topArrow.x, topArrow.y, topArrow.x, topArrow.y - MapDrawPanel.getGridSize());
			g.drawLine(topArrow.x, topArrow.y, topArrow.x -3, topArrow.y - 6);
		}
		if(b_bottomArrow && parent.getBot() != null) {
			g.drawLine(botArrow.x, botArrow.y, botArrow.x + 3, botArrow.y + 6);
			g.drawLine(botArrow.x, botArrow.y, botArrow.x, botArrow.y + MapDrawPanel.getGridSize());
			g.drawLine(botArrow.x, botArrow.y, botArrow.x - 3, botArrow.y + 6);
		}
	}
	
}
