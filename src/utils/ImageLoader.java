package utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gui.GuiSprites;
import sun.awt.image.codec.JPEGImageDecoderImpl;

public class ImageLoader {
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private static BufferedImage image;
	private static boolean isLoading = false;
	private static boolean isLoaded = false;
	private static boolean isCanceled = false;
	
	private static final Object lock = new Object();
	
	public static void loadImage(String path) {
		// Set loader
		isLoading = true;
		isLoaded = false;
		
		// Show loading sprite
		GuiSprites.showLoadingSprite(true);
		
		// Execute loading
		executor.execute(new Runnable() {
			public void run() {
				try {
					while(IconLoader.getInstance().isLoading()) {
						synchronized(lock) {
							lock.wait();
						}
					}
					image = loadBufferedImage(path);
					isLoaded = true;
				} catch (OutOfMemoryError | Exception e) {
					e.printStackTrace();
					if(image != null) {
						image.flush();
						image = null;
						System.gc();
					}
					isCanceled = true;
				}
			}		
		});
	}
	
	public static void allowLoading() {
		synchronized(lock) {
			lock.notify();
		}
	}
	
	private static BufferedImage loadBufferedImage(String path) throws Exception {
		if(ImageCipher.isEncrypted(path))
			return new JPEGImageDecoderImpl(new ByteArrayInputStream(ImageCipher.imageDecrypt(path))).decodeAsBufferedImage();
		return new JPEGImageDecoderImpl(new FileInputStream(path)).decodeAsBufferedImage();
	}
	
	public static int[] getImageData() {
		return image.getRGB(0, 0, image.getWidth()/2, image.getHeight(), null, 0, image.getWidth()/2);
	}
	
	public static int[] getImageData2() {
		return image.getRGB(image.getWidth()/2, 0, image.getWidth()/2, image.getHeight(), null, 0, image.getWidth()/2);
	}
	
	public static BufferedImage getImage() {
		return image;
	}
	
	public static void clearImage() {
		image = null;
		System.gc();
	}
	
	public static boolean isLoading() {
		return isLoading;
	}
	
	public static boolean isLoaded() {
		return isLoaded;
	}
	
	public static boolean isCanceled() {
		// Show cancel sprite if loading is canceled
		if(isCanceled)
			GuiSprites.showCancelSprite(true);
		
		// Return cancel flag
		return isCanceled;
	}
	
	public static void resetLoader() {
		// Reset loader
		image = null;
		isLoading = false;
		isLoaded = false;
		isCanceled = false;
		
		// Hide loading sprite
		GuiSprites.showLoadingSprite(false);
	}
}
