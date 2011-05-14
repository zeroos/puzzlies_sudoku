package sudoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import utils.MyPreferences;
import sudoku.gridElements.GridElement;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class Grid extends JPanel{
    Controller controller;
    MyPreferences pref;
    boolean paused = false;
    
    int hlCol = -2;
    int hlRow = -2;
    
    Color coloringColor = null;
    
    public Grid(final Controller controller){
        this.controller = controller;
        
        pref = MyPreferences.getInstance();
        pref.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                recalculate();
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseMoved(MouseEvent e){
                if(paused) return;
                int x = e.getX() - pref.getInt("offsetX");
                int y = e.getY() - pref.getInt("offsetY");
                setHlCol((int)Math.floor((float)x/pref.getInt("fieldW")));
                setHlRow((int)Math.floor((float)y/pref.getInt("fieldH")));
            }
        });
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(paused){
                    unpause();
                    return;
                }
                final int offsetX = pref.getInt("offsetX");
                final int offsetY = pref.getInt("offsetY");
                final int fieldW = pref.getInt("fieldW");
                final int fieldH = pref.getInt("fieldH");
                int x = e.getX() - offsetX;
                int y = e.getY() - offsetY;
                final int fieldX = (int)Math.floor((float)x/fieldW);
                final int fieldY = (int)Math.floor((float)y/fieldH);
                
                if(fieldX >= controller.getData().getWidth() || fieldX < 0 ||
                   fieldY >= controller.getData().getHeight() || fieldY < 0){
                        return;
                }
                  
                
                final Cell c = controller.getData().getCell(fieldX, fieldY);
                final int value = c.getClickedPencilmarkValue(x - fieldX*fieldW, y-fieldY*fieldH);
                
                if(e.getButton() == MouseEvent.BUTTON1){
                    final int currentValue = c.getValue();
                    final Color currentColor = c.getColoring()==null?null:new Color(c.getColoring().getRGB());
                    final Color newColor = coloringColor==null?null:new Color(coloringColor.getRGB()).brighter();
                    controller.fireUndoableEditEventOccured(new UndoableEditEvent(this,
                        new AbstractUndoableEdit(){
                            @Override
                            public void undo(){
                                super.undo();
                                c.setValue(currentValue);
                                c.setColoring(currentColor);
                                repaint(offsetX+fieldX*fieldW, offsetY+fieldY*fieldH, fieldW, fieldH);
                            }
                            @Override
                            public void redo(){
                                super.redo();
                                if(newColor != null){
                                    if(newColor.equals(currentColor)){
                                        c.setColoring(null);
                                    }else{
                                        c.setColoring(newColor);
                                    }
                                }else{
                                    if(currentValue == 0){
                                        c.setValue(value);
                                    }else{
                                        c.setValue(0);
                                    }
                                }
                                repaint(offsetX+fieldX*fieldW, offsetY+fieldY*fieldH, fieldW, fieldH);
                            }
                        }
                    ));
                    if(newColor != null){
                        if(newColor.equals(currentColor)){
                            c.setColoring(null);
                        }else{
                            c.setColoring(newColor);
                        }
                    }else{
                        if(c.getValue() == 0){
                            c.setValue(value);
                        }else{
                            c.setValue(0);
                        }
                    }
                }else if(e.getButton() == MouseEvent.BUTTON3){
                    final boolean hasPencilmark = c.hasPencilmark(value);
                    final Color newColor = coloringColor==null?null:new Color(coloringColor.getRGB());
                    if(!hasPencilmark && newColor!=null) return; //tried to color unexisting pencilmark
                    final Color currentColor;
                    if(!hasPencilmark) currentColor = null;
                    else currentColor = c.getPencilmarkColoring(value)==null?null:new Color(c.getPencilmarkColoring(value).getRGB());
                    
                    if(c.getValue() == 0){
                        controller.fireUndoableEditEventOccured(new UndoableEditEvent(this,
                            new AbstractUndoableEdit(){
                                @Override
                                public void undo(){
                                    super.undo();
                                    if(hasPencilmark){
                                        if(newColor!=null){
                                            c.setPencilmarkColoring(value, currentColor);
                                        }else{
                                            c.addPencilmark(value);
                                        }
                                    }else c.delPencilmark(value);
                                    repaint(offsetX+fieldX*fieldW, offsetY+fieldY*fieldH, fieldW, fieldH);
                                }
                                @Override
                                public void redo(){
                                    super.redo();
                                    if(newColor != null){
                                        c.setPencilmarkColoring(value, newColor.equals(currentColor)?null:newColor);
                                    }else{
                                        if(hasPencilmark) c.delPencilmark(value);
                                        else c.addPencilmark(value);
                                    }
                                    repaint(offsetX+fieldX*fieldW, offsetY+fieldY*fieldH, fieldW, fieldH);
                                }
                            }
                        ));
                        if(newColor != null){
                            c.setPencilmarkColoring(value, newColor.equals(currentColor)?null:newColor);
                        }else{
                            if(hasPencilmark) c.delPencilmark(value);
                            else c.addPencilmark(value);
                        }
                    }
                }
                repaint(offsetX+fieldX*fieldW, offsetY+fieldY*fieldH, fieldW, fieldH);
            }
        });
        
        recalculate();
    }
    
    public void setHlCol(int col){
        if(hlCol == col) return;
        hlCol = col;
        int fieldW = pref.getInt("fieldW");
        int fieldH = pref.getInt("fieldH");
        int offsetX = pref.getInt("offsetX");
        int offsetY = pref.getInt("offsetY");
        repaint();
      //  repaint(offsetX+hlCol*fieldW, offsetY, fieldW, );
    }
    public void setHlRow(int row){
        if(hlRow == row) return;
        hlRow = row;
        repaint();
    }
    public void setColoringColor(Color color){
        coloringColor = color;
    }
    public Color getColoringColor(){
        return coloringColor;
    }
    public void recalculate(){
        int w = pref.getInt("offsetX")*2+pref.getInt("fieldW")*controller.getData().getWidth();
        int h = pref.getInt("offsetY")*2+pref.getInt("fieldH")*controller.getData().getHeight();
        Dimension size = new Dimension(w, h);
        setPreferredSize(size);
    }
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        int fieldW = pref.getInt("fieldW");
        int fieldH = pref.getInt("fieldH");
        int offsetX = pref.getInt("offsetX");
        int offsetY = pref.getInt("offsetY");
        
        g.setColor(new Color(pref.getInt("bgColor")));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if(paused){
            g.setColor(new Color(pref.getInt("cellValueColor")));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            g.drawString(TR.t("PAUSED"), 20, 30);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            g.drawString(TR.t("Click anywhere on the board to unpause."), 20, 60);
            return;
        }
        
        for(int y=0; y<controller.getData().getHeight(); y++){
            for(int x=0; x<controller.getData().getWidth(); x++){
                Cell cell = controller.getGrid()[x][y];
                if(cell != null){
                    cell.paintBackground(g, offsetX+x*fieldW, offsetY+y*fieldH);
                    if(hlCol == x || hlRow == y){
                        //draw highlighted
                        //g.setColor(new Color(40,40,255, 40));
                        g.setColor(new Color(0,0,0, 30));
                        g.fillRect(offsetX+x*fieldW, offsetY+y*fieldH, fieldW, fieldH);
                    }
                    cell.paintForeground(g, offsetX+x*fieldW, offsetY+y*fieldH);
                }
            }
        }
        
        ArrayList<GridElement> elements = controller.getElements();
        for(int i=0; i<elements.size(); i++){
            GridElement element = elements.get(i);
            element.paintComponent(g);
        }
    }
    
    public static int getCellX(int x){
        MyPreferences pref = MyPreferences.getInstance();
        return pref.getInt("offsetX") + pref.getInt("fieldW") * x;
    }
    public static int getCellY(int y){
        MyPreferences pref = MyPreferences.getInstance();
        return pref.getInt("offsetY") + pref.getInt("fieldH") * y;
    }
    public void pause(){
        paused = true;
        repaint();
    }
    public void unpause(){
        paused = false;
        repaint();
    }
    public void togglePause(){
        paused = !paused;
        repaint();
    }
}
