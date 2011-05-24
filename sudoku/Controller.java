package sudoku;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import sudoku.gridElements.GridElement;
import utils.MyPreferences;
import utils.Stopwatch;

/**
 *
 * @author zeroos
 */
public class Controller {
    Data data;
    MyPreferences pref;
    private UndoManager undoManager = new UndoManager();
    Grid grid;
    public Stopwatch stopwatch;
    
    EventListenerList undoableEditListenerList = new EventListenerList();

    //actions
    public Action undo = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent e) {
            undo();
        }
    };
    public Action redo = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent e) {
            redo();
        }
    };
    public Action togglePause = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent e){
            togglePause();
        }
    };
    
    public Controller(){
        this(null);
    }
    public Controller(URL url){
        pref = MyPreferences.getInstance();
        
        stopwatch = new Stopwatch();
        stopwatch.start();
        
        if(url == null) data = Generator.generate();
        else open(url);
        grid = new Grid(this);
        grid.repaint();
        setFieldSize(getPreferredFieldSize());
        
        grid.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),"undo"); //backspace for undo
        grid.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK),"undo"); //ctrl+z for undo
        grid.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),"redo"); //ctrl+shift+z for redo
        grid.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,InputEvent.CTRL_MASK),"redo"); //ctrl+y for redo
        grid.getActionMap().put("undo", undo);
        grid.getActionMap().put("redo", redo);
        
        grid.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_P,0),"togglePause"); //p for toggle pause
        grid.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE,0),"togglePause"); //pause for toggle pause
        grid.getActionMap().put("togglePause", togglePause);
    }
    public void open(URL url){
        Data d = XMLParser.parseFile(url);
        if(d != null){
            data = d;
            data.init();
        }else{
            System.out.println("Cannot load file. ;(");
        }
        
        setFieldSize(getPreferredFieldSize());
    }
    public Stopwatch getStopwatch(){
        return stopwatch;
    }
    public Grid getGridPanel(){
        return grid;
    }
    public Cell[][] getGrid(){
        return data.getGrid();
    }
    public ArrayList<GridElement> getElements(){
        return data.getElements();
    }
    public void setData(Data data){
        this.data = data;
    }
    public Data getData(){
        return data;
    }
    public int getPreferredFieldSize(){
        if(data.getMaxValue() < 10){
            return (int) Math.ceil(Math.sqrt(data.getMaxValue())) * pref.getInt("minPencilmarkSize");
        }
        //two digits
        return (int) Math.ceil(Math.sqrt(data.getMaxValue())) * pref.getInt("minPencilmarkSize") * 2;
    }
    public void setFieldSize(int newSize){
        pref.setInt("fieldW", newSize);
        pref.setInt("fieldH", newSize);
    }
    public boolean canUndo(){
        return undoManager.canUndo();
    }
    public boolean canRedo(){
        return undoManager.canRedo();
    }
    public void undo(){
        if(undoManager.canUndo()) undoManager.undo();
    }
    public void redo(){
        if(undoManager.canRedo()) undoManager.redo();
    }
    public void addUndoableEditListener(UndoableEditListener l){
        undoableEditListenerList.add(UndoableEditListener.class, l);
    }
    public void removeUndoableEditListener(UndoableEditListener l){
        undoableEditListenerList.remove(UndoableEditListener.class, l);
    }
    public void fireUndoableEditEventOccured(UndoableEditEvent e){
        undoManager.addEdit(e.getEdit());
        UndoableEditListener listeners[] =
            undoableEditListenerList.getListeners(UndoableEditListener.class);
        for(UndoableEditListener l: listeners){
            l.undoableEditHappened(e);
        }
    }
    public void pause(){
        grid.pause();
        stopwatch.pause();
    }
    public void unpause(){
        grid.unpause();
        stopwatch.start();
    }
    public void togglePause(){
        if(grid.isPaused()) unpause();
        else pause();
    }
}
