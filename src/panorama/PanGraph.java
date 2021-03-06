package panorama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import frames.MapDrawFrame;
import graph.NodeList;
import touring.TourPath;
import touring.Waypoint;
import utils.AutoSave;
import utils.ConfigData;
import utils.DialogUtils;
import utils.ImageCipher;
import utils.StringUtils;

public class PanGraph {
	public static final String DEFAULT_NAME = "New Map";
	
	private static PanNode head;
	private static PanNode home;
	private static String name;
	private static int nodeCount = 0;
	
	// graph graphSize
	private static PanGraphSize graphSize = new PanGraphSize();
	
	// graph path
	private static TourPath tour = new TourPath();
	
	// graph display mode
	private static boolean textMode = false;
	
	/* map creation functionality */
	
	public static void addNode(String panoramaPath, int originX, int originY) {
		// Create node
		PanNode newNode = new PanNode(panoramaPath, originX, originY);
		
		// Incase there is no home(starting) panorama set;
		if(home == null)
			setHome(newNode);
		// Check if there is root node (linked list head)
		// if the root exist add this node to existing list
		// else set this node as root for the entire class
		if(head != null) {
			setTail(newNode);
		}
		else
			setHead(newNode);
		
		// Update node count
		nodeCount++;
	}
	
	public static void deleteNode(PanNode selectedPanorama) {
		// Remove his connections
		if(selectedPanorama.getLeft() != null) {
			// This will remove waypoints between these nodes if there are any
			deleteWaypoint(selectedPanorama, selectedPanorama.getLeft());
			deleteWaypoint(selectedPanorama.getLeft(), selectedPanorama);
			
			selectedPanorama.getLeft().setRight(null);
			selectedPanorama.setLeft(null);
		}
		if(selectedPanorama.getRight() != null) {
			deleteWaypoint(selectedPanorama, selectedPanorama.getRight());
			deleteWaypoint(selectedPanorama.getRight(), selectedPanorama);
			
			selectedPanorama.getRight().setLeft(null);
			selectedPanorama.setRight(null);
		}
		if(selectedPanorama.getTop() != null) {
			deleteWaypoint(selectedPanorama, selectedPanorama.getTop());
			deleteWaypoint(selectedPanorama.getTop(), selectedPanorama);
			
			selectedPanorama.getTop().setBot(null);
			selectedPanorama.setTop(null);
		}
		if(selectedPanorama.getBot() != null) {
			deleteWaypoint(selectedPanorama, selectedPanorama.getBot());
			deleteWaypoint(selectedPanorama.getBot(), selectedPanorama);
			
			selectedPanorama.getBot().setTop(null);
			selectedPanorama.setBot(null);
		}
		// Remove node from linked list
		// If selected node is head of linked list, new head would be next node after head
		// else its somewhere in the list and needs to be skiped
		if(head == selectedPanorama) {
			head = head.getNext();
		}
		else {
			PanNode start = head;
			// if the current node is not next to selected node go further
			while(start.getNext() != selectedPanorama) {
				start = start.getNext();
			}
			// when this loop breaks next node from the start would be the one that should be skiped
			// set next node as the next node of a selected node
			start.setNext(start.getNext().getNext());
		}
		if(home == selectedPanorama) {
			// If it was starting panorama then assign head as a starting panorama
			// If it was last node then head would be null by now which will make home null
			if(head != null)
				setHome(head);
			else
				home = null;
		}
		
		// Update node count
		nodeCount--;
	}

	private static void deleteWaypoint(PanNode node1, PanNode node2) {
		if(!tour.hasPath() || TourPath.WAYPOINT_NOT_FOUND == tour.remove(new Waypoint(node1, node2)))
			return;
		
		// Reset graphics
		node2.getMapNode().setArrow(node1, false);	
		
		// Reset TourNum on every node
		for(PanNode node = head; node != null; node = node.getNext())
			node.tourNum.clear();
		
		// Assign new TourNum on every node
		tour.updateTourNumbers();
	}
	
	public static void setHome(PanNode node) {
		home = node;
	}
	
	public static void connectNodes(PanNode node1, PanNode node2) {
		// Check if this nodes are already connected
		boolean alreadyConnected = false;
		if(node1.getLeft() == node2)
			alreadyConnected = true;
		else if(node1.getRight() == node2)
			alreadyConnected = true;
		else if(node1.getTop() == node2)
			alreadyConnected = true;
		else if(node1.getBot() == node2)
			alreadyConnected = true;
		if(alreadyConnected) {
			DialogUtils.showMessage("Connection between selected nodes already exists.", "Connection Aborted");
			return;
		}
		// Determine connection port
		int width = PanMap.WIDTH;
		int height = PanMap.HEIGHT;
		int x1 = node1.getMapNode().x;
		int y1 = node1.getMapNode().y;
		int x2 = node2.getMapNode().x;
		int y2 = node2.getMapNode().y;
		
		boolean left = x1 > x2 + width;
		boolean right = x2 > x1 + width;
		boolean top = y1 > y2 + height;
		boolean bot = y2 > y1 + height;
		boolean TopOrBot = x2 < x1 + width && x1 < x2 + width;
		boolean LeftOrRight = y1 < y2 + height && y2 < y1 + height;
		
		if(LeftOrRight) {
			if(left) {
				// Check if port is taken
				if(node1.getLeft() != null || node2.getRight() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setLeft(node2);
				node2.setRight(node1);
			}
			else if(right) {
				// Check if port is taken
				if(node1.getRight() != null || node2.getLeft() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setRight(node2);
				node2.setLeft(node1);
			}
			else {
				DialogUtils.showMessage("Connection is not possible, try changing position", "Connection Aborted");
			}
		}
		else if(TopOrBot) {
			if(top) {
				// Check if port is taken
				if(node1.getTop() != null || node2.getBot() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setTop(node2);
				node2.setBot(node1);
			}
			else if(bot) {
				// Check if port is taken
				if(node1.getBot() != null || node2.getTop() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setBot(node2);
				node2.setTop(node1);
			}
			else {
				DialogUtils.showMessage("Connection is not possible, try changing position", "Connection Aborted");
			}
		}
		else {
			DialogUtils.showMessage("Connection is not possible, try changing position", "Connection Aborted");
		}
	}

	public static void disconnectNode(PanNode node1, PanNode node2) {
		// Check if these nodes are connected on some port
		int port = -1;
		if(node1.getLeft() == node2) port = 0;
		else if(node1.getRight() == node2) port = 1;
		else if(node1.getTop() == node2) port = 2;
		else if(node1.getBot() == node2) port = 3;
		else {
			DialogUtils.showMessage("Connection does not exist.", "No Connection Found");
			return;
		}
		
		// Removing path between them
		int waypointIndex1 = tour.remove(new Waypoint(node1, node2));
		int waypointIndex2 = tour.remove(new Waypoint(node2, node1));
		
		if(waypointIndex1 != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset graphics
			node2.getMapNode().setArrow(node1, false);
		}
		if(waypointIndex2 != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset graphics
			node1.getMapNode().setArrow(node2, false);
		}
		
		// update tourNum if path existed
		if(waypointIndex1 != TourPath.WAYPOINT_NOT_FOUND || waypointIndex2 != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset TourNum on every node
			PanNode node = head;
			while(node != null) {
				node.tourNum.clear();
				node = node.getNext();
			}
			
			// Assign new TourNum on every node
			tour.updateTourNumbers();
		}
		
		// null their connection
		switch(port) {
		case 0:
			node1.setLeft(null);
			node2.setRight(null);
			break;
		case 1:
			node1.setRight(null);
			node2.setLeft(null);
			break;
		case 2:
			node1.setTop(null);
			node2.setBot(null);
			break;
		case 3:
			node1.setBot(null);
			node2.setTop(null);
			break;
		default:
			break;
		}
	}
	
	public static void saveMap(String savePath, MapDrawFrame mapEditor, boolean auto) {
		// Background saving thread
		SwingWorker<Boolean, Integer> savingMap = new SwingWorker<Boolean, Integer>(){
			protected Boolean doInBackground() throws Exception {
				try {
					// show progress bar
					mapEditor.getProgressBar().setVisible(true);
					mapEditor.getProgressBar().setValue(0);
					mapEditor.getProgressBar().setMaximum(PanGraph.getNodeCount());
					// disable actions
					mapEditor.enableActions(false);
					// disable autosave
					AutoSave.stopSaving();
					
					// save
					FileOutputStream fos = new FileOutputStream(savePath);
					ObjectOutputStream out = new ObjectOutputStream(fos);
					
					// saving logic
					out.writeObject(head);
					out.writeObject(home);
					out.writeObject(name);
					out.writeObject(nodeCount);
					out.writeObject(graphSize);
					out.writeObject(tour);
					
					// saving icons
					int progress = 1;
					PanNode node = head;
					while(node != null) {
						out.writeObject(node.getMapNode().icon.getByteArray());
						node = node.getNext();
						publish(progress++);
					}
					
					// closing stream
					out.close();
					fos.close();
					
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			
			protected void process(List<Integer> progressData) {
				int latestData = progressData.get(progressData.size() - 1);
				mapEditor.getProgressBar().setValue(latestData);
			}
			
			// code that runs after doInBackground is finished
			protected void done() {
				try {
					if(!get()) {
						DialogUtils.showMessage("Could not save!", "Save");
						return;
					}	
					// update editor
					mapEditor.setTitle(savePath);
					mapEditor.enableActions(true);
					mapEditor.getProgressBar().setVisible(false);
					// set graph name
					name = savePath;
					
					// set autoload destination
					ConfigData.setLastUsedMap(savePath);
					// set autosave destination
					AutoSave.setSavingPath(savePath);
					// start autosave
					AutoSave.startSaving();
					
					if(!auto)
						DialogUtils.showMessage("Saved!", "Save");
				} 
				catch (InterruptedException ignore) {}
				catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		
		savingMap.execute();
	}
	
	public static boolean loadMap(String loadPath) {
		PanNode 		newHead;
		PanNode			newHome;
		int 			newNodeCount;
		PanGraphSize	newGraphSize;
		TourPath 		newTour;
		
		try {
			FileInputStream fin = new FileInputStream(loadPath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			newHead = (PanNode) ois.readObject();
			newHome = (PanNode) ois.readObject();
			name = (String) ois.readObject();
			newNodeCount = (int) ois.readObject();
			newGraphSize = (PanGraphSize) ois.readObject();
			newTour = (TourPath) ois.readObject();
			
			PanNode node = newHead;
			while(node != null) {
				byte[] iconData = (byte[]) ois.readObject();
				node.getMapNode().icon.init(iconData);
				node = node.getNext();
			}
			
			
			ois.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// path checking
		PanNode start = newHead;
		
		// Path to the last manually loaded image/audio file
		String lastImageLoc = null;
		String lastAudioLoc = null;
		String lastVideoLoc = null;
		
		// Path to the map directory
		int mapNameIndex = loadPath.lastIndexOf("\\");
		String mapLoc = loadPath.substring(0, mapNameIndex + 1);
		
		while(start != null) {
			File imageFile, audioFile, videoFile;
			
			imageFile = new File(start.getPanoramaPath());
			audioFile = (start.hasAudio()) ? new File(start.getAudioPath()) : null;
			videoFile = (start.hasVideo()) ? new File(start.getVideoPath()) : null;

			// Image Not Found
			if(!imageFile.exists()) {
				// get image name
				String imageName = StringUtils.getNameFromPath(start.getPanoramaPath());
				
				// creating path: mapLocation\Images\imageName
				File imageFile1 = new File(mapLoc.concat("\\Images\\").concat(imageName));
				
				// creating path: lastImageLocation\imageName
				File imageFile2 = null ;
				if(lastImageLoc != null) {
					int nameIndex = lastImageLoc.lastIndexOf("\\");
					imageFile2 = new File(lastImageLoc.substring(0, nameIndex + 1).concat(imageName));
				}
				
				String newPanoramaPath;
				if(imageFile1.exists()) {
					newPanoramaPath = imageFile1.getPath();
				}
				else if(imageFile2 != null && imageFile2.exists()) {
					newPanoramaPath = imageFile2.getPath();
				}
				else {
					// request replacement
					boolean replace = DialogUtils.replacePathDialog(imageFile.getPath());
					if(!replace) return false;
					
					// user requested replacement -> open image dialog 
					newPanoramaPath = lastImageLoc = DialogUtils.openImageDialog();
				}
				
				if(newPanoramaPath != null) {
					start.setPanoramaPath(newPanoramaPath);
				}
			}
			// Audio Not Found
			else if(audioFile != null && !audioFile.exists()) {
				// get audio name
				String audioName = StringUtils.getNameFromPath(audioFile.getPath());
				
				// creating path: mapLocation\Audio\audioName
				File audioFile1 = new File(mapLoc.concat("\\Audio\\").concat(audioName));
				
				// creating path: lastAudioLocation\audioName
				File audioFile2 = null ;
				if(lastAudioLoc != null) {
					int nameIndex = lastAudioLoc.lastIndexOf("\\");
					audioFile2 = new File(lastAudioLoc.substring(0, nameIndex + 1).concat(audioName));
				}
				
				String newAudioPath = null;
				if(audioFile1.exists()) {
					newAudioPath = audioFile1.getPath();
				}
				else if(audioFile2 != null && audioFile2.exists()) {
					newAudioPath = audioFile2.getPath();
				}
				else {
					// replace manually
					boolean replace = DialogUtils.replacePathDialog(audioFile.getPath());
					if(!replace) {
						// user requested replacement -> open audio dialog 
						newAudioPath = lastAudioLoc = DialogUtils.openAudioDialog();
					}
				}
				
				start.setAudio(newAudioPath);
			}
			// Video Not found
			else if(videoFile != null && !videoFile.exists()) {
				String videoName = StringUtils.getNameFromPath(videoFile.getPath());
				
				// creating path: mapLocation\Video\videoName
				File videoFile1 = new File(mapLoc.concat("\\Video\\").concat(videoName));
				
				// creating path: lastVideoLocation\videoName
				File videoFile2 = null ;
				if(lastVideoLoc != null) {
					int nameIndex = lastVideoLoc.lastIndexOf("\\");
					videoFile2 = new File(lastVideoLoc.substring(0, nameIndex + 1).concat(videoName));
				}
				
				String newVideoPath = null;
				if(videoFile1.exists()) {
					newVideoPath = videoFile1.getPath();
				}
				else if(videoFile2 != null && videoFile2.exists()) {
					newVideoPath = videoFile2.getPath();
				}
				else {
					// replace manually
					boolean replace = DialogUtils.replacePathDialog(videoFile.getPath());
					if(replace) {
						// user requested replacement -> open video dialog 
						newVideoPath = lastVideoLoc = DialogUtils.openVideoDialog();
					}
				}
				
				start.setVideoPath(newVideoPath);
			}
			// Everything is OK
			else {
				start = start.getNext();	
			}
		}
		
		// set autoload destination
		ConfigData.setLastUsedMap(loadPath);
				
		// set autosave destination
		AutoSave.setSavingPath(loadPath);
		
		// success
		head = newHead;
		home = newHome;
		name = loadPath;
		nodeCount = newNodeCount;
		graphSize = newGraphSize;
		graphSize.updateSize();
		tour = newTour;
		
		return true;
	}
	
	public static void genPath() {
		// empty map
		if(head == null) return;
		
		// clear prev path
		if(tour.hasPath()) clearPath();
		
		// generate id path
		NodeList graph = new NodeList(home);
		int p[] = graph.generatePath();
		
		// create tour and set graphics
		PanNode node;
		PanNode next;
		
		node = getNode(p[0]);
		for(int i = 1; i < p.length; i++) {
			next = getNode(p[i]);
			
			// Adding to path
			int tourNum = tour.add(new Waypoint(node, next));
			
			// Add tourNum
			node.tourNum.add(tourNum);
			// On last waypoint add final tourNum
			if(i == p.length - 1) next.tourNum.add(tourNum + 1);
			
			// Set graphics
			next.getMapNode().setArrow(node, true);
			
			node = next;
		}
	}
	
	public static void clearPath() {
		tour.clearPath();
		
		PanNode node = head;
		while(node != null) {
			node.tourNum.clear();
			node.getMapNode().clearArrows();
			
			node = node.getNext();
		}
	}
	
	public static void addWaypoint(PanNode node1, PanNode node2) {
		// Check if nodes are connected
		boolean connected = false;
		if(node1.getLeft() == node2)
			connected = true;
		else if(node1.getRight() == node2)
			connected = true;
		else if(node1.getTop() == node2)
			connected= true;
		else if(node1.getBot() == node2)
			connected = true;
		if(!connected) {
			DialogUtils.showMessage("Connection between selected nodes does not exist.", "Path Creation Aborted");
			return;
		}
		
		// Adding to path
		boolean success = tour.addWithCheck(new Waypoint(node1, node2));
		
		// Path already exists
		if(!success) {
			DialogUtils.showMessage("Path between selected nodes already exist.", "Path Creation Aborted");
			return;
		}
		
		// Reset TourNum on every node
		PanNode node = head;
		while(node != null) {
			node.tourNum.clear();
			node = node.getNext();
		}
		
		// Assign new TourNum on every node
		tour.updateTourNumbers();

		// Set graphics
		node2.getMapNode().setArrow(node1, true);
	}
	
	public static void removeWaypoint(PanNode node1, PanNode node2) {
		// Check if path exists
		if(!tour.hasPath()) {
			DialogUtils.showMessage("Path does not exist.", "Path Deletion Aborted");
			return;
		}
		
		// Check if nodes are connected
		boolean connected = false;
		if(node1.getLeft() == node2)
			connected = true;
		else if(node1.getRight() == node2)
			connected = true;
		else if(node1.getTop() == node2)
			connected= true;
		else if(node1.getBot() == node2)
			connected = true;
		if(!connected) {
			DialogUtils.showMessage("Connection between selected nodes does not exist.", "Path Deletion Aborted");
			return;
		}
		
		// Removing from path
		int tourNum = tour.remove(new Waypoint(node1, node2));
		if(tourNum != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset graphics
			node2.getMapNode().setArrow(node1, false);	
			
			// Reset TourNum on every node
			PanNode node = head;
			while(node != null) {
				node.tourNum.clear();
				node = node.getNext();
			}
			
			// Assign new TourNum on every node
			tour.updateTourNumbers();
		}
	}
	
	public static void setTextMode(boolean b) {
		textMode = b;
	}
	
	public static void encryptMap(String key, MapDrawFrame mapEditor) {
		// encryption canceled
		if(key == null) return;
		
		// defining background thread for encryption
		SwingWorker<Boolean, Integer> encryptMap = new SwingWorker<Boolean, Integer>(){
			// encryption logic
			protected Boolean doInBackground() throws Exception {
				try {
					// show progress bar
					mapEditor.getProgressBar().setVisible(true);
					mapEditor.getProgressBar().setValue(0);
					mapEditor.getProgressBar().setMaximum(PanGraph.getNodeCount());
					int progress = 1;
					// disable actions
					mapEditor.enableActions(false);
					// disable autosave
					AutoSave.stopSaving();
					
					PanNode node = head;
					while(node != null) {
						// check node encryption status
						// node is of type .pimg
						if(ImageCipher.isEncrypted(node.getPanoramaPath())) {
							// re-encrypt node with new key
							ImageCipher.imageReEncrypt(node.getPanoramaPath(), key);
						}
						// node is not yet encrypted
						else if (!ImageCipher.isEncrypted(node.getPanoramaPath())){
							// encrypt node
							String newPanPath = ImageCipher.imageEncrypt(node.getPanoramaPath(), key);
							// update path to encrypted image path
							node.setPanoramaPath(newPanPath);
						}
						
						// update progress
						publish(progress++);
						
						// get next node
						node = node.getNext();
					}
				} catch (Exception e) {
					// encryption failed (abort)
					e.printStackTrace();
					return false;
				}
				
				// map encrypted
				return true;
			}
			
			// updates progress bar
			protected void process(List<Integer> encryptedCount) {
				int latestCount = encryptedCount.get(encryptedCount.size() - 1);
				mapEditor.getProgressBar().setValue(latestCount);
			}
			
			// code that runs after doInBackground is finished
			protected void done() {
				try {
					// get encryption result
					boolean success = get();
					
					// display message
					if(success) 
						DialogUtils.showMessage("Encryption is successfull!", "Encryption");
					else 
						DialogUtils.showMessage("Encryption failed!", "Encryption");
					
					// udate editor
					mapEditor.getProgressBar().setVisible(false);
					mapEditor.enableActions(true);
					// start autosave
					AutoSave.startSaving();
				} 
				catch (InterruptedException ignore) {}
				catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		
		// encrypt map
		encryptMap.execute();
	}
	
	/* class related functionality */
	
	public static PanNode getHead() {
		return head;
	}
	
	public static void setHead(PanNode node) {
		head = node;
	}
	
	private static void setTail(PanNode newTail) {
		PanNode tail = head;
		
		while(tail.getNext() != null)
			tail = tail.getNext();
		
		tail.setNext(newTail);
	}
	
	public static PanNode getHome() {
		return home;
	}
	
	public static void removeMap() {
		head = null;
		home = null;
		name = DEFAULT_NAME;
		nodeCount = 0;
		
		tour = new TourPath();
		graphSize = new PanGraphSize();
	}
	
	public static PanNode getNode(int id) {
		PanNode node = head;
		while(id != 0) {
			node = node.getNext();
			id--;
		}
		
		return node;
	}

	public static boolean hasTour() {
		return tour.hasPath();
	}
	
	public static PanNode[] getTour() {
		return tour.getPath();
	}
	
	public static PanGraphSize getGraphSize() {
		return graphSize;
	}

	public static String getName() {
		if(name == null) return DEFAULT_NAME;
		
		return name;
	}
	
	public static void setName(String newName) {
		name = newName;
	}	
	
	public static boolean isEmpty() {
		return head == null;
	}

	public static boolean isTextMode() {
		return textMode;
	}

	public static int getNodeCount() {
		return nodeCount;
	}
}
