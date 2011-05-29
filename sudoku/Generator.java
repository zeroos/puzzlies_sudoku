package sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zeroos
 */
public class Generator {
    public static Random random = new Random();
    public static String templateDir = "/home/zeroos/programowanie/java/sudoku/test/";
    public static String fileSufix = "_template.sud";
    public static String type = "16x16";
    public static final String algorithmName = "basic";
    public static int removeCellsNum = 150;
    
    public static void main(String args[]){
        String templateFile = templateDir + type +fileSufix;
        if(args.length > 0) templateFile = args[0];
        
        if(args.length > 1) removeCellsNum = Integer.parseInt(args[1]);
        Data d = generate(templateFile, removeCellsNum);
        
        Solver s = new Solver(d.clone());
        try {
            s.solve();
        } catch (UnsolvableException ex) {
            System.out.println("WTF?");
        }
        Data solvedData = s.getData();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(templateFile));
            char[] cbuf = new char[5024];
            int count = reader.read(cbuf);
            String template = String.valueOf(cbuf, 0, count);
            String givensTemplate = "<givens>%s</givens>";
            String solutionsTemplate = "<data>%s</data>";
            String meta = "<meta><algorithm>" + algorithmName + "</algorithm><type>" + type + "</type></meta>";
            template = String.format(template, meta, givensTemplate, solutionsTemplate);
            System.out.format(template, d.getGridValues(), solvedData.getGridValues());
        }catch (FileNotFoundException ex) {
            System.out.println("ERROR");
            System.out.println("File not found.");
        }catch (IOException e){
            System.out.println("ERROR");
            System.out.println("Cannot read file.");
        }
    }
    public static Data generate(){
        return generate(templateDir + type + fileSufix, removeCellsNum);
    }
    public static Data generate(String templateFile, int removeCellsNum){
        return removeCells(generateFullBoard(templateFile),removeCellsNum);
    }
    public static Data generateFullBoard(String templateFile){
        try {
            Data data = XMLParser.parseFile(new URL("file://" + templateFile));
            data.init();
            try {
                return generateFullBoard(data, new Position(0,0));
            } catch (UnsolvableException ex) {
                return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        

        
    }
    public static Data generateFullBoard(Data data, Position start_pos) throws UnsolvableException{
        System.out.println(start_pos);
        data.updatePencilmarks();
        for(int y=start_pos.getY(); y<data.getHeight(); y++){
            int start_x = start_pos.getY() == y?start_pos.getX():0;
            for(int x=start_x; x<data.getWidth(); x++){
                //System.out.println("(" + x + "," + y + ") ---");
                //System.out.println(data.getGridValues());
                data.updatePencilmarks();
                Cell c = data.getCell(x, y);
                if(c==null || c.getValue() != 0) continue;
                Collection<Pencilmark> s = c.getPencilmarks().values();
                s.remove(null);
                
                List<Pencilmark> values = Arrays.asList((Pencilmark[]) s.toArray(new Pencilmark[0]));
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
                        newD.getCell(x, y).setValue(values.get(i).getValue());
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
        if(number == 0) return data;
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
                System.out.println("DELETED " + deleted + " CELL");
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
