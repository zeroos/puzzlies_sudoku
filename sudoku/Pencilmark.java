package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */
public class Pencilmark {
    MyPreferences pref;

    protected int value;
    protected Color coloring;
    
    public Pencilmark(int value){
        pref = MyPreferences.getInstance();
        this.value = value;
    }

    public Color getColoring() {
        return coloring;
    }

    public void setColoring(Color coloring) {
        this.coloring = coloring;
    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    public void paintComponent(Graphics g, int x, int y, int w, int h){
        paintPencilmark(g, x, y, w, h, value, coloring, true);
    }
    public static void paintPencilmark(Graphics g, int x, int y, int w, int h, int value, Color coloring, boolean isActive){
        MyPreferences pref = MyPreferences.getInstance();
        if(coloring != null){
            g.setColor(coloring);
            g.fillOval(x+1, y+1, w-1, h-1);
        }
        
        if(value != 0){
            Font font;
            String str = Integer.toString(value);
            int fontSize = (int)(h*0.9)+1;
            int strWidth = 0;
            int prevStrWidth = 1;
            do{
                fontSize--;
                font = new Font(Font.SANS_SERIF, isActive?Font.BOLD:Font.PLAIN, fontSize);
                if(strWidth==prevStrWidth) break;
                prevStrWidth = strWidth;
                g.setFont(font);
            }while((strWidth = g.getFontMetrics().stringWidth(str)) > 0.9*w);
            //font = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
            //g.setFont(font);
            //strWidth = g.getFontMetrics().stringWidth(str);

            if(isActive)
                g.setColor(new Color(pref.getInt("activePencilmarkColor")));
            else
                g.setColor(new Color(pref.getInt("inactivePencilmarkColor")));

            g.setFont(font);
            g.drawString(str, x+w/2-strWidth/2, y+h/2 + fontSize/2);
        }
    }
}
