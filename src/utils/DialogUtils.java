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
	
}
