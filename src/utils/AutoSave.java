package utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import panorama.PanNode;

public class AutoSave implements Runnable {
	private static final String DEFAULT_FILE_PATH = "AutoSave.p360";
	
	private static ScheduledThreadPoolExecutor autoSave = new ScheduledThreadPoolExecutor(1);
	private static ScheduledFuture<?> autoSaveTasks;
	
	private static String savingPath = new String();
	
	public void run() {
		save();
	}
	
	public static void setSavingPath(String path) {
		savingPath = new String(path);
	}
	
	public static void resetSavingPath() {
		savingPath = new String();
	}
	
	public static void save() {
		// nothing to save
		if(PanNode.getHead() == null) return;
		
		// get save path
		String savePath = (!savingPath.isEmpty()) ? savingPath : DEFAULT_FILE_PATH;
		
		// save map
		PanNode.saveMap(savePath);
	}
	
	public static void startSaving() {
		if(autoSaveTasks == null)
			autoSaveTasks = autoSave.scheduleAtFixedRate(new AutoSave(), 1, 1, TimeUnit.MINUTES);
	}
	
	public static void stopSaving() {
		autoSaveTasks.cancel(false);
		autoSaveTasks = null;
	}
}
