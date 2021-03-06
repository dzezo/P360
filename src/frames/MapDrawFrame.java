package frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import glRenderer.Scene;
import loaders.IconLoader;
import panorama.PanGraph;
import panorama.PanNode;
import touring.TourManager;
import utils.AutoSave;
import utils.DialogUtils;

@SuppressWarnings("serial")
public class MapDrawFrame extends MapFrame {
	private static volatile MapDrawFrame instance = null;
	
	public static synchronized MapDrawFrame getInstance() {
		if(instance == null) {
			instance = new MapDrawFrame();
		}
		
		return instance;
	}
	
	// ToolBar
	private JToolBar toolbar = new JToolBar();
	private JButton b_add = new JButton(new ImageIcon(Class.class.getResource("/toolbar/add.png")));
	private JButton b_remove = new JButton(new ImageIcon(Class.class.getResource("/toolbar/remove.png")));
	private JButton b_home = new JButton(new ImageIcon(Class.class.getResource("/toolbar/home.png")));
	private JButton b_connect = new JButton(new ImageIcon(Class.class.getResource("/toolbar/connect.png")));
	private JButton b_disconnect = new JButton(new ImageIcon(Class.class.getResource("/toolbar/disconnect.png")));
	private JButton b_addSound = new JButton(new ImageIcon(Class.class.getResource("/toolbar/addSound.png")));
	private JButton b_removeSound = new JButton(new ImageIcon(Class.class.getResource("/toolbar/removeSound.png")));
	private JButton b_addVideo = new JButton(new ImageIcon(Class.class.getResource("/toolbar/addVideo.png")));
	private JButton b_removeVideo = new JButton(new ImageIcon(Class.class.getResource("/toolbar/removeVideo.png")));
	private JButton b_genPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/genPath.png")));
	private JButton b_clearPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/clearPath.png")));
	private JButton b_addToPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/addToPath.png")));
	private JButton b_removeFromPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/removeFromPath.png")));
	private JToggleButton b_textMode = new JToggleButton(new ImageIcon(Class.class.getResource("/toolbar/text.png")));
	private JButton b_encrypt = new JButton(new ImageIcon(Class.class.getResource("/toolbar/encrypt.png")));
	private JButton b_load = new JButton(new ImageIcon(Class.class.getResource("/toolbar/load.png")));
	private JButton b_save = new JButton(new ImageIcon(Class.class.getResource("/toolbar/save.png")));
	private JButton b_ok = new JButton(new ImageIcon(Class.class.getResource("/toolbar/ok.png")));
	
	// ProgressBar
	private JProgressBar progressBar;
	
	// Error messages
	private final String err_selection = 
			"No Selection Found";
	private final String err_noSelection = 
			"Left click to select panorama";
	private final String err_noMultipleSelection = 
			"You need to select both panoramas to run this function.\n"
			+ "You can use ctrl+click or left and right mouse click to do so.";

	private MapDrawFrame() {
		super("P360");
		// instantiate map panel
		setMapPanel(new MapDrawPanel());
		
		// create frame
		createToolBar();
		createFrame();
		createProgressBar();
		
		// init AutoSave
		AutoSave.init(this);
	}

	private void createToolBar() {
		// JButton ActionListeners
		b_add.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) { add(); }
		});
		b_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { remove(); }
		});
		b_home.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { home(); }
		});
		b_connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { connect(); }			
		});
		b_disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { disconnect(); }			
		});
		b_addSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { addAudio(); }
		});
		b_removeSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { removeAudio(); }
		});
		b_addVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { addVideo(); }
		});
		b_removeVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { removeVideo(); }
		});
		b_genPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { genPath(); }
		});
		b_clearPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { clearPath(); }
		});
		b_addToPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { addToPath(); }
		});
		b_removeFromPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { removeFromPath(); }
		});
		b_textMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { setTextMode(); }
		});
		b_encrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { encrypt(); }
		});
		b_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { load(); }
		});
		b_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { save(); }
		});
		b_ok.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) { ok(); }
		});
		
		// The JToolBar uses a BoxLayout to layout it�s components.
		toolbar.add(b_add);
		toolbar.add(b_remove);
		toolbar.add(b_home);
		toolbar.addSeparator();
		toolbar.add(b_connect);
		toolbar.add(b_disconnect);
		toolbar.addSeparator();
		toolbar.add(b_addSound);
		toolbar.add(b_removeSound);
		toolbar.addSeparator();
		toolbar.add(b_addVideo);
		toolbar.add(b_removeVideo);
		toolbar.addSeparator();
		toolbar.add(b_genPath);
		toolbar.add(b_clearPath);
		toolbar.addSeparator();
		toolbar.add(b_addToPath);
		toolbar.add(b_removeFromPath);
		toolbar.addSeparator();
		toolbar.add(b_textMode);
		// Add some glue so subsequent items are pushed to the right
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(b_encrypt);
		toolbar.addSeparator();
		toolbar.add(b_load);
		toolbar.add(b_save);
		toolbar.addSeparator();
		toolbar.add(b_ok);
		
		// Disables toolbar from moving
		toolbar.setFloatable(false);
		getContentPane().add(toolbar, BorderLayout.NORTH);
	}
	
	private void createFrame() {
		setSize(mapSize);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		// add map panel to frame
		getMapPanel().setParent(this);
		add(getMapPanel(), BorderLayout.CENTER);
		
		setVisible(false);
		
		// Listeners
		addWindowListener(new WindowAdapter() 
		{
			public void windowActivated(WindowEvent we) {
				// repaint frame every 20milis
				startFrameRepaint();
				
				// auto save every minute
				AutoSave.startSaving();
			}
			
            public void windowClosing(WindowEvent we){               
                // stop auto save
                AutoSave.stopSaving();
                
            	// hide frame
                hideFrame();
            }
        });
	}
	
	private void createProgressBar() {
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		getContentPane().add(progressBar, BorderLayout.SOUTH);
	}
	
	public void showFrame() {
		setTitle(PanGraph.getName());
		setVisible(true);
		setOrigin();
		
		// Unpause IconLoader
		IconLoader.getInstance().postponeLoading(false);
	}
	
	public void hideFrame() {
        stopFrameRepaint();
        setVisible(false);
        getMapPanel().deselectNodes();
        
        // Pause IconLoader
        IconLoader.getInstance().postponeLoading(true);
        
        PanGraph.getGraphSize().updateSize();
	}
	
	/* Toolbar Actions */
	private void add() {
		File images[] = DialogUtils.openImagesDialog();
		if(images == null) return;
		
		int spawnX, spawnY, offset;
		for(int i = 0; i < images.length; i++) {
			offset = i*MapDrawPanel.getGridSize();
			spawnX = -getMapPanel().getOrigin().x + offset;
			spawnY = -getMapPanel().getOrigin().y + offset;
			PanGraph.addNode(images[i].getPath(), spawnX, spawnY);
		}
	}
	
	private void remove() {
		PanNode selectedNode = getMapPanel().getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		PanGraph.deleteNode(selectedNode);
		getMapPanel().deselectNodes();
	}
	
	private void home() {
		PanNode selectedNode = getMapPanel().getSelectedNode1();
		if(selectedNode != null) {
			PanGraph.setHome(selectedNode);
		}
		else
			DialogUtils.showMessage(err_noSelection, err_selection);
	}
	
	private void connect() {
		PanNode selectedNode1 = getMapPanel().getSelectedNode1();
		PanNode selectedNode2 = getMapPanel().getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanGraph.connectNodes(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void disconnect() {
		PanNode selectedNode1 = getMapPanel().getSelectedNode1();
		PanNode selectedNode2 = getMapPanel().getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanGraph.disconnectNode(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void addAudio() {
		PanNode selectedNode = getMapPanel().getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		String audioPath = DialogUtils.openAudioDialog();
		if(audioPath == null) 
			return;
		
		selectedNode.setAudio(audioPath);
	}
	
	private void removeAudio() {
		PanNode selectedNode = getMapPanel().getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		selectedNode.removeAudio();
	}
	
	private void addVideo() {
		PanNode selectedNode = getMapPanel().getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		String videoPath = DialogUtils.openVideoDialog();
		if(videoPath == null) 
			return;
		
		selectedNode.setVideoPath(videoPath);
	}
	
	private void removeVideo() {
		PanNode selectedNode = getMapPanel().getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		selectedNode.setVideoPath(null);
	}
	
	private void genPath() {
		PanGraph.genPath();
	}
	
	private void clearPath() {
		PanGraph.clearPath();
	}
	
	private void addToPath() {
		PanNode selectedNode1 = getMapPanel().getSelectedNode1();
		PanNode selectedNode2 = getMapPanel().getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanGraph.addWaypoint(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void removeFromPath() {
		PanNode selectedNode1 = getMapPanel().getSelectedNode1();
		PanNode selectedNode2 = getMapPanel().getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanGraph.removeWaypoint(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void setTextMode() {
		PanGraph.setTextMode(b_textMode.isSelected());
	}
	
	private void encrypt() {
		if(PanGraph.isEmpty()) {
			DialogUtils.showMessage("Nothing to encrypt!", "Encrypt");
			return;
		}
		
		// get key
		String encryptionKey = DialogUtils.showKeyInputDialog();
		if(encryptionKey == null) return;
		
		// encrypt map
		PanGraph.encryptMap(encryptionKey, this);
	}
	
	public boolean load() {
		// get path
		String loadPath = DialogUtils.openMapDialog();
		if(loadPath == null) return false;
		
		// load
		boolean success = PanGraph.loadMap(loadPath);
		if(success) {
			// setting the origin of a map
			setOrigin();
			
			// display map source as title
			setTitle(loadPath);
		}
		
		return success;
	}
	
	public void save() {
		// nothing to save
		if(PanGraph.isEmpty()) {
			DialogUtils.showMessage("Nothing to save!", "Save");
			return;
		}
		
		// get path
		String savePath = DialogUtils.saveMapDialog();
		if(savePath == null) return;
		
		// save
		PanGraph.saveMap(savePath, this, false);
	}
	
	private void ok() {
		if(PanGraph.getHome() != null) {
			Scene.queuePanorama(PanGraph.getHome());
			TourManager.prepare(PanGraph.getHome());
			
			// stop auto save
            AutoSave.stopSaving();
            
            // hide frame
			hideFrame();
		}
	}
	
	public void enableActions(boolean enable) {
		b_add.setEnabled(enable);
		b_remove.setEnabled(enable);
		b_home.setEnabled(enable);
		b_connect.setEnabled(enable);
		b_disconnect.setEnabled(enable);
		b_addSound.setEnabled(enable);
		b_removeSound.setEnabled(enable);
		b_genPath.setEnabled(enable);
		b_clearPath.setEnabled(enable);
		b_addToPath.setEnabled(enable);
		b_removeFromPath.setEnabled(enable);
		b_textMode.setEnabled(enable);
		b_encrypt.setEnabled(enable);
		b_load.setEnabled(enable);
		b_save.setEnabled(enable);
		b_ok.setEnabled(enable);
	}
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
}
