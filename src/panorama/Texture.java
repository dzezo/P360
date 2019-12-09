package panorama;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import loaders.ImageLoader;
import utils.BuffUtils;

public class Texture {
	protected int textureID;
	protected int textureID2; // drugi deo
	protected int width, height;
	
	public Texture() {
		width = ImageLoader.getInstance().getImage().getWidth();
		height = ImageLoader.getInstance().getImage().getHeight();
		
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width / 2, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, 
				BuffUtils.storeInIntBuffer(ImageLoader.getInstance().getImageData(0, 0, 2, 1)));
		
		glBindTexture(GL_TEXTURE_2D, 0);
	
		// Drugi deo texture
		
		textureID2 = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID2);
		
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width / 2, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, 
				BuffUtils.storeInIntBuffer(ImageLoader.getInstance().getImageData(1, 0, 2, 1)));
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		// Ocisti ucitanu sliku
		
		ImageLoader.getInstance().clearImage();
	}

	public int getTextureID() {
		return textureID;
	}
	
	// drugi deo
	public int getTextureID2() {
		return textureID2;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
