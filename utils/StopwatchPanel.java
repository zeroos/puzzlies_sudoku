package utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

/**
 *
 * @author zeroos
 */
public class StopwatchPanel extends JPanel{
    Stopwatch stopwatch;
    MyPreferences pref;
    
    StopwatchPanel(Stopwatch stopwatch){
        this.pref = MyPreferences.getInstance();
        this.stopwatch = stopwatch;
        Dimension d = new Dimension(100,20);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        
        
        (new Timer()).schedule(new TimerTask(){
            @Override
            public void run() {
                repaint();
            }
            
        }, 0, 1000);
    }
    @Override
    public void paintComponent(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(pref.getInt("inactivePencilmarkColor")));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        
        String clock;
        int sec = (int) (stopwatch.getTime()/1000);
        int min = sec/60;
        int hours = min/60;
        
        if(hours>=1){
            clock = String.format("%d:%02d:%02d", hours,
                min%60, sec%60);
        }else{
            clock = String.format("%02d:%02d", min%60, 
                sec%60);
        }
        g.drawString(clock, 0, 15);
    }
}
