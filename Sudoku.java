import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sudoku.Controller;
import sudoku.Panel;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class Sudoku {
    public static void main(final String args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(args.length > 0){
                    createAndShowGUI(args[0]);
                }else{
                    createAndShowGUI();
                }
            }
        });
    }
    public static void createAndShowGUI(){
        createAndShowGUI(null);
    }
    public static void createAndShowGUI(String file){
        file = "/home/zeroos/programowanie/java/sudoku/test/test.sud";

        JFrame frame = new JFrame(TR.t("Sudoku"));
        Panel panel = new Panel(new Controller(file));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(450, 500);
        frame.setVisible(true);
    }
}


