package sudoku.gridElements;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import sudoku.Cell;
import sudoku.Data;
import sudoku.Grid;
import sudoku.Position;
import sudoku.UnsolvableException;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */
public class House implements GridElement{
    MyPreferences pref;
    ArrayList<Position> cellPositions;
    Color color = new Color(0xcc, 0xcc, 0xff);
    boolean drawBackground = true;
    boolean drawOutline = false;
    Data data;
    
    public House(ArrayList<Position> cellPositions){
        pref = MyPreferences.getInstance();
        this.cellPositions = cellPositions;
    }
    
    public void setDrawBackground(boolean drawBackground){
        this.drawBackground = drawBackground;
    }
    public boolean getDrawBackground(){
        return drawBackground;
    }
    public void setDrawOutline(boolean drawOutline){
        this.drawOutline = drawOutline;
    }
    public boolean getDrawOutline(){
        return drawOutline;
    }
    
    @Override
    public void init(Data d) {
        this.data = d;
        for(int i=0; i<cellPositions.size(); i++){
            Position p = cellPositions.get(i);
            Cell cell = d.getCell(p, true);
            if(drawBackground){
                cell.setBackgroundColor(color);
            }
            cell.setMaxValue(cellPositions.size());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if(drawOutline){
            //it must be possible to do it more efficiently
            g.setColor(new Color(pref.getInt("lineColor")));
            for(int i=0; i<cellPositions.size(); i++){
                Position p = cellPositions.get(i);
                int x = p.getX();
                int y = p.getY();
                if(!cellPositions.contains(new Position(x, y-1))){
                    g.drawLine(Grid.getCellX(x), Grid.getCellY(y)+1, Grid.getCellX(x+1), Grid.getCellY(y)+1);
                }
                if(!cellPositions.contains(new Position(x, y+1))){
                    g.drawLine(Grid.getCellX(x), Grid.getCellY(y+1)-1, Grid.getCellX(x+1), Grid.getCellY(y+1)-1);
                }
                if(!cellPositions.contains(new Position(x-1, y))){
                    g.drawLine(Grid.getCellX(x)+1, Grid.getCellY(y), Grid.getCellX(x)+1, Grid.getCellY(y+1));
                }
                if(!cellPositions.contains(new Position(x+1, y))){
                    g.drawLine(Grid.getCellX(x+1)-1, Grid.getCellY(y), Grid.getCellX(x+1)-1, Grid.getCellY(y+1));
                }
            }
        }
    }
    @Override
    public int validate(){
        if(data == null){
            return INVALID;
        }
        Cell test[] = new Cell[cellPositions.size()];
        int result = FINISHED;
        for(int i=0; i<cellPositions.size(); i++){
            Cell c = data.getCell(cellPositions.get(i));
            int value = c.getValue();
            if(value == 0){
                if(result == FINISHED) result = VALID;
                continue;
            }
            try{
                if(test[value-1] == null){
                    test[value-1] = data.getCell(cellPositions.get(i));
                    //test[value-1].setState(Cell.UNKNOWN);
                }else{
                    result = INVALID;
                    data.getCell(cellPositions.get(i)).setState(Cell.INCORRECT);
                    test[value-1].setState(Cell.INCORRECT);
                }
            }catch(ArrayIndexOutOfBoundsException e){
                data.getCell(cellPositions.get(i)).setState(Cell.INCORRECT);
            }
        }
        return result;
    }
    
    public void updatePencilmarks(Cell c){
        //updates Pencilmarks for a given Cell, i.e. removes all Pencilmarks that are incorrect
        //assumes that cellPositions contains Cell c.
        for(int i=0; i<cellPositions.size(); i++){
            int value = data.getCell(cellPositions.get(i)).getValue();
            c.delPencilmark(value);
        }
    }
    
    @Override
    public int updatePencilmarks() throws UnsolvableException{
        //single candidate
        for(int i=0; i<cellPositions.size(); i++){
            Cell c = data.getCell(cellPositions.get(i));
            updatePencilmarks(c);
        }
        
        
        
        //single position
        for(int v=1; v<=data.getMaxValue(); v++){
            ArrayList<Cell> possiblePositions = new ArrayList<Cell>();
            for(int i=0; i<cellPositions.size(); i++){
                Cell c = data.getCell(cellPositions.get(i));
                if(c.getValue() == v){
                    possiblePositions.add(c);
                    break;
                }
                if(c.hasPencilmark(v)) possiblePositions.add(c);
            }
            if(possiblePositions.size() == 1){
                Cell c = possiblePositions.get(0);
                for(int i=0 ; i<data.getMaxValue(); i++) c.delPencilmark(i);
                c.addPencilmark(v);
            }else if(possiblePositions.isEmpty()){
                throw new UnsolvableException(UnsolvableException.CONTRADICTION);
            }
        }
        
        
        return 0;
    }
    
    @Override
    public House clone(){
        House h = new House((ArrayList<Position>)cellPositions.clone());
        h.setDrawBackground(drawBackground);
        h.setDrawOutline(drawOutline);
        return h;
    }
    
    @Override
    public String toString(){
        String txt = "House [";
        for(int i=0; i<cellPositions.size(); i++){
            txt += cellPositions.get(i) + " ";
        }
        return txt + "]";
    }
    
}
