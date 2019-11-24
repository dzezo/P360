package videoPlayer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class VideoPlayer extends JFrame {

    public VideoPlayer() {
         //PlayerPanel player = new PlayerPanel();
         this.setTitle("Swing Video Player");
         this.setDefaultCloseOperation(EXIT_ON_CLOSE);
         this.setLayout(new BorderLayout());
         this.setSize(640, 480);
         this.setLocationRelativeTo(null);
         //this.add(player, BorderLayout.CENTER);
         this.validate();
         this.setVisible(true);

         //player.play("http://174.132.240.162:8000/;stream.nsv");
    }
    
}
