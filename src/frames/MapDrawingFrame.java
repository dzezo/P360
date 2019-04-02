package frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import panorama.PanNode;
import utils.ChooserUtils;
import utils.DialogUtils;

@SuppressWarnings("serial")
public class MapDrawingFrame extends MapFrame {
	// Map
	private boolean mapUpdated = false;
	
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
	private JButton b_editPath = new JButton(new ImageIcon(Class.class.getResource("/toolbar/editPath.png")));
	private JButton b_load = new JButton(new ImageIcon(Class.class.getResource("/toolbar/load.png")));
	private JButton b_save = new JButton(new ImageIcon(Class.class.getResource("/toolbar/save.png")));
	private JButton b_ok = new JButton(new ImageIcon(Class.class.getResource("/toolbar/ok.png")));

	public MapDrawingFrame(String title) {
		super(title);
		createToolBar();
		createFrame();
	}

	protected void createFrame() {
		setSize(mapWidth, mapHeight);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		
		mapPanel = new MapDrawingPanel();
		add(mapPanel, BorderLayout.CENTER);
		
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		executor.scheduleAtFixedRate(new RepaintMap(this), 0, 20, TimeUnit.MILLISECONDS);
		
		setVisible(false);
		
		// Listeners
		addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
                setVisible(false);
            }
        });
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
			public void actionPerformed(ActionEvent arg0) { addSound(); }
		});
		b_removeSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { removeSound(); }
		});
		b_genPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { genPath(); }
		});
		b_clearPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { clearPath(); }
		});
		b_editPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { editPath(); }
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
		
		// The JToolBar uses a BoxLayout to layout it’s components.
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
		toolbar.add(b_editPath);
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

	public boolean isMapUpdated() {
		if (mapUpdated) {
			mapUpdated = false;
			return true;
		}
		else
			return false;
	}
	
	public void showFrame() {
		setVisible(true);
		
		// setting the origin of a map
		setOrigin();
	}
	
	/* Toolbar Actions */
	private void add() {
		PanNode.addNode(mapPanel.getOriginX(), mapPanel.getOriginY());
	}
	
	private void remove() {
		PanNode selectedNode = mapPanel.getSelectedNode1();
		if(selectedNode != null) {
			PanNode.deleteNode(selectedNode);
			mapPanel.deselectNodes();
		}
		else
			DialogUtils.showMessage("Left click to select panorama", "No Selection Found");
	}
	
	private void home() {
		PanNode selectedNode = mapPanel.getSelectedNode1();
		if(selectedNode != null) {
			PanNode.setHome(selectedNode);
		}
		else
			DialogUtils.showMessage("Left click to select panorama", "No Selection Found");
	}
	
	private void connect() {
		PanNode selectedNode1 = mapPanel.getSelectedNode1();
		PanNode selectedNode2 = mapPanel.getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanNode.connectNodes(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage("Use Ctrl+click to select multiple panoramas.", "No Selection Found");
	}
	
	private void disconnect() {
		PanNode selectedNode1 = mapPanel.getSelectedNode1();
		PanNode selectedNode2 = mapPanel.getSelectedNode2();
		if(selectedNode1 != null && selectedNode2 != null) {
			PanNode.disconnectNode(selectedNode1, selectedNode2);
		}
		else
			DialogUtils.showMessage("Use Ctrl+click to select multiple panoramas.", "No Selection Found");
	}
	
	private void addSound() {
		
	}
	
	private void removeSound() {
		
	}
	
	private void genPath() {
		PanNode.genPath();
	}
	
	private void clearPath() {
		
	}
	
	private void editPath() {
		
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
		if(PanNode.getHead() == null) {
			DialogUtils.showMessage("Nothing to save!", "Save");
			return false;
		}
		else {
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
	}
	
	private void ok() {
		if(PanNode.getHome() != null)
			mapUpdated = true;
		setVisible(false);
		MainFrame.enableFullScreen();
	}
	
}
