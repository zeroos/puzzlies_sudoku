import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sudoku.Controller;
import sudoku.Panel;
import sudoku.Solver;
import sudoku.UnsolvableException;
import utils.MyButton;
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
        final Controller c = new Controller(file);
        
        
        JMenuBar menuBar = new JMenuBar();
        JMenuItem menuItem;
        menuItem = new JMenuItem(new AbstractAction(TR.t("Solve")){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    Solver s = new Solver(c.getData());
                    s.solve();
                } catch (UnsolvableException ex) {
                    System.out.println("UNSOLVABLE:");
                    if(ex.getReason() == UnsolvableException.CONTRADICTION){
                        System.out.println("CONTRADICTION");
                    }else if(ex.getReason() == UnsolvableException.MULTIPLE_SOLUTIONS){
                        System.out.println("MULTIPLE SOLUTIONS");
                    }else{
                        System.out.println("UNKNOWN");
                    }
                }
            }
        });
        menuBar.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction(TR.t("One step")){
            @Override
            public void actionPerformed(ActionEvent e){
                Solver s = new Solver(c.getData());
                try {
                    s.oneStep();
                } catch (UnsolvableException ex) {
                    System.out.println("Unsolvable");
                }
            }
        });
        menuBar.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction(TR.t("Show hints")){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    c.getData().updatePencilmarks();
                } catch (UnsolvableException ex) {
                    System.out.println("Unsolvable");
                }
                    }
        });
        menuBar.add(menuItem);
        
        
        frame.setJMenuBar(menuBar);
        Panel panel = new Panel(c, new MyButton[]{new MyButton("test")});
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(450, 500);
        frame.setVisible(true);
    }
}


