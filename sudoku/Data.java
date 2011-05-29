package sudoku;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sudoku.gridElements.Given;
import sudoku.gridElements.GridElement;
import sudoku.gridElements.House;

/**
 *
 * @author zeroos
 */
public class Data implements Cloneable{
    protected int width;
    protected int height;
    protected Cell[][] grid;
    protected int maxValue = 0;
    protected ArrayList<GridElement> elements = new ArrayList<GridElement>();
    
    EventListenerList changeListenerList = new EventListenerList();
    EventListenerList finishListenerList = new EventListenerList();

    public Data(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Cell[width][height];
    }
    
    public Cell[][] getGrid(){
        return grid;
    }
    public Cell getCell(Position pos){
        return getCell(pos, false);
    }
    public Cell getCell(Position pos, boolean force){
        return getCell(pos.getX(), pos.getY(), force);
    }
    public Cell getCell(int x, int y){
        return getCell(x, y, false);
    }
    public Cell getCell(int x, int y, boolean force){
        Cell c = grid[x][y];
        if(c == null && force){
            c = new Cell();
            grid[x][y] = c;
            c.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    validate();
                    fireChangeEvent();
                }
                
            });
        }
        return c;
    }
    
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return width;
    }
    public int getMaxValue(){
        return maxValue;
    }
    public void setMaxValue(int value){
        if(value>maxValue) maxValue=value;
    }
    
    public void addElement(GridElement g){
        elements.add(g);
    }
    public void init(){
        for(int i=0; i<elements.size(); i++){
            elements.get(i).init(this);
        }
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                Cell c = getCell(x, y, false);
                if(c != null) setMaxValue(c.getMaxValue());
            }
        }
    }
    public int updatePencilmarks() throws UnsolvableException{
        addAllPencilmarks();
        int result = 0;
        for(int i=0; i<elements.size(); i++){
            GridElement e = elements.get(i);
            result += e.updatePencilmarks();
        }
        return result;
    }
    public int validate(){
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                Cell c = getCell(x, y, false);
                if(c != null) c.setState(Cell.UNKNOWN);
            }
        }
        
        
        int result = GridElement.FINISHED;
        for(int i=0; i<elements.size(); i++){
            GridElement e = elements.get(i);
            int valid = e.validate();
            if(valid < result) result = valid;
        }
        if(result==GridElement.FINISHED) fireFinishEvent();
        return result;
    }
    
    public ArrayList<GridElement> getElements(){
        return elements;
    }
    
    public void setGridValues(String data){
        if(data.length() != getWidth() * getHeight()){
            System.err.println("Solution data corrupted, ignored.");
            return;
        }
        
        int dataPos = 0;
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                String stringVal = String.valueOf(data.charAt(dataPos));
                int value = Integer.parseInt(stringVal, Character.MAX_RADIX);
                if(value != 0){
                    getCell(x, y).setValue(value);
                }
                dataPos++;
            }
        }
    }
    public String getGridValues(){        
        String data = "";
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                data += String.valueOf(getCell(x,y).getValue());
            }
        }
        return data;
    }
    public String getSolutionMD5(){
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(getGridValues().getBytes());
            String result = new BigInteger(1,m.digest()).toString(16);
            return ("00000000000000000000000000000000" + result).substring(result.length());
        }catch(NoSuchAlgorithmException e){
            System.err.println("MD5 algorithm not found.");
            return "";
        }catch(Exception e){
            System.err.println("Error while genereting md5 sum.");
            return "";
        }
    }
    public void addGivens(String data) {
        /*Position pos = new Position(element.getAttributes().getNamedItem("pos").getNodeValue());
        int value = Integer.parseInt(element.getAttributes().getNamedItem("value").getNodeValue());
        data.addElement((GridElement)(new Given(pos, value)));*/
        
        if(data.length() != getWidth() * getHeight()){
            System.err.println("Givens data incorrect, ignored.");
            return;
        }
        int dataPos = 0;
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++, dataPos++){
                try{
                    int value = Integer.parseInt(String.valueOf(data.charAt(dataPos)), Character.MAX_RADIX);
                     if(value != 0) addElement((GridElement)(new Given(new Position(x,y), value)));
                }catch(NumberFormatException e){
                    //ignore
                }
                
               
            }
        }
    }
    public void addBlockset(String data){
        if(data.length() != getWidth() * getHeight()){
            System.err.println("Blockset data incorrect, ignored.");
            return;
        }
        HashMap map = new HashMap();
        int dataPos = 0;
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                String stringVal = String.valueOf(data.charAt(dataPos));
                if(!map.containsKey(stringVal)){
                    map.put(stringVal, new ArrayList<Position>());
                }
                ((ArrayList<Position>)map.get(stringVal)).add(new Position(x,y));
                dataPos++;
            }
        }
        Iterator i = map.values().iterator();
        while(i.hasNext()){
            House house = new House((ArrayList<Position>)i.next());
            house.setDrawOutline(true);
            house.setDrawBackground(false);
            addElement(house);
        }
    }
    public void addPencilmarks(String data){
        String[] pencilmarks = data.split(",");
        int dataPos = 0;
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                getCell(x,y).addPencilmarks(pencilmarks[dataPos++]);
                if(dataPos >= pencilmarks.length) return;
            }
        }
    }

    void addAllPencilmarks() {
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                for(int i=1; i<=getMaxValue(); i++){
                    getCell(x,y).addPencilmark(i);
                }
            }
        }
        fireChangeEvent();
    }

    void delAllPencilmarks() {
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                for(int i=1; i<=9; i++){
                    getCell(x,y).delPencilmark(i);
                }
            }
        }
        fireChangeEvent();
    }
    
    
    @Override
    public Data clone(){
        Data newD = new Data(getWidth(), getHeight());
        for(int i=0; i<elements.size(); i++){
            newD.addElement(elements.get(i).clone());
        }
        newD.init();
        for(int y=0; y<getHeight(); y++){
            for(int x=0; x<getWidth(); x++){
                int cellValue = getCell(x,y).getValue();
                if(cellValue != 0){
                    newD.getCell(x,y).setValue(cellValue);
                }
            }
        }
        return newD;
    }
    
    public void addFinishListener(ChangeListener l) {
        finishListenerList.add(ChangeListener.class, l);
    }

    public void removeFinishListener(ChangeListener l) {
        finishListenerList.remove(ChangeListener.class, l);
    }

    public void fireFinishEvent() {
        ChangeListener listeners[] =
                finishListenerList.getListeners(ChangeListener.class);
        for (ChangeListener l : listeners) {
            l.stateChanged(new ChangeEvent(this));
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
        for (ChangeListener l : listeners) {
            l.stateChanged(new ChangeEvent(this));
        }
    }
}
