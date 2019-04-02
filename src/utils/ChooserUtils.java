package utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ChooserUtils {
	/* choosers */
	private static JFileChooser jfc_image = new JFileChooser();
	private static JFileChooser jfc_map = new JFileChooser();
	private static JFileChooser jfc_audio = new JFileChooser();
	
	/* chooser filters */
	private static FileFilter ff_image = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			fileName = fileName.toLowerCase();
			return (fileName.endsWith(".jpg") || fileName.endsWith(".tif"));
		}
		public String getDescription() {
			return "*.jpg, *.tif";
		}
	};
	private static FileFilter ff_map = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			return (fileName.endsWith(".p360"));
		}
		public String getDescription() {
			return "*.p360";
		}
	};
	private static FileFilter ff_audio = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			fileName = fileName.toLowerCase();
			return (fileName.endsWith(".wav"));
		}
		public String getDescription() {
			return "*.wav";
		}
	};
	
	public static void init() {
		// setting up choosers
		jfc_image.setAcceptAllFileFilterUsed(false);
		jfc_image.setFileFilter(ff_image);
		
		jfc_map.setAcceptAllFileFilterUsed(false);
		jfc_map.setFileFilter(ff_map);
		
		jfc_audio.setAcceptAllFileFilterUsed(false);
		jfc_audio.setFileFilter(ff_audio);
	}
	
	public static String openMapDialog() {
		int result = jfc_map.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String loadPath = jfc_map.getSelectedFile().getPath();
			
			// attach extension if there is not any
			if(!loadPath.endsWith(".p360"))
				loadPath = loadPath.concat(".p360");
			
			// check if file exists
			File file = new File(loadPath);
			
			// file does not exist
			if(!file.exists()) {
				// show error msg and leave
				DialogUtils.showMessage("File does not exist", "Load Map");
				return null;
			}
			// file exists
			else {
				// return path to file
				return loadPath;
			}
		}
		// opening canceled
		else {
			return null;
		}
		
	}
	
	public static String saveMapDialog() {
		int result = jfc_map.showSaveDialog(null);
		
		// determining saving path
		if(result == JFileChooser.APPROVE_OPTION) {
			String savePath = jfc_map.getSelectedFile().getPath();
			
			// attach extension if there is not any
			if(!savePath.endsWith(".p360"))
				savePath = savePath.concat(".p360");
			
			// check if file exists
			File saveFile = new File(savePath);
			
			// file exists
	        if (saveFile.exists()) {
	          int overwriteResult = 
	        		  DialogUtils.showConfirmDialog("The file already exists. Do you want to overwrite it?", "Confirm Replace");
	          
	          // overwrite is refused
	          if(overwriteResult == DialogUtils.NO)
	        	  return null;
	        }
	        
	        // path is free to use or overwrite path
	        return savePath;
		}
		// saving canceled
		else {
			return null;
		}
	}
	
	public static String openImageDialog() {
		int result = jfc_image.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String panPath = jfc_image.getSelectedFile().getPath();
			
			return panPath;
		}
		// opening canceled;
		else {
			return null;
		}
	}
	
}
