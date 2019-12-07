package utils;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ImageIcon;

import panorama.PanMap;
import panorama.PanMapIcon;

public class IconLoader extends Thread {
	private static volatile IconLoader instance = null;
	
	private Queue<PanMapIcon> loadingQueue = new LinkedList<>();
	
	private Object lock = new Object();
	
	private boolean doStop = false;
	private boolean pause = false;
	
	private IconLoader() {
		this.setName("PanMapIcon loader");
		this.start();
	}
	
	public static synchronized IconLoader getInstance() {
		if(instance == null)
			instance = new IconLoader();
		return instance;
	}
	
	public void doStop() {
		synchronized(lock) {
			doStop = true;
			lock.notify();
		}
	}
	
	/**
	 * Podesava pause flag
	 * @param pause
	 * <br> true - Zavrsava ucitavanje i pauzira sledeca ucitavanja.
	 * <br> false - Budi nit ukoliko spava i postavlja flag da je ucitavanje dozvoljeno.
	 */
	public void pauseLoading(boolean pause) {
		if(pause) {
			this.pause = true;
		}
		else {
			synchronized(lock) {
				this.pause = false;
				lock.notify();
			}
		}
	}
	
	private boolean keepRunning() {
		return doStop == false;
	}
	
	private boolean keepLoading() {
		return pause == false;
	}
	
	public void run() {
		while(keepRunning()) {
			synchronized(lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
			while(!loadingQueue.isEmpty() && keepLoading() && keepRunning()) {
				try {
					// Deque PanMapIcon
					PanMapIcon icon = loadingQueue.remove();
					String iconPath = icon.getParent().getParent().getPanoramaPath();
					
					// Load icon
					Image image;
					// image is .pimg
					if(ImageCipher.isEncrypted(iconPath)) {
						image = new ImageIcon(ImageCipher.imageDecrypt(iconPath)).getImage();
					}
					// image is not encrypted
					else {
						image = new ImageIcon(iconPath).getImage();
					}
					
					// Create icon from image
			        if (image != null) {
			        	icon.setIcon(new ImageIcon(image.getScaledInstance(PanMap.WIDTH, PanMap.HEIGHT, Image.SCALE_DEFAULT)));
			        	
			        	// free memory
			        	image.flush();
			           	image = null;
			            
			            // load completed
			            icon.setLoadedFlag();
			        }
			        
			        System.gc();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void add(PanMapIcon icon) {
		synchronized(lock) {
			loadingQueue.add(icon);
			lock.notify();
		}
	}
	
}