package frames;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class MapFrame extends Frame {
	public static int mapWidth = 800;
	public static int mapHeight = 600;
	
	protected boolean updated = false;
	
	protected MapPanel mapPanel;
	
	public MapFrame(String title) {
		super(title);
	}
	
	protected void createFrame() {
		setSize(mapWidth, mapHeight);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		
		// add map panel to frame
		mapPanel.setParent(this);
		add(mapPanel, BorderLayout.CENTER);
		
		// repaint frame every 20milis
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		executor.scheduleAtFixedRate(new RepaintMap(this), 0, 20, TimeUnit.MILLISECONDS);
		
		setVisible(false);
		
		// Listeners
		addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
                setVisible(false);
            }
        });
	}
	
	public MapPanel getMapPanel() {
		return this.mapPanel;
	}
	
	public boolean isUpdated() {
		if (updated) {
			updated = false;
			return true;
		}
		else
			return false;
	}
	
	protected abstract void setOrigin();
}
