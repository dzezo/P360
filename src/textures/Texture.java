package textures;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import utils.BuffUtils;

public class Texture {
	protected int textureID;
	protected int width, height;
	
	public Texture(String path) {
		textureID = load(path);
	}
	
	private int load(String path) {
		int[] pixels = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(path));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width*height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] data = new int[width*height];
		int a,r,g,b;
		for(int i = 0; i < width*height; i++) {
			a = (pixels[i] & 0xFF000000) >> 24;
			r = (pixels[i] & 0xFF0000) >> 16;
			g = (pixels[i] & 0xFF00) >> 8;
			b = (pixels[i] & 0xFF);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		int tex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, BuffUtils.storeInIntBuffer(data));
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return tex;
	}

	public int getTextureID() {
		return textureID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
