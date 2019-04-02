package frames;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MapViewFrame extends MapFrame {

	public MapViewFrame(String title) {
		super(title);
		createFrame();
	}

	protected void createFrame() {
		setSize(mapWidth, mapHeight);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		
		mapPanel = new MapViewPanel();
		add(mapPanel, BorderLayout.CENTER);
		
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

	public void showFrame(String title) {
		setVisible(true);
		setTitle(title);
		
		// setting the origin of a map
		setOrigin();
	}

}
