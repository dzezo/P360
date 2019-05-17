package panorama;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class PanMapIcon implements Serializable {
	private PanMap parent;
	private ImageIcon icon;
	
	public PanMapIcon() {
		parent = null;
		icon = null;
	}
	
	public PanMapIcon(PanMap parent, String iconPath) {
		this.parent = parent;
		
		ImageIcon tmpIcon = new ImageIcon(iconPath);
        if (tmpIcon != null) {    
        	icon = new ImageIcon(tmpIcon.getImage().getScaledInstance(PanMap.WIDTH, PanMap.HEIGHT, Image.SCALE_DEFAULT));  
        	tmpIcon.getImage().flush();
            tmpIcon = null;
        }
        
	}
	
	public void drawIcon(Graphics2D g) {
		int dx1 = parent.x + 1;
		int dy1 = parent.y + 1;
		int dx2 = parent.x + PanMap.WIDTH;
		int dy2 = parent.y + PanMap.HEIGHT;
		g.drawImage(icon.getImage(), dx1, dy1, dx2, dy2, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
	}
	
	public boolean isLoaded() {
		if(icon == null)
			return false;
		else
			return true;
	}
}
