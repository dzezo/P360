package glRenderer;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import panorama.PanNode;

public class TourManager implements Runnable{
	private static ScheduledThreadPoolExecutor tour = new ScheduledThreadPoolExecutor(1);
	private static ScheduledFuture<?> tourTasks;
	
	private static PanNode[] path;
	private static boolean hasPath;
	private static boolean nextPano;
	private static int pathLocation;
	
	private static long timeOfChange; 
	private static boolean changeComplete;
	private static long visitedPanTime = 3000;
	
	private static boolean touring = false;
	
	/**
	 * Funkcija se poziva ukoliko ucitana/izmenjena mapa poseduje putanju.
	 * Ukoliko se startni cvor nalazi na putanji, putanja je validna, otvara se nova nit koja izvrsava
	 * run metodu ove klase.
	 * @param p je putanja
	 * @param start je polazni cvor na putanji
	 */
	public static void init(PanNode[] p, PanNode start) {
		path = new PanNode[p.length];
		
		pathLocation = -1;
		for(int i = 0; i < p.length; i++) {
			// panorama na putanji odgovara startnom cvoru i lokacija startnog cvora nije nadjena
			if(start.equals(p[i]) && pathLocation == -1) {
				// postavi indeks odakle pocinje putanja
				pathLocation = i;
			}
			
			// dodaj panorame na putanju
			path[i] = p[i];
			path[i].visited = false;
		}
		
		// ukoliko postoji pocetak putanje, pokreni turu
		if(pathLocation != -1){
			hasPath = true;
			
			// resetuj audio menadzer
			AudioManager.resetAudioPlayed();
			
			// resetuj kameru pre pocetka ture
			Scene.getCamera().resetTripMeter();
			timeOfChange = System.currentTimeMillis();
			changeComplete = true;
			
			// pokreni turua
			tourTasks = tour.scheduleAtFixedRate(new TourManager(), 0, 50, TimeUnit.MILLISECONDS);
		}
		else {
			hasPath = false;
		}
	}
	
	/**
	 * Funkcija koja ispituje da li su zadovoljeni uslovi za prelazak na sledecu panoramu
	 */
	public void run() {
		// ukoliko je automatsko paniranje iskljuceno 
		// ili se nije postavila nova panorama na scenu
		if(!Scene.getCamera().isAutoPanning() || !changeComplete) {
			// sacekaj
			touring = false;
			return;
		}
		
		touring = true;
		
		PanNode activePano = Scene.getActivePanorama();
		
		// da li je tokom ture doslo do rucne promene panorame
		if(!path[pathLocation].equals(activePano)) {
			// nadji gde se na putanji nalazi nova panorama
			for(int i = 0; i < path.length; i++) {
				if(activePano.equals(path[i])) {
					pathLocation = i;
					break;
				}
			}
			
			// resetuj audio menadzer
			AudioManager.resetAudioPlayed();
			
			// resetuj kameru
			Scene.getCamera().resetTripMeter();
			timeOfChange = System.currentTimeMillis();
			changeComplete = true;
			
			return;
		}
		
		// da li je panorama vec posecena, ukoliko jeste ispitaj uslov za prelaz na sledecu
		if(activePano.visited) {
			// na sledecu panoramu se prelazi ukoliko je isteklo dozvoljeno vreme za vec posecenu panoramu
			long currentTime = System.currentTimeMillis();
			if((currentTime - timeOfChange) > visitedPanTime) {
				changeComplete = false;
				nextPano = true;
			}
			
			return;
		}
		
		// ukoliko je kamera napravila pun krug i naracija je zavrsena
		if(Scene.getCamera().cycleComplete() && !activePano.isAudioPlaying()) {
			// oznaci da je panorama posecena i predji na sledecu
			activePano.visited = true;
			
			changeComplete = false;
			nextPano = true;
		}
	}
	
	/**
	 * @return je true ukoliko mapa sadrzi validnu putanju, a putanja je validna ukoliko se na njoj nalazi
	 * startni (home) cvor
	 */
	public static boolean hasPath() {
		return hasPath;
	}
	
	/**
	 * @return je true ukoliko je ispunjen uslov da se predje na sledecu panoramu na putanji
	 */
	public static boolean nextPano() {
		if(nextPano) {
			nextPano = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Prelazi na sledeci cvor na putanji
	 */
	public static void goNextPano() {
		pathLocation = (pathLocation + 1)%path.length;
		PanNode nextPano = path[pathLocation];
		
		// preskoci poslednju panoramu ukoliko je ista kao i prva
		if(pathLocation == path.length - 1 
				&& path[0].equals(path[pathLocation])) 
		{
			pathLocation ^= pathLocation;
			nextPano = path[pathLocation];
		}
		
		// resetuj visited flag ukoliko se ciklus zavrsio
		if(pathLocation == 0) {
			int i = 0;
			while(i != path.length) {
				path[i++].visited = false;
			}
		}
		
		// predji na sledeci cvor
		Scene.loadNewImage(nextPano);
		
		// resetuj audio menadzer
		AudioManager.resetAudioPlayed();
		
		// resetuj kameru
		Scene.getCamera().resetTripMeter();
		timeOfChange = System.currentTimeMillis();
		changeComplete = true;
	}
	
	/**
	 * @return true ukoliko menadzer obilazi putanju
	 */
	public static boolean isTouring() {
		return touring;
	}
	
	public static void stopTourManager() {
		hasPath = false;
		if(tourTasks != null)
			tourTasks.cancel(false);
	}
}
