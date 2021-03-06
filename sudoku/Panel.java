/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Panel.java
 *
 * Created on 2011-05-10, 16:11:26
 */
package sudoku;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import utils.Zoomer;
import utils.ColorChooser;
import utils.MyButton;
import utils.buttons.AddPencilmarksButton;
import utils.buttons.PauseButton;
import utils.buttons.RedoButton;
import utils.buttons.UndoButton;

/**
 *
 * @author zeroos
 */
public class Panel extends javax.swing.JPanel {

    Data data;
    Controller controller;
    /** Creates new form Panel */
    public Panel(final Controller controller) {
        this(controller, new MyButton[]{});
    }
    public Panel(final Controller controller, MyButton[] buttons) {
        this.controller = controller;
        initComponents();
        jScrollPane1.setViewportView(controller.getGridPanel());
        zoomerPanel.add(new Zoomer(controller));
        
        final ColorChooser colorChooser = new ColorChooser();
        final UndoButton undoButton = new UndoButton();
        final RedoButton redoButton = new RedoButton();
        final PauseButton pauseButton = new PauseButton(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.togglePause();
            }
        });
        final AddPencilmarksButton addPencilmarksButton = new AddPencilmarksButton();
        addPencilmarksButton.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(addPencilmarksButton.isAdd()) controller.getData().addAllPencilmarks();
                else controller.getData().delAllPencilmarks();
            }
            
        });
        
        
        colorChooser.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getGridPanel().setColoringColor(colorChooser.getChoosenColor());
            }
        });
        
        undoButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.undo();
                undoButton.setEnabled(controller.canUndo());
                redoButton.setEnabled(controller.canRedo());
            }
        });
        redoButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.redo();
                undoButton.setEnabled(controller.canUndo());
                redoButton.setEnabled(controller.canRedo());
            }
        });
        
        controller.addUndoableEditListener(new UndoableEditListener(){
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoButton.setEnabled(controller.canUndo());
                redoButton.setEnabled(controller.canRedo());
            }
            
        });
        
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
        
        bottomButtons.add(colorChooser);
        bottomButtons.add(undoButton);
        bottomButtons.add(redoButton);
        bottomButtons.add(pauseButton);
        bottomButtons.add(addPencilmarksButton);
        
        for(int i=0; i<buttons.length; i++){
            topRight.add(buttons[i]);
        }
        topLeft.add(controller.getStopwatch().getPanel());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        topRight = new javax.swing.JPanel();
        topLeft = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        zoomerPanel = new javax.swing.JPanel();
        bottomButtons = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        topPanel.setBackground(new java.awt.Color(255, 255, 255));
        topPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        topPanel.setPreferredSize(new java.awt.Dimension(20, 25));
        topPanel.setLayout(new java.awt.BorderLayout());

        topRight.setBackground(new java.awt.Color(255, 255, 255));
        topRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 2));
        topPanel.add(topRight, java.awt.BorderLayout.LINE_END);

        topLeft.setBackground(new java.awt.Color(255, 255, 255));
        topPanel.add(topLeft, java.awt.BorderLayout.LINE_START);

        add(topPanel, java.awt.BorderLayout.PAGE_START);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 20));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 20));
        jPanel2.setLayout(new java.awt.BorderLayout());

        zoomerPanel.setBackground(new java.awt.Color(255, 255, 255));
        zoomerPanel.setMaximumSize(new java.awt.Dimension(120, 20));
        zoomerPanel.setMinimumSize(new java.awt.Dimension(120, 20));
        zoomerPanel.setPreferredSize(new java.awt.Dimension(120, 20));
        zoomerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        jPanel2.add(zoomerPanel, java.awt.BorderLayout.LINE_END);

        bottomButtons.setBackground(new java.awt.Color(255, 255, 255));
        bottomButtons.setPreferredSize(new java.awt.Dimension(150, 10));
        bottomButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        jPanel2.add(bottomButtons, java.awt.BorderLayout.LINE_START);

        add(jPanel2, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomButtons;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel topLeft;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel topRight;
    private javax.swing.JPanel zoomerPanel;
    // End of variables declaration//GEN-END:variables
}
