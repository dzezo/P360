package frames;

import panorama.PanNode;

@SuppressWarnings("serial")
public abstract class MapFrame extends Frame {
	public static int mapWidth = 800;
	public static int mapHeight = 600;
	
	protected MapPanel mapPanel;
	
	public MapFrame(String title) {
		super(title);
	}
	
	public MapPanel getMapPanel() {
		return this.mapPanel;
	}
	
	protected void setOrigin() {
		if(PanNode.getHome() != null) {
			int x = PanNode.getHome().getMapNode().x;
			int y = PanNode.getHome().getMapNode().y;
			
			int h = (int) (PanNode.getHome().getMapNode().getHeight() / 2);
			int w = (int) (PanNode.getHome().getMapNode().getWidth() / 2);
			
			int centerX = mapPanel.getWidth() / 2;
			int centerY = mapPanel.getHeight() / 2;
			
			mapPanel.setOrigin(x - centerX + w, y - centerY + h);
		}
		else {
			mapPanel.setOrigin(0,0);
		}
	}
	
}
