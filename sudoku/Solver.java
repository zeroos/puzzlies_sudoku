package sudoku;

import java.util.Set;
import javax.swing.JFrame;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class Solver {
    Data data;
    public Solver(Data data){
        this.data = data;
    }
    
    public Data getData(){
        return data;
    }
    
    public void solve() throws UnsolvableException{
        while(oneStep());
        makeAssumptions();
    }
    
    public boolean oneStep() throws UnsolvableException{
        boolean changed = false;
        data.updatePencilmarks();
        for(int y=0; y<data.getHeight(); y++){
            for(int x=0; x<data.getWidth(); x++){
                Cell c = data.getCell(x,y);
                if(c.getValue() == 0){
                    Set s = c.getPencilmarks().keySet();
                    if(s.size() == 1){
                        c.setValue((Integer)s.iterator().next());
                        changed = true;
                    }else if(s.isEmpty()){
                        throw new UnsolvableException(UnsolvableException.CONTRADICTION);
                    }
                }
            }
        }
        return changed;
    }
    
    private void makeAssumptions() throws UnsolvableException{
        Data newD = data.clone();
        for(int y=0; y<newD.getHeight(); y++){
            for(int x=0; x<newD.getWidth(); x++){
                if(data.getCell(x, y, false) != null && 
                   data.getCell(x, y).getValue() == 0){
                    Data solution = null;
                    for(int v=1; v<= newD.getMaxValue(); v++){
                        newD.getCell(x, y).setValue(v);
                        Solver newSolver = new Solver(newD);
                        try{
                            newSolver.solve();
                            //solved
                            if(solution != null) throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
                            solution = newSolver.getData().clone();
                        }catch(UnsolvableException e){
                            if(e.getReason() == UnsolvableException.MULTIPLE_SOLUTIONS){
                                throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
                            }
                        }
                    }
                    if(solution == null) throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
                    this.data.setGridValues(solution.getGridValues());
                }
            }
        }
    }
}
