package sudoku;

import java.util.Iterator;
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
                    newD.updatePencilmarks();
                    Cell c = newD.getCell(x, y);
                    Iterator<Pencilmark> i = c.getPencilmarks().values().iterator();
                    while(i.hasNext()){
                        int v=i.next().getValue();
                        c.setValue(v);
                        Solver newSolver = new Solver(newD.clone());
                        try{
                            newSolver.solve();
                            //solved
                            if(solution != null) throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
                            solution = newSolver.getData().clone();
                        }catch(UnsolvableException e){
                            if(e.getReason() == UnsolvableException.MULTIPLE_SOLUTIONS){
                                //System.out.println("MULTIP");
                                throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
                            }
                        }
                    }
                    if(solution == null) throw new UnsolvableException(UnsolvableException.CONTRADICTION);
                    data.setGridValues(solution.getGridValues());
                }
            }
        }
    }
}
