package sudoku.gridElements;

import java.awt.Graphics;
import sudoku.Data;
import sudoku.UnsolvableException;

/**
 *
 * @author zeroos
 */
public interface GridElement extends Cloneable{
    static final int INVALID = 0;
    static final int VALID = 1;
    static final int FINISHED = 2;
    
    public void init(Data d);
    public void paintComponent(Graphics g);
    public int validate();
    public int updatePencilmarks() throws UnsolvableException;
    public GridElement clone();
}
