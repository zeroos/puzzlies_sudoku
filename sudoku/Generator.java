package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author zeroos
 */
public class Generator {
    public static Random random = new Random();
    
    public static void main(String args[]){
        System.out.println(generate().getGridValues());
    }
    public static Data generate(){
        return removeCells(generateFullBoard(), 35);
    }
    public static Data generateFullBoard(){
        String file = "/home/zeroos/programowanie/java/sudoku/test/basic_template.sud";
        Data data = XMLParser.parseFile(file);
        data.init();
        try {
            return generateFullBoard(data, new Position(0,0));
        } catch (UnsolvableException ex) {
            return null;
        }
        

        
    }
    public static Data generateFullBoard(Data data, Position start_pos) throws UnsolvableException{
        data.updatePencilmarks();
        for(int y=start_pos.getY(); y<data.getHeight(); y++){
            int start_x = start_pos.getY() == y?start_pos.getX():0;
            for(int x=start_x; x<data.getWidth(); x++){
                //System.out.println("(" + x + "," + y + ") ---");
                //System.out.println(data.getGridValues());
                Cell c = data.getCell(x, y);
                if(c==null || c.getValue() != 0) continue;
                Set s = c.getPencilmarks().keySet();
                s.remove(null);
                List<Integer> values = Arrays.asList((Integer[]) s.toArray(new Integer[0]));
                Collections.shuffle(values, random);
                //int rand = random.nextInt(values.length);
                if(values.isEmpty()){
                    //System.out.println("ROLLBACK1");
                    return null;
                }
                Data solution = null;
                for(int i=0; i<values.size(); i++){
                    try{
                        Data newD = data.clone();
                        
                        newD.getCell(x, y).setValue(values.get(i));
                        if((newD = generateFullBoard(newD, new Position(x,y))) != null){
                            data = newD;
                            //System.out.println("DONE");
                            //System.out.println(data.getGridValues());
                            return data;
                        }
                    }catch(UnsolvableException e){
                        //System.out.println("ROLLBACK2");
                        return null;
                    }
                }
                if(solution == null){
                    //System.out.println("ROLLBACK2");
                    return null;
                }
                data = solution;
                
                //System.out.println(data);
            }
        }
        return data;
    }
    
    public static Data removeCells(Data data, int number){
        Data newData = data.clone();
        ArrayList<Position> positions = new ArrayList<Position>();
        for(int y=0; y<data.getHeight(); y++){
            for(int x=0; x<data.getWidth(); x++){
                positions.add(new Position(x, y));
            }
        }
        Collections.shuffle(positions, random);
        int deleted = 0;
        for(int i=0; i<positions.size(); i++){
            Position pos = positions.get(i);
            //System.out.println(pos);
            Data testData = newData.clone();
            testData.getCell(pos).setValue(0);
            try{
                Solver s = new Solver(testData.clone());
                s.solve();
                //valid
                newData = testData;
                deleted ++;
                //System.out.println("removed");
                if(deleted == number){
                    return newData;
                }
                
            }catch(UnsolvableException e){
                //pass
            }
        }
        return newData;
    }
}
