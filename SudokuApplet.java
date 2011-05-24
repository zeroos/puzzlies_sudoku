
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sudoku.Controller;
import sudoku.Panel;
import utils.MyButton;
import utils.TR;


/**
 *
 * @author zeroos
 */
public class SudokuApplet extends JApplet{
    private boolean isPoppedOut = false;
    final MyButton popOutButton = new MyButton();
    ActionListener appletCloseListener;
    private Dimension initialSize;
    public String host;

    @Override
    public void init(){
        try{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    createAndShowGUI();
                }
                
            });
        }catch(Exception e){
            System.err.println("Error while initializing applet.");
        }
    }
    private void createAndShowGUI(){
        host = getCodeBase().getProtocol() + "://" + getCodeBase().getHost() + ":" + getCodeBase().getPort();
        
        initialSize = getSize();
        try {
            String file = host + "/sudoku/api/" + getParameter("id");
                        
            final Controller c = new Controller(new URL(file));
            
            updatePopOutButton();
            
            
            Panel panel = new Panel(c, new MyButton[]{popOutButton});
            setContentPane(panel);
            
            popOutButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    if(isPoppedOut){
                        appletCloseListener.actionPerformed(null);
                    }
                }
            });
            
            c.getData().addFinishListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    try{
                        //init data
                        String data = URLEncoder.encode("solution_md5", "UTF-8") + "="
                            + URLEncoder.encode(c.getData().getSolutionMD5(), "UTF-8");
                        data += "&" + URLEncoder.encode("timetoken", "UTF-8") + "="
                            + URLEncoder.encode(getParameter("timetoken"), "UTF-8");
                        data += "&" + URLEncoder.encode("time", "UTF-8") + "="
                            + URLEncoder.encode(Long.toString(c.getStopwatch().getTime()/1000), "UTF-8");
                        data += "&" + URLEncoder.encode("was_paused", "UTF-8") + "="
                            + URLEncoder.encode(c.getStopwatch().wasPaused()?"True":"False", "UTF-8");


                        //POST data
                        URL url = new URL(host + "/sudoku/api/solve/" + getParameter("id") + "/");
                        URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                        wr.write(data);
                        wr.flush();
                        // Get the response 
                        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String result = rd.readLine();
                        System.out.println("Server msg: " + rd.readLine());
                        wr.close();
                        rd.close();
                        String msg = TR.t("Unknown error occured.");
                        if(result.equals("OK")){
                            //reload page
                            //get a string without query part
                            int queryStr = getDocumentBase().toString().indexOf('?');
                            URL redirectTo;
                            if(queryStr != -1){
                                redirectTo = new URL(getDocumentBase().toString().substring(0, queryStr));
                            }else{
                                redirectTo = new URL(getDocumentBase().toString());
                            }
                            getAppletContext().showDocument(redirectTo, "_self");
                            return;
                        }else if(result.equals("INCORRECT_SOLUTION")){
                            msg = TR.t("Your solution is incorrect.");
                        }else if(result.equals("NOT_AUTHENTICATED")){
                            msg = TR.t("Congratulations! You have solved this puzzle.\n\n" +
                                "You are not logged in, so it won't be saved. Log in to track your progress!");
                            JOptionPane.showMessageDialog(null, msg, "Sudoku solved", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }else if(result.equals("ERROR")){
                            msg = TR.t("Sorry, server error. Try again in a while.");
                        }

                        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }catch(Exception ex){
                        System.err.println("E:" + ex);
                        String msg = TR.t("An error occured while connecting to the server. Try again.");
                        JOptionPane.showMessageDialog(null, msg, TR.t("Connection error"), JOptionPane.ERROR_MESSAGE);
                        System.err.println(msg);

                    }
                }
                    
            });

        } catch (MalformedURLException ex) {
            Logger.getLogger(SudokuApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    private void updatePopOutButton(){ 
        if(isPoppedOut) 
            popOutButton.setLabel(TR.t("Pop in")); 
        else 
            popOutButton.setLabel(TR.t("Pop out")); 
    } 
 
    public boolean isAppletDragStart(MouseEvent e) { 
        if(e.getSource().equals(popOutButton) && !isPoppedOut) return true; 
        return false; 
    } 
    public void appletDragStarted(){ 
        isPoppedOut = true; 
        updatePopOutButton(); 
        Container container = this.getParent(); 
        while(container != null) { 
            if(container instanceof Frame) { 
                Frame frame = (Frame)container; 
                frame.setResizable(true); 
                frame.setUndecorated(false); 
                return; 
            } 
            container = container.getParent(); 
        } 
    } 
    public void setAppletCloseListener(ActionListener l){ 
        //do not display floating x button 
        appletCloseListener = l; 
    } 
    public void appletRestored(){ 
        isPoppedOut = false; 
        updatePopOutButton(); 
        this.setSize(initialSize); 
    }

    
    
}
