package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import main.Main;

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
	 * @return putanja do enkriptovane slike
	 * @throws Exception IOException / KeyNotFound
	 */
	public static String imageEncrypt(String imagePath, String key) throws Exception {
		if(key == null) throw new Exception("Encryption key not found.");
		
		String encryptedImagePath = createEncryptedImagePath(imagePath);
		
		FileInputStream inStream = new FileInputStream(imagePath);
		FileOutputStream outStream = new FileOutputStream(encryptedImagePath);
		
		byte[] buffer = new byte[1024];
		byte[] outBuffer;
		
		int keyItr = 0;
		int byteNum;
		
		// write key
		outBuffer = new byte[32];
		for(int i = 0; i < outBuffer.length; i++) {
			if(i < key.length()) {
				outBuffer[i] = (byte) (key.charAt(i) & 0x00FF); // ASCII support
			}
			else {
				outBuffer[i] = 0;
			}
		}
		
		// write encrypted bytes
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
	}
	
	/**
	 * Vrsi dekripciju slike, tako sto XOR-uje svaki bajt slike sa jednim karakterom iz kljuca.
	 * @param imagePath - putanja do slike koju treba dekriptovati
	 * @param key - kljuc za dekripciju
	 * @return dekriptovane bajtove slike
	 * @throws Exception IOException
	 */
	public static byte[] imageDecrypt(String imagePath, String key) throws Exception {
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
	}
	
	/**
	 * Vrsi re-enkripciju sliku
	 * @param imagePath - putanja do slike koju treba re-enkriptovati
	 * @param oldKey - kljuc za enkripciju
	 * @param newKey - novi kljuc za enkripciju
	 * @throws Exception IOException / KeyNotFound
	 */
	public static void imageReEncrypt(String imagePath, String oldKey, String newKey) throws Exception {
		if(oldKey == null) throw new Exception("Old encryption key not found.");
		if(newKey == null) throw new Exception("New encryption key not found.");
		
		// Decrypted data is at working_dir\tmp.pimg
		
		String strSrcImage = imagePath;
		String strDstImage = Main.WORKING_DIR.getPath() + "\\tmp.pimg";
		
		FileInputStream inStream = new FileInputStream(strSrcImage);
		FileOutputStream outStream = new FileOutputStream(strDstImage);
		
		byte[] buffer = new byte[1024];
		byte[] outBuffer;
		
		int oldKeyItr = 0;
		int newKeyItr = 0;
		int byteNum;
		
		while((byteNum = inStream.read(buffer)) != -1) {
			outBuffer = new byte[byteNum];
			for(int i=0; i < byteNum; i++) {
				outBuffer[i] = (byte) (buffer[i] ^ oldKey.charAt(oldKeyItr));
				
				oldKeyItr = (oldKeyItr + 1) % oldKey.length();
			}
			
			outStream.write(outBuffer);
		}
		
		outStream.flush();
		outStream.close();
		inStream.close();
		
		// Encrypt data back to source path
		inStream = new FileInputStream(strDstImage);
		outStream = new FileOutputStream(strSrcImage);
		
		while((byteNum = inStream.read(buffer)) != -1) {
			outBuffer = new byte[byteNum];
			for(int i=0; i < byteNum; i++) {
				outBuffer[i] = (byte) (buffer[i] ^ newKey.charAt(newKeyItr));
				
				newKeyItr = (newKeyItr + 1) % newKey.length();
			}
			
			outStream.write(outBuffer);
		}
		
		outStream.flush();
		outStream.close();
		inStream.close();
		
		// Remove tmp.pimg
		File file = new File(strDstImage);
		file.delete();
	}
}
