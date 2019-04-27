package panorama;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class ImageLoader {
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private static Image img;
	private static boolean isLoading = false;
	private static boolean isLoaded = false;
	
	public static void loadImage(String path) {
		isLoading = true;
		isLoaded = false;
		
		executor.execute(new Runnable() {

			public void run() {
				try {
					BufferedImage image = ImageIO.read(new FileInputStream(path));
					int width = image.getWidth();
					int height = image.getHeight();
					int[] pixels = new int[width*height];
					image.getRGB(0, 0, width, height, pixels, 0, width);
					
					img = new Image(pixels, width, height);
					isLoaded = true;
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
		});
	}
	
	public static Image getImage() {
		return img;
	}
	
	public static boolean isLoading() {
		return isLoading;
	}
	
	public static boolean isLoaded() {
		return isLoaded;
	}
	
	public static void resetLoader() {
		img = null;
		isLoading = false;
		isLoaded = false;
	}
}
