package frames;

import glRenderer.Scene;
import panorama.PanNode;

@SuppressWarnings("serial")
public class MapViewFrame extends MapFrame {

	public MapViewFrame(String title) {
		super(title);
		// instantiate map panel
		mapPanel = new MapViewPanel();
		
		// create frame
		createFrame();
	}

	public void showFrame(String title) {
		// show frame
		setVisible(true);
		setTitle(title);
		
		// setting the origin of a map
		setOrigin();
	}

	protected void setOrigin() {
		PanNode activePan = Scene.getActivePanorama();
		
		if(activePan != null) {
			int x = activePan.getMapNode().x;
			int y = activePan.getMapNode().y;
			
			int h = (int) (activePan.getMapNode().getHeight() / 2);
			int w = (int) (activePan.getMapNode().getWidth() / 2);
			
			int centerX = mapPanel.getWidth() / 2;
			int centerY = mapPanel.getHeight() / 2;
			
			mapPanel.setOrigin(x - centerX + w, y - centerY + h);
		}
		else {
			mapPanel.setOrigin(0,0);
		}
	}

}
