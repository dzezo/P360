package frames;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;

import glRenderer.DisplayManager;
import glRenderer.Scene;
import input.Controller;
import input.Controllers;
import input.InputManager;
import panorama.PanNode;
import utils.ChooserUtils;
import utils.DialogUtils;

@SuppressWarnings("serial")
public class MainFrame extends Frame {
	// FRAME
	private Canvas displayCanvas = new Canvas();
	private boolean running = false;
	// MENUBAR
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu mapMenu = new JMenu("Map");
	private JMenu viewMenu = new JMenu("View");
	private JMenu soundMenu = new JMenu("Sound");
	private JMenu gamePadMenu = new JMenu("Gamepad");
	/* fileMenu items */
	private JMenuItem file_open = new JMenuItem("Open Image");
	/* mapMenu items */
	private JMenuItem map_new = new JMenuItem("New Map");
	private JMenuItem map_load = new JMenuItem("Load Map");
	private JMenuItem map_save = new JMenuItem("Save Map");
	private JMenuItem map_change = new JMenuItem("Change Map");
	private JMenuItem map_show = new JMenuItem("Show Map");
	/* viewMenu items */
	private static JMenuItem view_fullScreen = new JMenuItem("Full Screen");
	private static JCheckBoxMenuItem view_autoPan = new JCheckBoxMenuItem("Auto Pan");
	/* soundMenu items */
	private boolean sound_controlEnabled = false;
	private boolean sound_play = false;
	private static JMenuItem sound_playPause = new JMenuItem("Play");
	private static JMenuItem sound_stop = new JMenuItem("Stop");
	/* gamePadMenu items */
	private JMenuItem gamePad_scan = new JMenuItem("Scan");
	/* load */
	private boolean newImage = false;
	private boolean newMap = false;
	/* map gui */
	private MapDrawFrame mapEditor = new MapDrawFrame("Create Map");
	private MapViewFrame mapView = new MapViewFrame("View Map");
	
	
	public MainFrame(String title) {
		super(title);
		ChooserUtils.init();
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
		
		add(mainPanel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
		
		// Frame Listener
		addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
            	// Break main loop
            	running = false;
            	// Disposing mapView frame
            	mapView.cleanUp();
            	// Disposing mapEditor frame
            	mapEditor.cleanUp();
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
		mapMenu.add(map_save);
		mapMenu.addSeparator();
		mapMenu.add(map_change);
		mapMenu.add(map_show);
		
		enableMapControl(sound_controlEnabled);
		
		// VIEW
		viewMenu.add(view_autoPan);
		viewMenu.addSeparator();
		viewMenu.add(view_fullScreen);
		
		view_autoPan.setSelected(true);
		view_fullScreen.setEnabled(false);
		
		// SOUND
		soundMenu.add(sound_playPause);
		soundMenu.add(sound_stop);
		
		sound_playPause.setEnabled(true);
		sound_stop.setEnabled(true);
		
		// GAMEPAD
		gamePadMenu.add(gamePad_scan);
		gamePadMenu.addSeparator();
		
		menuBar.add(fileMenu);
		menuBar.add(mapMenu);
		menuBar.add(viewMenu);
		menuBar.add(soundMenu);
		menuBar.add(gamePadMenu);
		
		setJMenuBar(menuBar);
		
		/* Item listeners */
		file_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { openSingleImage(); }
		});		
		map_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { newMap(); }			
		});		
		map_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { loadMap(); }
		});		
		map_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { saveMap(); }
		});		
		map_change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { changeMap(); }
		});
		map_show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { showMap(); }
		});	
		view_fullScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { fullscreen(); }
		});
		view_autoPan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { autoPan(); }
		});
		sound_playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { soundPlayPause(); }
		});
		sound_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { soundStop(); }
		});
		gamePad_scan.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) { scanForController(); }
		});
	}

	private void enableMapControl(boolean status) {
		map_save.setEnabled(status);
		map_change.setEnabled(status);
		map_show.setEnabled(status);
	}
	
	public void enableSoundControl(boolean status) {
		// audio track reached end
		if(status && !Scene.getActivePanorama().isAudioPlaying() && sound_play) {
			sound_play = false;
			sound_playPause.setText("Play");
		}
		// track started automatically
		else if(status && Scene.getActivePanorama().isAudioPlaying() && !sound_play) {
			sound_play = true;
			sound_playPause.setText("Pause");
		}
		
		// there is sound and sound is not enabled or
		// there is no sound and sound is enabled
		if(status && !sound_controlEnabled || !status && sound_controlEnabled) {
			// enable/disable
			sound_playPause.setEnabled(status);
			sound_stop.setEnabled(status);
		}
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

	public MapDrawFrame getMapDrawingFrame() {
		return mapEditor;
	}
	
	public MapViewFrame getMapViewFrame() {
		return mapView;
	}
	
	public static void enableFullScreen() {
		view_fullScreen.setEnabled(true);
	}
	
	public static void autoPan(boolean b) {
		view_autoPan.setSelected(b);
	}
	
	/* Menubar Actions */
	
	private void openSingleImage(){
		String newPath = ChooserUtils.openImageDialog();
		if(newPath == null) return;
		PanNode activePanorama = Scene.getActivePanorama();
		
		// If there's no active panorama or selected panorama is not the same as active one
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
			enableMapControl(false);
		}
	}
	
	private void newMap() {
		// No map loaded
		if(PanNode.getHead() == null)
			mapEditor.showFrame();
		
		// Map loaded, prompt overwrite
		else {
			int dialogRes = DialogUtils.showConfirmDialog("Creating new map will overwrite existing one, \nDo you want to continue?", "New Map");
			if(dialogRes == DialogUtils.YES) {
				PanNode.setHead(null);
				PanNode.setHome(null);
				mapEditor.showFrame();
			}
		}
		
		enableMapControl(true);
		mapEditor.setTitle("New Map");
	}
	
	private void loadMap() {
		// loading
		boolean success = mapEditor.load();
		
		if(success) {
			newMap = true;
			enableFullScreen();
			enableMapControl(true);
		}
	}
	
	private void saveMap() {
		mapEditor.save();
	}
	
	private void changeMap() {
		mapEditor.showFrame();
	}
	
	private void showMap() { 
		mapView.showFrame(mapEditor.getTitle());
	}
	
	private void fullscreen() {
		DisplayManager.setFullscreen();
	}
	
	private void autoPan() {
		InputManager.setAutoPan();
	}
	
	private void soundPlayPause() {
		PanNode pan = Scene.getActivePanorama();
		
		// is sound is playing -> pause it / change text to play
		if(sound_play) {
			pan.pauseAudio();
			sound_playPause.setText("Play");
		}
		// is sound paused -> play it / change text to pause
		else {
			pan.playAudio();
			sound_playPause.setText("Pause");
		}
		
		// set flag
		sound_play = !sound_play;
	}
	
	private void soundStop() {
		PanNode pan = Scene.getActivePanorama();
		
		pan.stopAudio();
		
		sound_play = false;
		sound_playPause.setText("Play");
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
}