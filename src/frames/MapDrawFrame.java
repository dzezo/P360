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
import javax.swing.JToolBar;

import panorama.PanNode;
import utils.AutoSave;
import utils.ChooserUtils;
import utils.DialogUtils;

@SuppressWarnings("serial")
public class MapDrawFrame extends MapFrame {
	// ToolBar
	private JToolBar toolbar = new JToolBar();
	private JButton b_add = new JButton(new ImageIcon(Class.class.getResource("/toolbar/add.png")));
	private JButton b_remove = new JButton(new ImageIcon(Class.class.getResource("/toolbar/remove.png")));
	private JButton b_home = new JButton(new ImageIcon(Class.class.getResource("/toolbar/home.png")));
	private JButton b_connect = new JButton(new ImageIcon(Class.class.getResource("/toolbar/connect.png")));
	private JButton b_disconnect = new JButton(new ImageIcon(Class.class.getResource("/toolbar/disconnect.png")));
	private JButton b_addSound = new JButton(new ImageIcon(Class.class.getResource("/toolbar/addSound.png")));
	private JButton b_removeSound = new JButton(new ImageIcon(Class.class.getResource("/toolbar/removeSound.png")));
	private JButton b_genPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/genPath.png")));
	private JButton b_clearPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/clearPath.png")));
	private JButton b_addToPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/addToPath.png")));
	private JButton b_removeFromPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/removeFromPath.png")));
	private JButton b_load = new JButton(new ImageIcon(Class.class.getResource("/toolbar/load.png")));
	private JButton b_save = new JButton(new ImageIcon(Class.class.getResource("/toolbar/save.png")));
	private JButton b_ok = new JButton(new ImageIcon(Class.class.getResource("/toolbar/ok.png")));
	
	// Error messages
	private final String err_selection = "No Selection Found";
	private final String err_noSelection = "Left click to select panorama";
	private final String err_noMultipleSelection = "You need to select both panoramas to run this function.\nYou can use ctrl+click or left and right mouse click to do so.";

	public MapDrawFrame(String title) {
		super(title);
		// instantiate map panel
		mapPanel = new MapDrawPanel();
		
		// create frame
		createToolBar();
		createFrame();
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
		toolbar.add(b_genPath);
		toolbar.add(b_clearPath);
		toolbar.addSeparator();
		toolbar.add(b_addToPath);
		toolbar.add(b_removeFromPath);
		// Add some glue so subsequent items are pushed to the right
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(b_load);
		toolbar.add(b_save);
		toolbar.addSeparator();
		toolbar.add(b_ok);
		
		// Disables toolbar from moving
		toolbar.setFloatable(false);
		getContentPane().add(toolbar, BorderLayout.NORTH);
	}
	
	protected void createFrame() {
		setSize(mapWidth, mapHeight);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		
		// add map panel to frame
		mapPanel.setParent(this);
		add(mapPanel, BorderLayout.CENTER);
		
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
            	// stop frame repaint
                stopFrameRepaint();
                
                // stop auto save and save once more
                AutoSave.stopSaving();
                AutoSave.save();
                
            	// hide frame
                setVisible(false);
            }
        });
	}
	
	public void showFrame() {
		// show frame
		setVisible(true);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		// setting the origin of a map
		setOrigin();
	}
	
	protected void setOrigin() {
		if(PanNode.getHome() != null) {
			int x = PanNode.getHome().getMapNode().x;
			int y = PanNode.getHome().getMapNode().y;
			
			int h = (int) (PanNode.getHome().getMapNode().getHeight() / 2);
			int w = (int) (PanNode.getHome().getMapNode().getWidth() / 2);
			
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;
			
			mapPanel.setOrigin(x - centerX + w, y - centerY + h);
		}
		else {
			mapPanel.setOrigin(0,0);
		}
	}
	
	/* Toolbar Actions */
	private void add() {
		File images[] = ChooserUtils.openImagesDialog();
		if(images == null) return;
		
		int spawnX, spawnY, offset;
		for(int i = 0; i < images.length; i++) {
			offset = i*MapDrawPanel.getGridSize();
			spawnX = mapPanel.getOriginX() + offset;
			spawnY = mapPanel.getOriginY() + offset;
			PanNode.addNode(images[i].getPath(), spawnX, spawnY);
		}
	}
	
	private void remove() {
		PanNode selectedNode = mapPanel.getSelectedNode1();
		if(selectedNode != null) {
			PanNode.deleteNode(selectedNode);
			mapPanel.deselectNodes();
		}
		else
			DialogUtils.showMessage(err_noSelection, err_selection);
	}
	
	private void home() {
		PanNode selectedNode = mapPanel.getSelectedNode1();
		if(selectedNode != null) {
			PanNode.setHome(selectedNode);
		}
		else
			DialogUtils.showMessage(err_noSelection, err_selection);
	}
	
	private void connect() {
		PanNode selectedNode1 = mapPanel.getSelectedNode1();
		PanNode selectedNode2 = mapPanel.getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanNode.connectNodes(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void disconnect() {
		PanNode selectedNode1 = mapPanel.getSelectedNode1();
		PanNode selectedNode2 = mapPanel.getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanNode.disconnectNode(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void addAudio() {
		PanNode selectedNode = mapPanel.getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		String audioPath = ChooserUtils.openAudioDialog();
		if(audioPath == null) 
			return;
		
		PanNode.addAudio(selectedNode, audioPath);
	}
	
	private void removeAudio() {
		PanNode selectedNode = mapPanel.getSelectedNode1();
		if(selectedNode == null) {
			DialogUtils.showMessage(err_noSelection, err_selection);
			return;
		}
		
		PanNode.removeAudio(selectedNode);
	}
	
	private void genPath() {
		PanNode.genPath();
	}
	
	private void clearPath() {
		PanNode.clearPath();
	}
	
	private void addToPath() {
		PanNode selectedNode1 = mapPanel.getSelectedNode1();
		PanNode selectedNode2 = mapPanel.getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanNode.addToPath(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	private void removeFromPath() {
		PanNode selectedNode1 = mapPanel.getSelectedNode1();
		PanNode selectedNode2 = mapPanel.getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanNode.removeFromPath(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage(err_noMultipleSelection, err_selection);
	}
	
	public boolean load() {
		// get path
		String loadPath = ChooserUtils.openMapDialog();
		if(loadPath == null) return false;
		
		// load
		boolean success = PanNode.loadMap(loadPath);
		if(success) {
			// setting the origin of a map
			setOrigin();
			
			// display map source as title
			setTitle(loadPath);
		}
		
		return success;
	}
	
	public boolean save() {
		// get path
		String savePath = ChooserUtils.saveMapDialog();
		if(savePath == null) return false;
		
		// save
		boolean success = PanNode.saveMap(savePath);
		if(success) {
			// display map source as title
			setTitle(savePath);
			
			// show message
			DialogUtils.showMessage("Saved!", "Save");
		}
		
		return success;
	}
	
	private void ok() {
		if(PanNode.getHome() != null) {
			updated = true;
			
			// stop auto save and save once more
            AutoSave.stopSaving();
            AutoSave.save();
            
            // hide frame
			setVisible(false);
			
			MainFrame.enableFullScreen();
		}
	}
	
}
