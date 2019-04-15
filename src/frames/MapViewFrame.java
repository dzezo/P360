package frames;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

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
	
	protected void createFrame() {
		setSize(mapWidth, mapHeight);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		
		// add map panel to frame
		mapPanel.setParent(this);
		add(mapPanel, BorderLayout.CENTER);
		
		setVisible(false);
		
		// Listeners	
		addWindowListener(new WindowAdapter() 
		{
			public void windowActivated(WindowEvent we) {
				// repaint frame every 20milis
				startFrameRepaint();
			}
			
            public void windowClosing(WindowEvent we){
            	// stop frame repaint
                stopFrameRepaint();
                
            	// hide frame
                setVisible(false);
            }
        });
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
