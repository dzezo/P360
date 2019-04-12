package panorama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import glRenderer.Scene;
import graph.NodeList;
import utils.ChooserUtils;
import utils.DialogUtils;

@SuppressWarnings("serial")
public class PanNode implements Serializable {
	// graf realizovan kao lista suseda
	private static PanNode head;
	private static PanNode home;
	private PanNode next;
	
	// putanja
	public static TourPath tour = new TourPath();
	public transient boolean visited = false;
	
	// id cvora i njegova graficka reprezentacija
	private int drawNum = Integer.MAX_VALUE;
	private MapNode mapNode;
	
	// susedi cvora
	private PanNode left;
	private PanNode right;
	private PanNode top;
	private PanNode bot;
	
	// slika i putanja do slike
	private transient Panorama panorama;
	private String panoramaPath;
	
	// audio zapis
	private PanAudio audio;
	
	
	/**
	 * Konstruktor za panoramu
	 * @param panoramaPath - file system putanja do panorame
	 */
	public PanNode(String panoramaPath) {
		this.panoramaPath = panoramaPath;
	}
	
	/**
	 * Konstruktor za panoramu koja ima svoju graficku reprezentaciju na mapi
	 * @param panoramaPath - file system putanja
	 * @param x, y - koordinate na mapi
	 */
	public PanNode(String panoramaPath, int x, int y) {
		this.panoramaPath = panoramaPath;		
		mapNode = new MapNode(this, x, y);
	}
	
	public void loadPanorama() {
		if(panorama == null)
			panorama = new Panorama(panoramaPath);
	}
	
	public boolean isHome() {
		if (home.equals(this)) 
			return true;
		else
			return false;
	}
	
	public boolean isActive() {
		PanNode activePano = Scene.getActivePanorama();
		if(activePano != null && activePano.equals(this))
			return true;
		else
			return false;
	}
	
	/* map creation functionality */
	
	public static void addNode(String panoramaPath, int originX, int originY) {
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
	}
	
	public static void deleteNode(PanNode selectedPanorama) {
		// Remove his connections
		if(selectedPanorama.getLeft() != null) {
			selectedPanorama.getLeft().setRight(null);
			selectedPanorama.setLeft(null);
		}
		if(selectedPanorama.getRight() != null) {
			selectedPanorama.getRight().setLeft(null);
			selectedPanorama.setRight(null);
		}
		if(selectedPanorama.getTop() != null) {
			selectedPanorama.getTop().setBot(null);
			selectedPanorama.setTop(null);
		}
		if(selectedPanorama.getBot() != null) {
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
		int width = MapNode.width;
		int height = MapNode.height;
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
		// if they are then null their connection, else show message
		if(node1.getLeft() == node2) {
			node1.setLeft(null);
			node2.setRight(null);
		}
		else if(node1.getRight() == node2) {
			node1.setRight(null);
			node2.setLeft(null);
		}
		else if(node1.getTop() == node2) {
			node1.setTop(null);
			node2.setBot(null);
		}
		else if(node1.getBot() == node2) {
			node1.setBot(null);
			node2.setTop(null);
		}	
		else {
			DialogUtils.showMessage("Connection does not exist.", "No Connection Found");
		}
	}
	
	public static void addAudio(PanNode selectedNode, String audioPath) {
		// find selected node
		PanNode node = head;
		while(!node.equals(selectedNode)) {
			node = node.getNext();
		}
		
		// set audio
		node.audio = new PanAudio(audioPath);
		
		// set graphic representation
		node.mapNode.audioName = node.mapNode.setNameFromPath(audioPath);
	}
	
	public static void removeAudio(PanNode selectedNode) {
		// find selected node
		PanNode node = head;
		while(!node.equals(selectedNode)) {
			node = node.getNext();
		}
		
		// remove audio
		if(node.isAudioPlaying())
			node.stopAudio();
		node.audio = null;
		
		// remove graphic representation
		node.mapNode.audioName = null;
	}
	
	public static boolean saveMap(String savePath) {
		// Writing to file
		try {
			FileOutputStream fs = new FileOutputStream(savePath);
			ObjectOutputStream oos = new ObjectOutputStream(fs);
			oos.writeObject(head);
			oos.writeObject(home);
			oos.writeObject(tour);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// success
		return true;
	}
	
	public static boolean loadMap(String loadPath) {
		try {
			FileInputStream fin = new FileInputStream(loadPath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			head = (PanNode) ois.readObject();
			home = (PanNode) ois.readObject();
			tour = (TourPath) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// path checking
		PanNode start = head;
		while(start != null) {
			File imageFile, audioFile;
			
			imageFile = new File(start.getPanoramaPath());
			audioFile = (start.hasAudio()) ? new File(start.getAudioPath()) : null;

			if(!imageFile.exists()) {
				boolean replace = DialogUtils.replacePathDialog(imageFile.getPath());
				if(!replace) return false;
				
				// user requested replacement -> open image dialog 
				String newPanoramaPath = ChooserUtils.openImageDialog();
				if(newPanoramaPath != null) 
					start.setPanoramaPath(newPanoramaPath);
			}
			else if(audioFile != null && !audioFile.exists()) {
				boolean replace = DialogUtils.replacePathDialog(audioFile.getPath());
				if(!replace) return false;
				
				// user requested replacement -> open audio dialog 
				String newAudioPath = ChooserUtils.openAudioDialog();
				if(newAudioPath != null) 
					start.setAudioPath(newAudioPath);
			}
			else {
				start = start.getNext();
			}
		}
		
		// success
		return true;
	}
	
	public static void genPath() {
		// empty map
		if(head == null) return;
		
		// clear prev path
		if(tour.hasPath()) clearPath();
		
		// generate id path
		NodeList graph = new NodeList(head, home);
		int p[] = graph.generatePath();
		
		// create tour and set graphics
		PanNode node;
		PanNode next;
		
		node = getNode(p[0]);
		for(int i = 1; i < p.length; i++) {
			next = getNode(p[i]);
			tour.add(new Waypoint(node, next));
			
			// set graphics
			next.mapNode.setArrow(node, true);
			
			node = next;
		}
	}
	
	public static void clearPath() {
		tour.clearPath();
		
		PanNode node = head;
		while(node != null) {
			node.mapNode.clearArrows();
			node = node.next;
		}
	}
	
	public static void addToPath(PanNode node1, PanNode node2) {
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
		tour.add(new Waypoint(node1, node2));
		
		// Set graphics
		node2.mapNode.setArrow(node1, true);
	}
	
	public static void removeFromPath(PanNode node1, PanNode node2) {
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
		tour.remove(new Waypoint(node1, node2));
		
		// Reset graphics
		node2.mapNode.setArrow(node1, false);	
	}
	
	/* audio control */
	
	public void playAudio() {
		this.audio.play();
	}
	
	public void pauseAudio() {
		this.audio.pause();
	}
	
	public void stopAudio() {
		this.audio.stop();
	}
	
	public void setAudioPath(String newAudioPath) {
		audio.setAudioPath(newAudioPath);
	}
	
	public String getAudioPath() {
		if(audio != null)
			return audio.getAudioPath();
		return null;
	}
	
	public boolean hasAudio() {
		if(audio == null)
			return false;
		return true;
	}
	
	public boolean isAudioPlaying() {
		if(audio != null)
			return audio.isPlaying();
		return false;
	}
	
	/* getters and setters */
	
	// list related
	public static PanNode getHead() {
		return head;
	}
	
	public static void setHead(PanNode node) {
		head = node;
	}
	
	public static void setTail(PanNode newTail) {
		PanNode tail = head;
		while(tail.next!=null)
			tail = tail.next;
		tail.next = newTail;
	}
	
	public static PanNode getHome() {
		return home;
	}
	
	public static void removeMap() {
		head = null;
		home = null;
		tour.clearPath();
	}
	
	public static PanNode getNode(int id) {
		PanNode node = head;
		while(id != 0) {
			node = node.next;
			id--;
		}
		
		return node;
	}
	
	// node related
	public int getID() {
		return this.drawNum;
	}
	
	public void setDrawNum(int i) {
		this.drawNum = i;
	}
	
	public int getDrawNum() {
		return this.drawNum;
	}

	public MapNode getMapNode() {
		return mapNode;
	}
	
	public Panorama getPanorama() {
		return panorama;
	}
	
	public void setPanorama(Panorama panorama) {
		this.panorama = panorama;
	}
	
	public String getPanoramaPath() {
		return panoramaPath;
	}
	
	public void setPanoramaPath(String panoramaPath) {
		this.panoramaPath = panoramaPath;
		mapNode.panName = mapNode.setNameFromPath(panoramaPath);
	}
	
	public PanNode getNext() {
		return next;
	}
	
	public void setNext(PanNode next) {
		this.next = next;
	}
	
	public PanNode getLeft() {
		return left;
	}
	
	public void setLeft(PanNode left) {
		this.left = left;
	}
	
	public PanNode getRight() {
		return right;
	}
	
	public void setRight(PanNode right) {
		this.right = right;
	}
	
	public PanNode getTop() {
		return top;
	}
	
	public void setTop(PanNode top) {
		this.top = top;
	}
	
	public PanNode getBot() {
		return bot;
	}
	
	public void setBot(PanNode bot) {
		this.bot = bot;
	}

	public PanAudio getAudio() {
		return this.audio;
	}

}