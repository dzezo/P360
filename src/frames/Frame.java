package frames;

import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public abstract class Frame {
	/* frame */
	protected JFrame frame;
	/* choosers */
	protected JFileChooser imageChooser = new JFileChooser();
	protected JFileChooser mapChooser = new JFileChooser();
	/* chooser filters */
	protected FileFilter imageFileFilter = new FileFilter() {
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
	protected FileFilter mapFileFilter = new FileFilter() {
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
	
	public Frame(String title) {
		frame = new JFrame(title);
		// setting up choosers
		imageChooser.setAcceptAllFileFilterUsed(false);
		imageChooser.setFileFilter(imageFileFilter);
		mapChooser.setAcceptAllFileFilterUsed(false);
		mapChooser.setFileFilter(mapFileFilter);
	}
	
	protected abstract void createFrame();
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void cleanUp() {
		frame.setVisible(false);
        frame.dispose();
        System.out.println(frame.getTitle() + " is disposed...");
	}
}
