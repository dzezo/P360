package frames;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public abstract class MapFrame extends Frame {
	protected ScheduledThreadPoolExecutor repaint = new ScheduledThreadPoolExecutor(5);;
	protected ScheduledFuture<?> repaintTasks;
	
	public static int mapWidth = 800;
	public static int mapHeight = 600;
	
	protected boolean updated = false;
	
	protected MapPanel mapPanel;
	
	public MapFrame(String title) {
		super(title);
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
	
	protected void startFrameRepaint() {
		if(repaintTasks == null) {
			repaintTasks = repaint.scheduleAtFixedRate(new RepaintMap(this), 0, 20, TimeUnit.MILLISECONDS);
		}
	}
	
	protected void stopFrameRepaint() {
		repaintTasks.cancel(false);
		repaintTasks = null;
	}
	
	protected abstract void setOrigin();
}
