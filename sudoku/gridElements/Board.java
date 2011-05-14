package sudoku.gridElements;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import sudoku.Data;
import sudoku.Grid;
import sudoku.Position;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */
public class Board implements GridElement{
    Position start_pos;
    Position end_pos;
    MyPreferences pref;
    ArrayList<House> houses = new ArrayList<House>();
    
    public Board(Position start_pos, Position end_pos){
        pref = MyPreferences.getInstance();
        this.start_pos = start_pos;
        this.end_pos = end_pos;
    }
    @Override
    public void init(Data d) {
        for(int y=start_pos.getY(); y<=end_pos.getY(); y++){
            ArrayList<Position> cells = new ArrayList<Position>();
            for(int x=start_pos.getX(); x<=end_pos.getX(); x++){
                cells.add(new Position(x, y));
            }
            House house = new House(cells);
            house.setDrawBackground(false);
            house.init(d);
            houses.add(new House(cells));
        }
        for(int x=start_pos.getX(); x<=end_pos.getX(); x++){
            ArrayList<Position> cells = new ArrayList<Position>();
            for(int y=start_pos.getY(); y<=end_pos.getY(); y++){
                cells.add(new Position(x, y));
            }
            House house = new House(cells);
            house.setDrawBackground(false);
            house.init(d);
            houses.add(house);
        }
        /*for(int y=start_pos.getY(); y<=end_pos.getY(); y++){
            for(int x=start_pos.getX(); x<=end_pos.getX(); x++){
                d.getCell(x, y).setMaxValue(end_pos.getX()-start_pos.getX()+1);
            }
        }*/
    }

    @Override
    public void paintComponent(Graphics g) {
        /**
         * Creating board border by joining 4 points:
         * 1 ---- 2
         * |      |
         * |      |
         * 3 ---- 4
         */
        int x_start = Grid.getCellX(start_pos.getX());
        int x_end = Grid.getCellX(end_pos.getX()+1);
        int y_start = Grid.getCellY(start_pos.getY());
        int y_end = Grid.getCellY(end_pos.getY()+1);
        
        g.setColor(new Color(pref.getInt("lineColor"))); 
        // 1 -> 2
        g.drawLine(x_start-2, y_start-2, x_end+2, y_start-2);
        g.drawLine(x_start, y_start+1, x_end, y_start+1);
        
        // 1 -> 3
        g.drawLine(x_start-2, y_start-2, x_start-2, y_end+2);
        g.drawLine(x_start+1, y_start, x_start+1, y_end);
        
        // 3 -> 4
        g.drawLine(x_start, y_end-1, x_end, y_end-1);
        g.drawLine(x_start-2, y_end+2, x_end+2, y_end+2);
        
        // 2 -> 4
        g.drawLine(x_end-1, y_start, x_end-1, y_end);
        g.drawLine(x_end+2, y_start-2, x_end+2, y_end+2);
    }
    public int validate(){
        return VALID;
    }
}
