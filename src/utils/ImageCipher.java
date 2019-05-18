package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCipher {
	
	private static String createEncryptedImagePath(String path) {
		int extension = path.lastIndexOf(".");
		return path.substring(0, extension) + ".pimg";
	}
	
	public static boolean isEncrypted(String path) {
		String extension = path.substring(path.lastIndexOf("."), path.length());
		if(extension.equals(".pimg"))
			return true;
		else
			return false;
	}
	
	/**
	 * Vrsi enkripciju slike, tako sto XOR-uje svaki bajt slike sa jednim karakterom iz kljuca.
	 * @param imagePath - putanja do slike koju treba enkriptovati
	 * @param key - kljuc za enkripciju
	 * @return - putanja do enkriptovane slike, u slucaju IOException vraca null
	 */
	public static String imageEncrypt(String imagePath, String key) {
		if(key == null) return null;
		
		try {
			String encryptedImagePath = createEncryptedImagePath(imagePath);
			
			FileInputStream inStream = new FileInputStream(imagePath);
			FileOutputStream outStream = new FileOutputStream(encryptedImagePath);
			
			byte[] buffer = new byte[1024];
			byte[] outBuffer;
			
			int keyItr = 0;
			int byteNum;
			
			while((byteNum = inStream.read(buffer)) != -1) {
				outBuffer = new byte[byteNum];
				for(int i=0; i < byteNum; i++) {
					outBuffer[i] = (byte) (buffer[i] ^ key.charAt(keyItr));
					keyItr = (keyItr + 1) % key.length();
				}
				outStream.write(outBuffer);
			}
			
			outStream.flush();
			outStream.close();
			inStream.close();
			
			return encryptedImagePath;
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static byte[] imageDecrypt(String imagePath, String key) {
		try {
			FileInputStream inStream = new FileInputStream(imagePath);
			File image = new File(imagePath);
			
			byte[] buffer = new byte[1024];
			byte[] outBuffer = new byte[(int) image.length()];
			
			int keyItr = 0;
			int buffItr = 0;
			int byteNum;
			
			while((byteNum = inStream.read(buffer)) != -1) {
				for(int i=0; i < byteNum; i++) {
					outBuffer[buffItr++] = (byte) (buffer[i] ^ key.charAt(keyItr));
					keyItr = (keyItr + 1) % key.length();
				}
			}
			
			inStream.close();
			return outBuffer;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
