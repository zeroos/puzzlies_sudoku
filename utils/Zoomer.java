package utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sudoku.Controller;

/**
 *
 * @author zeroos
 */
public class Zoomer extends JPanel implements ChangeListener {

    private MyPreferences pref;
    private Controller controller;
    private JSlider slider;
    int width = 110;
    int height = 20;
    int offsetX = 5;
    int offsetY = 5;
    boolean swap = true;

    public Zoomer(Controller controller) {
        this.controller = controller;
        pref = MyPreferences.getInstance();

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        slider = new JSlider(20, 70);
        slider.setPreferredSize(new Dimension(80, 20));
        slider.setOpaque(false);
        slider.setValue(controller.getPreferredFieldSize());
        this.setOpaque(false);
        add(slider);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getY() < height - offsetY && e.getY() > offsetY) {
                    if (e.getX() > offsetX && e.getX() < offsetX + height / 2) {
                        //minus clicked
                        swap = false;
                        if (slider.getValue() < 5) {
                            slider.setValue(0);
                        } else {
                            slider.setValue(slider.getValue() - 5);
                        }
                    } else if (e.getX() > width - offsetX - height / 2 && e.getX() < width - offsetX) {
                        //plus clicked
                        swap = false;
                        if (slider.getValue() > 95) {
                            slider.setValue(100);
                        } else {
                            slider.setValue(slider.getValue() + 5);
                        }
                    }
                }
            }
        });
        slider.addChangeListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(110, 20);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider s = (JSlider) e.getSource();
        //int newSize = (int) Math.ceil(((Math.log(-s.getValue() + 1 + 100) * 11) / Math.log(2) + 82));
        int swap = (int)((s.getMaximum() - s.getMinimum())*0.1);
        int preferredSize = controller.getPreferredFieldSize();
        if(this.swap && preferredSize + swap > s.getValue() && preferredSize - swap < s.getValue()){
            s.setValue(preferredSize);
        }
        this.swap = true;
        
        controller.setFieldSize(s.getValue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(pref.getInt("lineColor")));
        
        //preferred
        double proc = ((double)controller.getPreferredFieldSize()-slider.getMinimum())/(slider.getMaximum() - slider.getMinimum());
        g.drawLine(20 + (int)(proc*70), 10, 20 + (int)(proc*70), height-2);
        //g.drawLine(90, 0, 90, height);
        //minus
        g.drawLine(offsetX, height / 2, height / 2 + offsetX, height / 2);
        //plus
        g.drawLine(width - offsetX, height / 2, width - offsetX - height / 2, height / 2);
        g.drawLine(width - offsetX - height / 4, offsetY, width - offsetX - height / 4, height - offsetY);
    }
}
