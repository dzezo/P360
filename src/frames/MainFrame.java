package frames;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.lwjgl.LWJGLException;

import glRenderer.DisplayManager;
import glRenderer.Scene;
import input.Controller;
import input.Controllers;
import input.InputManager;
import textures.PanNode;

public class MainFrame extends Frame {
	// FRAME
	private Canvas displayCanvas = new Canvas();
	private boolean running = false;
	// MENUBAR
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu mapMenu = new JMenu("Map");
	private JMenu viewMenu = new JMenu("View");
	private JMenu gamePadMenu = new JMenu("Gamepad");
	/* fileMenu items */
	private JMenuItem file_open = new JMenuItem("Open...");
	/* mapMenu items */
	private JMenuItem map_new = new JMenuItem("New Map");
	private JMenuItem map_load = new JMenuItem("Load Map");
	private JMenuItem map_save = new JMenuItem("Save Map");
	private JMenuItem map_change = new JMenuItem("Change Map");
	/* viewMenu items */
	private static JMenuItem view_fullScreen = new JMenuItem("Full Screen");
	private static JCheckBoxMenuItem view_autoPan = new JCheckBoxMenuItem("Auto Pan");
	/* gamePadMenu items */
	private JMenuItem gamePad_scan = new JMenuItem("Scan");
	/* load */
	private boolean newImage = false;
	private boolean newMap = false;
	/* map creation gui */
	private MapFrame mapCreat = new MapFrame("Create Map");
	
	
	public MainFrame(String title) {
		super(title);
		createMenuBar();
		createFrame();
		// Frame created
		DisplayManager.createDisplay(displayCanvas);
		running = true;
	}
	
	protected void createFrame() {
		displayCanvas.setPreferredSize(new Dimension(DisplayManager.getWidth(), DisplayManager.getHeight()));
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(displayCanvas, BorderLayout.CENTER);
		
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		// Frame Listeners
		frame.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
            	// Break main loop
            	running = false;
            	// Disposing mapcreat frame
            	mapCreat.cleanUp();
            	// Disposing self
                cleanUp();
            }
            public void windowActivated(WindowEvent we) {
            	// windowActivated is invoked when the Window is set to be the active Window.
            	// Such case is after exiting map creation mode.
            	displayCanvas.requestFocusInWindow();
            }
        });
	}
	
	private void createMenuBar() {
		// FILE
		fileMenu.add(file_open);
		// MAP
		mapMenu.add(map_new);
		mapMenu.add(map_load);
		map_save.setEnabled(false);
		mapMenu.add(map_save);
		mapMenu.addSeparator();
		map_change.setEnabled(false);
		mapMenu.add(map_change);
		// VIEW
		view_autoPan.setSelected(true);
		viewMenu.add(view_autoPan);
		viewMenu.addSeparator();
		view_fullScreen.setEnabled(false);
		viewMenu.add(view_fullScreen);
		// GAMEPAD
		gamePadMenu.add(gamePad_scan);
		gamePadMenu.addSeparator();
		
		menuBar.add(fileMenu);
		menuBar.add(mapMenu);
		menuBar.add(viewMenu);
		menuBar.add(gamePadMenu);
		
		frame.setJMenuBar(menuBar);
		
		/* Menu listeners */
		MenuListener ml = new MenuListener() {
			public void menuCanceled(MenuEvent e) {
				displayCanvas.requestFocusInWindow();
			}
			public void menuDeselected(MenuEvent e) {}
			public void menuSelected(MenuEvent e) {
				displayCanvas.requestFocusInWindow();
			}};
		
		fileMenu.addMenuListener(ml);
		mapMenu.addMenuListener(ml);
		viewMenu.addMenuListener(ml);
		
		/* Item listeners */
		file_open.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) {
				//show file chooser dialog
				int result = imageChooser.showOpenDialog(null);
				//if file selected
				if(result == JFileChooser.APPROVE_OPTION) {
					String newPath = imageChooser.getSelectedFile().getPath();
					PanNode activePanorama = Scene.getActivePanorama();
					// If there's no active panorama or selected panorma is not the same as active one
					// then set new active panorama and enable fullscreen mode
					if(activePanorama == null || !newPath.equals(activePanorama.getPanoramaPath())) {
						// Remove map if loaded
						PanNode.setHead(null);
						PanNode.setHome(null);
						// set new single panorama
						PanNode newPanorama = new PanNode(newPath);
						Scene.setActivePanorama(newPanorama);
						enableFullScreen();
						newImage = true;
						// disable map mode
						map_save.setEnabled(false);
						map_change.setEnabled(false);
					}
				}
				displayCanvas.requestFocusInWindow();
			}
		});
		
		map_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// No map loaded
				if(PanNode.getHead() == null)
					mapCreat.setVisible(true);
				// Map loaded, prompt overwrite
				else {
					String msg = "Creating new map will overwrite existing one, \nDo you want to continue?";
					int dialogRes = JOptionPane.showConfirmDialog(null, msg, "New Map", JOptionPane.YES_NO_OPTION);
					if(dialogRes == JOptionPane.YES_OPTION) {
						PanNode.setHead(null);
						PanNode.setHome(null);
						mapCreat.setVisible(true);
					}
					else if(dialogRes == JOptionPane.NO_OPTION) {
						displayCanvas.requestFocusInWindow();
					}
				}
				map_save.setEnabled(true);
				map_change.setEnabled(true);
				mapCreat.frame.setTitle("New Map");
			}			
		});
		
		map_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// show and read dialog
				int result = mapChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					String loadPath = mapChooser.getSelectedFile().getPath();
					// attach extension if there is not any
					if(!loadPath.endsWith(".p360"))
						loadPath = loadPath.concat(".p360");
					// check if file exists
					File file = new File(loadPath);
					if(!file.exists()) {
						// show error msg and leave
						JOptionPane.showMessageDialog(null, "File does not exist", "Load Map", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					// loading
					boolean success = PanNode.loadMap(loadPath, imageChooser);
					if(!success) return;
					// setting the origin of a map
					int originX = PanNode.getHome().getMapNode().x;
					int originY = PanNode.getHome().getMapNode().y;
					mapCreat.getMapPanel().setOrigin(originX, originY);
					// display map source as title
					mapCreat.frame.setTitle(loadPath);
					enableFullScreen();
					newMap = true;
					// enable map mode
					map_save.setEnabled(true);
					map_change.setEnabled(true);
				}
				displayCanvas.requestFocusInWindow();
			}
		});
		
		map_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(PanNode.getHead() == null) {
					JOptionPane.showMessageDialog(null, "Nothing to save!", "Save", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					String savePath;
					int result = mapChooser.showSaveDialog(null);
					if(result == JFileChooser.APPROVE_OPTION) {
						// determining saving path
						// set saving location
						savePath = mapChooser.getSelectedFile().getPath();
						// attach extension if there is not any
						if(!savePath.endsWith(".p360"))
							savePath = savePath.concat(".p360");
						// check if file exists
						File saveFile = new File(savePath);
				        if (saveFile.exists()) {
				          int overwriteResult = JOptionPane.showConfirmDialog(null, "The file already exists. Do you want to overwrite it?", "Confirm Replace", JOptionPane.YES_NO_OPTION);
				          if(overwriteResult == JOptionPane.NO_OPTION)
				        	  return;
				        }
						// saving
						PanNode.saveMap(savePath);
						// show message
						mapCreat.frame.setTitle(savePath);
						JOptionPane.showMessageDialog(null, "Saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				displayCanvas.requestFocusInWindow();
			}
		});
		
		map_change.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) {
				mapCreat.setVisible(true);
			}
		});
		
		view_fullScreen.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) {
				DisplayManager.setFullscreen();
				displayCanvas.requestFocusInWindow();
			}
		});
		
		view_autoPan.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) {
				InputManager.setAutoPan();
				displayCanvas.requestFocusInWindow();
			}
		});
		gamePad_scan.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {
				scanForController();
				gamePadMenu.doClick();
			}
		});
	}

	public boolean isRunning() {
		return running;
	}
	
	
	public boolean isNewImage() {
		if(newImage) {
			newImage = false;
			return true;
		}
		else
			return false;
	}
	
	public boolean isNewMap() {
		if(newMap) {
			newMap = false;
			return true;
		}
		else
			return false;
	}

	
	public MapFrame getMapFrame() {
		return mapCreat;
	}

	
	private void scanForController() {
		// Clear dropdown menu
		for(int i=gamePadMenu.getItemCount()-1; i>1; i--)
			gamePadMenu.remove(i);
		// Rescan hardware
		try {
			Controllers.destroy();
			Controllers.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// Add controllers to menu
		for(int i=0; i<Controllers.getControllerCount(); i++) {
			String controllerName = Controllers.getController(i).getName();
			JMenuItem controller = new JMenuItem(controllerName);
			controller.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Get name of selected controller and compare it to all available controllers
					String selectedControllerName = controller.getText();
					for(int i=0; i<Controllers.getControllerCount(); i++) {
						Controller c = Controllers.getController(i);
						if(selectedControllerName.equals(c.getName())) {
							InputManager.setController(c);
							break;
						}
					}
				}
				
			});
			gamePadMenu.add(controller);
		}
	}
	
	public static void enableFullScreen() {
		view_fullScreen.setEnabled(true);
	}
	
	public static void autoPan(boolean b) {
		view_autoPan.setSelected(b);
	}
}