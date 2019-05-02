package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import panorama.PanGraph;

@SuppressWarnings("serial")
public class AutoLoad implements Serializable {
	private static final String AUTO_LOAD_CONFIG_PATH = "auto_load.cfg";
	
	private static String lastUsedMap;
	
	public static boolean load() {
		// get file path of the last map
		try {
			FileInputStream fin = new FileInputStream(AUTO_LOAD_CONFIG_PATH);
			ObjectInputStream ois = new ObjectInputStream(fin);
			lastUsedMap = (String) ois.readObject();
			ois.close();
		} catch (Exception e) {
			System.out.println("Config file not found!");
			return false;
		}
		
		// load last used map
		return PanGraph.loadMap(lastUsedMap);
	}
	
	public static void setLastUsedMap(String filePath) {
		// update path of the last map
		lastUsedMap = new String(filePath);
		
		// update config file
		try {
			FileOutputStream fs = new FileOutputStream(AUTO_LOAD_CONFIG_PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fs);
			oos.writeObject(lastUsedMap);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
