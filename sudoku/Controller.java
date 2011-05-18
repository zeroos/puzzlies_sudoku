package sudoku;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import sudoku.gridElements.GridElement;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */
public class Controller {
    Data data;
    MyPreferences pref;
    private UndoManager undoManager = new UndoManager();
    Grid grid;
    
    EventListenerList undoableEditListenerList = new EventListenerList();

    
    
    public Controller(){
        this(null);
    }
    public Controller(String file){
        pref = MyPreferences.getInstance();
        setDefaults();
        
        if(file == null) data = Generator.generate();
        else open(file);
        grid = new Grid(this);
        grid.repaint();
        setFieldSize(getPreferredFieldSize());
    }
    public void open(String file){
        Data d = XMLParser.parseFile(file);
        if(d != null){
            data = d;
            data.init();
        }else{
            System.out.println("Cannot load file. ;(");
        }
        
        setFieldSize(getPreferredFieldSize());
    }
    public void setDefaults(){
        pref.setInt("fieldW", 30);
        pref.setInt("fieldH", 30);
        pref.setInt("offsetX", 15);
        pref.setInt("offsetY", 15);
        pref.setInt("lineColor", new Color(0x77,0x77,0x77).getRGB());
        pref.setInt("fieldColor", new Color(0xff,0xff,0xff).getRGB());
        pref.setInt("bgColor", new Color(0xdd,0xdd,0xdd).getRGB());
        pref.setInt("givenValueColor", new Color(0x44,0x44,0xaa).getRGB());
        pref.setInt("cellValueColor", new Color(0x22,0x22,0x22).getRGB());
        pref.setInt("warningColor", new Color(0xaa,0x00,0x00).getRGB());
        pref.setInt("activePencilmarkColor", new Color(0x22, 0x22, 0x22).getRGB());
        pref.setInt("inactivePencilmarkColor", new Color(0xaa, 0xaa, 0xaa).getRGB());
        pref.setInt("minPencilmarkSize", 15);
        pref.setInt("buttonHeight", 20);
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
    }
    public void unpause(){
        grid.unpause();
    }
    public void togglePause(){
        grid.togglePause();
    }
}
