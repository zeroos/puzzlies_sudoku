package sudoku;

/**
 *
 * @author zeroos
 */
public class Position implements Cloneable{
    protected int x;
    protected int y;

    public Position(String s) {
        /* s should be in the following format: x,y */
        String[] split = s.split(",");
        this.x = Integer.parseInt(split[0]);
        this.y = Integer.parseInt(split[1]);
    }
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Position other = (Position) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.x;
        hash = 83 * hash + this.y;
        return hash;
    }
    
    @Override
    public Position clone(){
        return new Position(x, y);
    }
    
    public String toString(){
        return "(" + x + ";" + y + ")";
    }

}
