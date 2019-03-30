package frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import textures.PanNode;

public class MapFrame extends Frame {
	// Frame
	public static int mapWidth = 800;
	public static int mapHeight = 600;
	private MapDrawingPanel mapPanel;
	private boolean mapUpdated = false;
	// ToolBar
	private JToolBar toolbar = new JToolBar();
	private JButton b_add = new JButton(new ImageIcon(Class.class.getResource("/toolbar/add.png")));
	private JButton b_remove = new JButton(new ImageIcon(Class.class.getResource("/toolbar/remove.png")));
	private JButton b_home = new JButton(new ImageIcon(Class.class.getResource("/toolbar/home.png")));
	private JButton b_connect = new JButton(new ImageIcon(Class.class.getResource("/toolbar/connect.png")));
	private JButton b_disconnect = new JButton(new ImageIcon(Class.class.getResource("/toolbar/disconnect.png")));
	private JButton b_genPath = new JButton("PATH");
	private JButton b_load = new JButton(new ImageIcon(Class.class.getResource("/toolbar/load.png")));
	private JButton b_save = new JButton(new ImageIcon(Class.class.getResource("/toolbar/save.png")));
	private JButton b_ok = new JButton(new ImageIcon(Class.class.getResource("/toolbar/ok.png")));

	public MapFrame(String title) {
		super(title);
		createToolBar();
		createFrame();
	}

	protected void createFrame() {
		frame.setSize(mapWidth, mapHeight);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		mapPanel = new MapDrawingPanel();
		frame.add(mapPanel, BorderLayout.CENTER);
		
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		executor.scheduleAtFixedRate(new RepaintMap(this), 0, 20, TimeUnit.MILLISECONDS);
		
		frame.setVisible(false);
		
		// Listeners
		frame.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
                frame.setVisible(false);
            }
        });
	}

	private void createToolBar() {
		// JButton ActionListeners
		b_add.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				// Show file chooser dialog
				int result = imageChooser.showOpenDialog(null);
				// if file selected
				if(result == JFileChooser.APPROVE_OPTION) {
					String panPath = imageChooser.getSelectedFile().getPath();
					PanNode.addNode(panPath, mapPanel.getOriginX(), mapPanel.getOriginY());
				}
			}
		});
		b_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanNode selectedNode = mapPanel.getSelectedNode1();
				if(selectedNode != null) {
					PanNode.deleteNode(selectedNode);
					mapPanel.deselectNodes();
				}
				else
					showMessage("Left click to select panorama", "No Selection Found");
			}
		});
		b_home.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanNode selectedNode = mapPanel.getSelectedNode1();
				if(selectedNode != null) {
					PanNode.setHome(selectedNode);
				}
				else
					showMessage("Left click to select panorama", "No Selection Found");
			}
		});
		b_connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanNode selectedNode1 = mapPanel.getSelectedNode1();
				PanNode selectedNode2 = mapPanel.getSelectedNode2();
				if(selectedNode1 != null && selectedNode2 != null) {
					PanNode.connectNodes(selectedNode1, selectedNode2);
				}
				else
					showMessage("Use Ctrl+click to select multiple panoramas.", "No Selection Found");
			}
			
		});
		b_disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PanNode selectedNode1 = mapPanel.getSelectedNode1();
				PanNode selectedNode2 = mapPanel.getSelectedNode2();
				if(selectedNode1 != null && selectedNode2 != null) {
					PanNode.disconnectNode(selectedNode1, selectedNode2);
				}
				else
					showMessage("Use Ctrl+click to select multiple panoramas.", "No Selection Found");
			}
			
		});
		b_genPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PanNode.genPath();
			}
		});
		b_load.addActionListener(new ActionListener() {
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
						showMessage("File does not exist.","Load Map");
						return;
					}
					// loading
					boolean success = PanNode.loadMap(loadPath, imageChooser);
					if(!success) return;
					// setting the origin of a map
					int originX = PanNode.getHome().getMapNode().x;
					int originY = PanNode.getHome().getMapNode().y;
					mapPanel.setOrigin(originX, originY);
					// display map source as title
					frame.setTitle(loadPath);
				}
			}
		});
		b_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(PanNode.getHead() == null) {
					showMessage("Nothing to save!", "Save");
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
						//Show message
						frame.setTitle(savePath);
						showMessage("Saved!", "Save");
					}
				}
			}
		});
		b_ok.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				if(PanNode.getHome() != null)
					mapUpdated = true;
				frame.setVisible(false);
				MainFrame.enableFullScreen();
			}
		});
		// The JToolBar uses a BoxLayout to layout it’s components.
		toolbar.add(b_add);
		toolbar.add(b_remove);
		toolbar.add(b_home);
		toolbar.addSeparator();
		toolbar.add(b_connect);
		toolbar.add(b_disconnect);
		toolbar.addSeparator();
		toolbar.add(b_genPath);
		// Add some glue so subsequent items are pushed to the right
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(b_load);
		toolbar.add(b_save);
		toolbar.add(b_ok);
		// Disables toolbar from moving
		toolbar.setFloatable(false);
		frame.getContentPane().add(toolbar, BorderLayout.NORTH);
	}

	public boolean isMapUpdated() {
		if (mapUpdated) {
			mapUpdated = false;
			return true;
		}
		else
			return false;
	}

	public MapDrawingPanel getMapPanel() {
		return mapPanel;
	}
	
	private void showMessage(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
