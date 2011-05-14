package utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 *
 * @author zeroos
 */
public class ColorChooser extends JPanel{
    MyPreferences pref;
    Color colors[] = {
        new Color(220,130,130),
        new Color(130,220,130),
        new Color(220,220,130)
    };
    int selectedColor = -1;
    EventListenerList actionListenerList = new EventListenerList();
    
    
    public ColorChooser(ActionListener l){
        this();
        this.addActionListener(l);
    }
    public ColorChooser(){
        pref = MyPreferences.getInstance();
        Dimension d = new Dimension((colors.length+1)*pref.getInt("buttonHeight"), pref.getInt("buttonHeight"));
        setPreferredSize(d);
        setSize(d);
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                setSelectedColor(e.getX()/pref.getInt("buttonHeight") -1);
            }
        });
    }
    
    public boolean isColorChoosen(){
        return (selectedColor >= 0);
    }
    
    public Color getChoosenColor(){
        try{
            return colors[selectedColor];
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    
    public void setSelectedColor(int color){
        if(color == selectedColor) return;
        selectedColor = color;
        repaint();
        fireActionEvent();
    }
    
    
    @Override
    public void paintComponent(Graphics g){
        int size = pref.getInt("buttonHeight");
        g.setColor(new Color(pref.getInt("fieldColor")));
        g.fillRect(0, 0, size, size);
        g.setColor(new Color(pref.getInt("lineColor")));
        g.drawOval(size/3+1, size/3+1, size/3, size/3);
        g.drawLine(size*7/10, size*3/10, size*3/10, size*7/10);
        
        for(int c=0; c<colors.length; c++){
            g.setColor(colors[c]);
            g.fillRect((c+1)*size, 0, size, size);
            g.setColor(new Color(pref.getInt("lineColor")));
            g.drawLine((c+1)*size, 0, (c+1)*size, getSize().height);
        }
        
        
        g.setColor(new Color(pref.getInt("lineColor")));
        g.drawLine(0, 0, getSize().width, 0);
        g.drawLine(0, getSize().height-1, getSize().width, getSize().height-1);
        g.drawLine(0, 0, 0, getSize().height-1);
        g.drawLine(getSize().width-1, 0, getSize().width-1, getSize().height-1);
        
        //outline selected color
        g.setColor(Color.BLACK);
        g.fillRect((selectedColor+1)*size, 0, size, 2);
        g.fillRect((selectedColor+1)*size, size-2, size, 2);
        g.fillRect((selectedColor+1)*size, 0, 2, size);
        g.fillRect((selectedColor+2)*size-2, 0, 2, size);
    }
    public void addActionListener(ActionListener l) {
        actionListenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        actionListenerList.remove(ActionListener.class, l);
    }

    public void fireActionEvent() {
        ActionListener listeners[] =
                actionListenerList.getListeners(ActionListener.class);
        for (ActionListener l : listeners) {
            l.actionPerformed(new ActionEvent(this, 0, "pressed"));
        }
    }
}
