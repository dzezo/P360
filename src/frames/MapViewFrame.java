package frames;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import panorama.PanGraph;
import panorama.PanMap;

@SuppressWarnings("serial")
public class MapViewFrame extends MapFrame {

	public MapViewFrame(String title) {
		super(title);
		// instantiate map panel
		mapPanel = new MapViewPanel();
		
		// create frame
		createFrame();
	}
	
	private void createFrame() {
		setSize(mapWidth, mapHeight);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
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

	public void showFrame() {
		// show frame
		setVisible(true);
		setTitle(PanGraph.getName());
		
		// setting the origin of a map
		setOrigin();
	}

	protected void setOrigin() {
		if(PanGraph.getHome() != null) {
			// map center
			int x = PanGraph.getCenterX();
			int y = PanGraph.getCenterY();
			
			// node size
			int h = PanMap.HEIGHT / 2;
			int w = PanMap.WIDTH / 2;
			
			// panel size
			int centerX = mapPanel.getWidth() / 2;
			int centerY = mapPanel.getHeight() / 2;
			
			mapPanel.setOrigin((x + w) - centerX, (y + h) - centerY);
		}
		else {
			mapPanel.setOrigin(0,0);
		}
	}
	
}
