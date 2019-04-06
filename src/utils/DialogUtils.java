package utils;

import javax.swing.JOptionPane;

public class DialogUtils {
	public static int YES = JOptionPane.YES_OPTION;
	public static int NO = JOptionPane.NO_OPTION;
	
	public static void showMessage(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int showConfirmDialog(String msg, String title) {
		return JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);
	}
	
	/**
	 * Returns true if user wants to replace path, and vice versa
	 */
	public static boolean replacePathDialog(String path) {
		int dialogRes = showConfirmDialog("Could not find: " + path + "\nDo you want to change the path?", "Path Not Found");
		if(dialogRes == YES) {
			return true;
		}
		else {
			DialogUtils.showMessage("Could not find: " + path + "\nLoading is aborted.", "Loading Aborted");
			return false;
		}
	}
	
}
