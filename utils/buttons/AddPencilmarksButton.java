package utils.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class AddPencilmarksButton extends utils.MyButton{

    protected boolean add = true;
    
    public AddPencilmarksButton(ActionListener l){
            this();
            this.addActionListener(l);
    }
    public AddPencilmarksButton(){
            super("A");
            calcSize();
            setToolTipText(TR.t("Add all pencilmarks"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    add = !add;
                }
            });
    }
    
    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
    
    private void calcSize(){
            setPreferredSize(new Dimension(fieldH, fieldH));
            setSize(getPreferredSize());
    }
    @Override
    public void paint(Graphics g){
        g.clearRect(0,0,getWidth(),getHeight());
        int xOffset = 0;
        int yOffset = 0;
        
        //g.setColor(new Color(pref.getInt("lineColor")));
        //g.drawRect(0, 0, fieldH-1, fieldH-1);
        if(add){
            g.setColor(new Color(pref.getInt("activePencilmarkColor")));
        }else{
            g.setColor(new Color(pref.getInt("inactivePencilmarkColor")));
        }
        if(mousePressed){
            xOffset = yOffset = 1;
	}
        
        
        
                
        for(int y=1; y<=3; y++){
            for(int x=1; x<=3; x++){
                g.fillOval(xOffset+fieldH*x/4-1, yOffset+fieldH*y/4-1, 2, 2);
                g.fillOval(xOffset+fieldH*x/4-1, yOffset+fieldH*y/4-1, 2, 2);
                g.fillOval(xOffset+fieldH*x/4-1, yOffset+fieldH*y/4-1, 2, 2);
            }
        }
    }
}
