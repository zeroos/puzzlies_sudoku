package sudoku;

/**
 *
 * @author zeroos
 */
public class UnsolvableException extends Exception{
    public static final int CONTRADICTION = 0;
    public static final int MULTIPLE_SOLUTIONS = 1;
    
    int reason;
    public UnsolvableException(int reason){
        super();
        this.reason = reason;
    }
    public int getReason(){
        return reason;
    }
    
}
