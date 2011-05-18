package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */
public class Cell extends JPanel{
    public static final int NORMAL = 0;
    public static final int GIVEN = 1;
    public static final int UNKNOWN = 0;
    public static final int INCORRECT = 1;
    
    EventListenerList changeListenerList = new EventListenerList();
    
    int type = NORMAL;
    int state = UNKNOWN;
    int value = 0;
    int maxValue = 100;
    MyPreferences pref;
    Color backgroundColor = null;
    protected Color coloring;
    HashMap<Integer, Pencilmark> pencilmarks = new HashMap<Integer, Pencilmark>();

    
    public Cell(){
        pref = MyPreferences.getInstance();
    }
    
    public boolean hasPencilmark(int value){
        return pencilmarks.containsKey(value);
    }
    public void addPencilmark(int value){
        addPencilmark(new Pencilmark(value));
    }
    public void addPencilmark(Pencilmark p){
        pencilmarks.put(p.getValue(), p);
    }
    public void delPencilmark(int value){
        pencilmarks.remove(value);
    }
    
    
    public void addPencilmarks(String data){
        for(int i=0; i<data.length(); i++){
            int val = Integer.parseInt(String.valueOf(data.charAt(i)), Character.MAX_RADIX);
            addPencilmark(new Pencilmark(val));
        }
    }
    public HashMap<Integer, Pencilmark> getPencilmarks(){
        return pencilmarks;
    }
    
    public Color getColoring() {
        return coloring;
    }

    public void setPencilmarkColoring(int value, Color color){
        Pencilmark p = pencilmarks.get(value);
        if(p==null) return;
        p.setColoring(color);
    }
    public Color getPencilmarkColoring(int value){
        Pencilmark p = pencilmarks.get(value);
        if(p==null) return null;
        return p.getColoring();
    }
    
    public void setColoring(Color coloring) {
        this.coloring = coloring;
    }
    public void resetColoring(Color coloring) {
        //sets coloring to none
        this.coloring = null;
    }
    
    public void setMaxValue(int value){
        if(value<maxValue){
            maxValue = value;
        }
    }
    public int getMaxValue(){
        return maxValue;
    }
    
    public void setBackgroundColor(Color backgroundColor){
        this.backgroundColor = backgroundColor;
    }
    public Color getBackgroundColor(){
        return backgroundColor;
    }
    public void resetBackgroundColor(){
        /* resets backgroundColor to default value */
        backgroundColor = null;
    }
    
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }
    public void setState(int state){
        if(state == this.state) return;
        this.state = state;
    }
    public int getState(){
        return state;
    }
    
    
    public void setValue(int v){
        if(v>getMaxValue()) System.err.println("WARNING: value greater than maxValue for this cell.");
        if(getType() == GIVEN && value != 0) return;
        value = v;
        fireChangeEvent();
    }
    public int getValue(){
        return value;
    }
    
    public int getClickedPencilmarkValue(int clickedX, int clickedY){
        /* returns clicked Pencilmark. clickedX and clickedY are values
         * related to the begining of THIS CELL.
         */
        int gridSize = (int) Math.ceil(Math.sqrt(maxValue));
        int pencilmarkWidth = (int)Math.ceil(pref.getInt("fieldW")/(float)gridSize);
        int pencilmarkHeight = (int)Math.ceil(pref.getInt("fieldH")/(float)gridSize);
        int x = clickedX/pencilmarkWidth;
        int y = clickedY/pencilmarkHeight;
        return y*gridSize + x + 1;
    }
    
    public void paintComponent(Graphics g, int x, int y){
        paintComponent(g, x, y, pref.getInt("fieldW"), pref.getInt("fieldH"));
    }   
    public void paintComponent(Graphics g, int x, int y, int w, int h){
        paintBackground(g, x, y, w, h);
        paintForeground(g, x, y, w ,h);
    }
    
    public void paintBackground(Graphics g, int x, int y){
        paintBackground(g, x, y, pref.getInt("fieldW"), pref.getInt("fieldH"));
    }
    public void paintBackground(Graphics g, int x, int y, int w, int h){
        if(coloring != null){
            g.setColor(coloring);
        }else if(backgroundColor != null){
            g.setColor(backgroundColor);
        }else{
            g.setColor(new Color(pref.getInt("fieldColor")));
        }
        g.fillRect(x, y, w, h);
    }
    public void paintForeground(Graphics g, int x, int y){
        paintForeground(g, x, y, pref.getInt("fieldW"), pref.getInt("fieldH"));
    }
    public void paintForeground(Graphics g, int x, int y, int w, int h){
        g.setColor(new Color(pref.getInt("lineColor")));
        g.drawRect(x, y, w, h);
        
        if(value != 0){
            Font font;
            String str = Integer.toString(value);
            int fontSize = (int)(h*0.65)+1;
            int strWidth = 0;
            int prevStrWidth = 1;
            do{
                fontSize--;
                font = new Font(Font.SANS_SERIF, (type==GIVEN)?Font.BOLD:Font.PLAIN, fontSize);
                g.setFont(font);
                if(strWidth==prevStrWidth) break;
                prevStrWidth = strWidth;
            }while((strWidth = g.getFontMetrics().stringWidth(str)) > 0.9*w);
            if(state == INCORRECT){
                g.setColor(new Color(pref.getInt("warningColor")));
            }else if(type == GIVEN){
                g.setColor(new Color(pref.getInt("givenValueColor")));
            }else{
                g.setColor(new Color(pref.getInt("cellValueColor")));
            }
            g.drawString(str, x+w/2-strWidth/2, y+h/2 + fontSize/2);
        }else{
            //draw pencilmarks
            int gridSize = (int) Math.ceil(Math.sqrt(maxValue));
            int pencilmarkWidth = w/gridSize;
            int pencilmarkHeight = h/gridSize;
            for(int i=1; i<=maxValue; i++){
                int px = x + ((i-1)%gridSize)*pencilmarkWidth;
                int py = y + ((i-1)/gridSize)*pencilmarkHeight;
                if(pencilmarks.containsKey(i) && pencilmarks.get(i) != null){
                    Pencilmark p = pencilmarks.get(i);
                    p.paintComponent(g, px, py, pencilmarkWidth, pencilmarkHeight);
                }else{
                    Pencilmark.paintPencilmark(g, px, py, pencilmarkWidth, pencilmarkHeight, i, null, false);
                }
            }
        }
    }
    
    public void addChangeListener(ChangeListener l) {
        changeListenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeListenerList.remove(ChangeListener.class, l);
    }

    public void fireChangeEvent() {
        ChangeListener listeners[] =
                changeListenerList.getListeners(ChangeListener.class);
        //int state = UNKNOWN;//if at least one listener sets state to different value, remember this value
        for (ChangeListener l : listeners) {
            l.stateChanged(new ChangeEvent(this));
            //if(this.state != UNKNOWN) state = this.state;
        }
        //this.state = state;
    }
}
