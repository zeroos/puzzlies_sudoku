package sudoku;

/**
 *
 * @author zeroos
 */
public class IrregularBlocksetGenerator {
    String result = "";
    int w = 0;
    int h = 0;
    int[][] initialBlockset;
    int[][] blockset;
    int[][] expansionTable;
    
    public static void main(String args[]){
        System.out.println(generateIrregularBlockset("111222111222333444333444555666555666",6));
        
        return;
    }
    
    public static String generateIrregularBlockset(String initialBlockset, int w){
        int h = initialBlockset.length()/w;
        int[][] initialBlocksetArray = new int[w][h];
        for(int y=0; y<h; y++){
            for(int x=0; x<w; x++){
                int l = initialBlockset.charAt(y*h+x)-48;
                initialBlocksetArray[x][y] = l;
            }
        }
        IrregularBlocksetGenerator ibg = new IrregularBlocksetGenerator(initialBlocksetArray);
        
        return ibg.getResult();
    }
    public IrregularBlocksetGenerator(int[][] initialBlockset){
        this.initialBlockset = initialBlockset;
        this.blockset = initialBlockset;
        this.w = blockset.length;
        this.h = blockset[0].length;
        for(int i=0; i<1; i++){
            this.randomizeBlockset();
        }
        
        
        String result = "";
        for(int y=0; y<initialBlockset.length; y++){
            for(int x=0; x<initialBlockset[0].length; x++){
                result += initialBlockset[x][y];
            }
        }
        this.result = result;
    }
    private int[][] randomizeBlockset(){
        int randX1 = 1;
        int randY1 = 1;
        int randX2 = 3;
        int randY2 = 1;
        
        int val1 = blockset[randX1][randY1];
        int val2 = blockset[randX2][randY2];
        
        expand(0, 0);
        
        return blockset;
    }
    private int expand_r(int x, int y, int r){
        if(expansionTable[x][y] == 2) return -2;
        if(r==0 && expansionTable[x][y] == 1) return -1;
        if(r==0) expansionTable[x][y] = 1;
        else expansionTable[x][y] = 2;
        
        int v = blockset[x][y];
        int res = 0;
        
        for(int dx=-1; dx==-1 || dx==0 || dx==1; dx++){
            for(int dy=-1; dy==-1 || dy==0 || dy==1; dy++){
                if(x+dx < 0 || x+dx > w || y+dy < 0 || y+dy>h) continue;
                
                System.out.println(x+dx);
                System.out.println(y+dy);
                if(blockset[x+dx][y+dy] == v){
                    res = expand_r(x+1, y, r);
                }else if(r==0){
                    System.out.println("half goal"+x+"x"+y);
                    expansionTable[x][y] = 2;
                    res = expand_r(x+1, y, blockset[x+1][y]);
                }else if(r==v){
                    System.out.println("goal"+x+"x"+y);
                    return 1;
                }
            }
        }
        if(res == 1){
            return 1;
        }
        return 0;
    }
    private void expand(int x, int y){
        expansionTable = new int[w][h];
        expand_r(x,y,0);
    }
    private String getResult() {
        return result;
    }
}

/*
2222
1111
3334
3444

11g12b333
11gb2b333
gggbbb333
444555666
444555666
444555666
777888999
777888999
777888999
 */
