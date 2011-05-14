package sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import sudoku.gridElements.GridElement;
import sudoku.gridElements.House;

/**
 *
 * @author zeroos
 */
public class Data {
    protected int width;
    protected int height;
    protected Cell[][] grid;
    protected int maxValue = 0;
    protected ArrayList<GridElement> elements = new ArrayList<GridElement>();

    public Data(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Cell[width][height];
    }
    
    public Cell[][] getGrid(){
        return grid;
    }
    public Cell getCell(Position pos){
        return getCell(pos, true);
    }
    public Cell getCell(Position pos, boolean force){
        return getCell(pos.getX(), pos.getY());
    }
    public Cell getCell(int x, int y){
        return getCell(x, y, true);
    }
    public Cell getCell(int x, int y, boolean force){
        Cell c = grid[x][y];
        if(c == null && force){
            c = new Cell();
            grid[x][y] = c;
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
}
