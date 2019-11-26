package videoPlayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicSliderUI;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;
import utils.DialogUtils;

public class VideoPlayer {

	private final JFrame frame;
	
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	private String videoPath;
	
	private JLayeredPane contentPane;
	private JPanel playerPane;
	private JPanel controlsPane;
	
	private JButton playPauseButton;
	private JButton fullScreenButton;
	
	private JSlider timeSlider;
	private boolean timeSeeking;
	
	private Timer hideTimer;
	private Timer fullScreenTimer;
	private boolean doubleClick;
	
	private BufferedImage cursorImg;
	private Cursor blankCursor;
	
    public VideoPlayer() {
    	frame = new JFrame("Media Player");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// clean up
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mediaPlayerComponent.release();
				frame.dispose();
			}
		});
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent(
				null,
				null,
				new AdaptiveFullScreenStrategy(frame),
				null,
				null);
		
		mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			// finished event se triggeruje kada se playback zavrsi.
			public void finished(MediaPlayer mediaPlayer) {
				// Nije dozvoljen libVLC callback preko event niti, jer moze da dovede do nepredvidivog ponasanja JVM-a
				// Potrebno je napraviti asinhroni egzekutor koji nudi MediaPlayer objekat koji se prosledjuje listeneru.
				mediaPlayer.submit(new Runnable() {
					public void run() {
						mediaPlayerComponent.mediaPlayer().media().play(videoPath);
					}
				});
			}
			// play() zahteva pokretanje i odmah se vraca, a libVLC ce asinhrono probati da pokrene video.
			// Ovo znaci da ce success/error biti indikovan sa zakasnjenjem, i ovo je event koji to hvata.
			public void error(MediaPlayer mediaPlayer) {
				// Ukoliko event handler menja gui onda to moraju uraditi na Swing Event Dispatch Thread-u
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DialogUtils.showMessage("Failed to play: " + videoPath, "Error");
					}	
				});
			}
			public void positionChanged(MediaPlayer mp, float f) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(!timeSeeking) {
							int iPos = (int)(f * 100.0);
				            timeSlider.setValue(iPos);
						}
					}	
				});
	        }
		});
		
		Component videoSurface = mediaPlayerComponent.videoSurfaceComponent();
		videoSurface.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if(!controlsPane.isVisible())
					controlsPane.setVisible(true);
				frame.getContentPane().setCursor(Cursor.getDefaultCursor());
				hideTimer.restart();
			}
		});
		videoSurface.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(doubleClick) {
					mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
					doubleClick = false;
				}
				else {
					doubleClick = true;
				}
				fullScreenTimer.restart();
			}
		});
		
		playerPane = new JPanel();
		playerPane.setLayout(new BorderLayout());
		playerPane.add(mediaPlayerComponent, BorderLayout.CENTER);
		playerPane.setSize(new Dimension(600, 400));
		
		controlsPane = new JPanel();
		playPauseButton = new JButton("Pause");
		playPauseButton.setFocusable(false);
		controlsPane.add(playPauseButton);
		timeSlider = new JSlider();
		timeSlider.setFocusable(false);
		timeSlider.setUI(new BasicSliderUI(timeSlider) {
			protected TrackListener createTrackListener(JSlider slider) {
				return new TrackListener() {
					public boolean shouldScroll(int direction) {
						return false;
					}
				};
			}
		});
		timeSeeking = false;
		controlsPane.add(timeSlider);
		fullScreenButton = new JButton("Full Screen");
		fullScreenButton.setFocusable(false);
		controlsPane.add(fullScreenButton);
		controlsPane.setSize(controlsPane.getPreferredSize());
		controlsPane.setLocation(playerPane.getWidth() / 2 - controlsPane.getWidth() / 2, 
				playerPane.getHeight() - controlsPane.getHeight());
		
		playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(mediaPlayerComponent.mediaPlayer().status().isPlaying())
            		mediaPlayerComponent.mediaPlayer().controls().pause();
            	else
            		mediaPlayerComponent.mediaPlayer().controls().play();
            }
        });
		
		timeSlider.addMouseMotionListener(new MouseMotionAdapter() {		
			public void mouseDragged(MouseEvent arg0) {
				BasicSliderUI ui = (BasicSliderUI) timeSlider.getUI();
	            timeSlider.setValue(ui.valueForXPosition(arg0.getX()));
				mediaPlayerComponent.mediaPlayer().controls().setPosition((float) timeSlider.getValue() / 100);
		    }
		});
		
		timeSlider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				timeSeeking = true;
				BasicSliderUI ui = (BasicSliderUI) timeSlider.getUI();
	            timeSlider.setValue(ui.valueForXPosition(arg0.getX()));
	            mediaPlayerComponent.mediaPlayer().controls().setPosition((float) timeSlider.getValue() / 100);
			}
			
			public void mouseReleased(MouseEvent arg0) {
				timeSeeking = false;
			}	
		});
		
		fullScreenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
            }
        });
		
		
		contentPane = new JLayeredPane();
		contentPane.add(playerPane, new Integer(0));
		contentPane.add(controlsPane, new Integer(1));
		contentPane.setPreferredSize(playerPane.getSize());
		contentPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				playerPane.setSize(contentPane.getSize());
				controlsPane.setLocation(playerPane.getWidth() / 2 - controlsPane.getWidth() / 2, 
						playerPane.getHeight() - controlsPane.getHeight());
				contentPane.revalidate();
				contentPane.repaint();				
			}	
		});
		
		frame.setContentPane(contentPane);
		frame.pack();
		
		cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit()
				.createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		
		hideTimer = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(frame.isVisible() && controlsPane.isVisible()) {
					Rectangle controlsRect = controlsPane.getBounds();
					controlsRect.setLocation(controlsPane.getLocationOnScreen());
					if(!controlsRect.contains(MouseInfo.getPointerInfo().getLocation())) {	
						controlsPane.setVisible(false);
						frame.getContentPane().setCursor(blankCursor);
					}
				}
			}	
		});
		hideTimer.start();
		
		fullScreenTimer = new Timer(750, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doubleClick = false;
			}	
		});
		fullScreenTimer.start();
    }
    
    public void playVideo(String videoPath) {
    	this.videoPath = videoPath;
    	frame.setVisible(true);
    	
    	timeSlider.setValue(0);
    	
    	mediaPlayerComponent.mediaPlayer().fullScreen().set(true);
    	mediaPlayerComponent.mediaPlayer().media().play(videoPath);
    }
}
