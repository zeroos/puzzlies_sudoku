package sudoku.gridElements;

import java.awt.Graphics;
import sudoku.Cell;
import sudoku.Data;
import sudoku.Position;
import sudoku.UnsolvableException;

/**
 *
 * @author zeroos
 */
public class Given implements GridElement{
    Position pos;
    int value;
    public Given(Position pos, int value) {
        this.pos = pos;
        this.value = value;
    }
    public Position getPos(){
        return pos;
    }
    public int getValue(){
        return value;
    }

    @Override
    public void init(Data d) {
        Cell c = d.getCell(pos);
        c.setType(Cell.GIVEN);
        c.setValue(value);
    }

    @Override
    public void paintComponent(Graphics g) {
        //does not paint itself
    }
    @Override
    public int validate(){
        return VALID;
    }
    @Override
    public int updatePencilmarks() throws UnsolvableException{
        return 0;
    }
    
    @Override
    public Given clone(){
        return new Given(pos, value);
    }
}
