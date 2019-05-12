package utils;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import gui.GuiRenderer;
import gui.GuiSprites;

public class ImageLoader {
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private static ImageData img;
	private static boolean isLoading = false;
	private static boolean isLoaded = false;
	private static boolean isCanceled = false;
	
	public static void loadImage(String path) {
		isLoading = true;
		isLoaded = false;
		
		GuiSprites.loading.show(GuiRenderer.getGuiList());
		
		executor.execute(new Runnable() {

			public void run() {
				BufferedImage image;
				int width, height;
				int[] pixels;
				
				try {
					image = ImageIO.read(new FileInputStream(path));
					width = image.getWidth();
					height = image.getHeight();
					pixels = new int[width*height];
					image.getRGB(0, 0, width, height, pixels, 0, width);
					
					img = new ImageData(pixels, width, height);
					
					isLoaded = true;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					isCanceled = true;
					
					try {
						GuiSprites.loading.hide(GuiRenderer.getGuiList());
						GuiSprites.cancel.show(GuiRenderer.getGuiList());
						
						Thread.sleep(3000);
						
						GuiSprites.cancel.hide(GuiRenderer.getGuiList());
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
				
				GuiSprites.loading.hide(GuiRenderer.getGuiList());
			}
			
		});
	}
	
	public static ImageData getImageData() {
		return img;
	}
	
	public static boolean isLoading() {
		return isLoading;
	}
	
	public static boolean isLoaded() {
		return isLoaded;
	}
	
	public static boolean isCanceled() {
		return isCanceled;
	}
	
	public static void resetLoader() {
		img = null;
		isLoading = false;
		isLoaded = false;
		isCanceled = false;
	}
}
