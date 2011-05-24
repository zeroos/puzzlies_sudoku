package sudoku;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sudoku.gridElements.Board;
import sudoku.gridElements.Given;
import sudoku.gridElements.GridElement;
import sudoku.gridElements.House;
import utils.Utils;

/**
 *
 * @author zeroos
 */
public class XMLParser {
    static double API = 0.1;
    public static Data parseFile(URL url){
        Data data = null;
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(url.openStream());
            Element root = doc.getDocumentElement();
            if(Double.parseDouble(root.getAttribute("api")) > API){
                throw new Exception("Unsuported API version.");
            }
            
            Node grid = root.getElementsByTagName("grid").item(0);
            
            int rows = Integer.parseInt(grid.getAttributes().getNamedItem("rows").getNodeValue());
            int cols = Integer.parseInt(grid.getAttributes().getNamedItem("cols").getNodeValue());
            data = new Data(cols, rows);
            
            NodeList gridChilds = grid.getChildNodes();
            
            NodeList elements = null;
            NodeList solution = null;
            
            for(int i=0; i<gridChilds.getLength(); i++){
                //elements or solution
                if(gridChilds.item(i).getNodeName().equals("elements")){
                    elements = gridChilds.item(i).getChildNodes();
                }else if(gridChilds.item(i).getNodeName().equals("solution")){
                    solution = gridChilds.item(i).getChildNodes();
                }
            }
            if(elements == null) throw new Exception("No elements defined.");
            for(int i=0; i<elements.getLength(); i++){
                Node element = elements.item(i);
                if(element.getNodeName().equals("board")){
                    Position start_pos = new Position(element.getAttributes().getNamedItem("start_pos").getNodeValue());
                    Position end_pos = new Position(element.getAttributes().getNamedItem("end_pos").getNodeValue());
                    data.addElement((GridElement)(new Board(start_pos, end_pos)));
                }else if(element.getNodeName().equals("given")){
                    Position pos = new Position(element.getAttributes().getNamedItem("pos").getNodeValue());
                    int value = Integer.parseInt(element.getAttributes().getNamedItem("value").getNodeValue());
                    data.addElement((GridElement)(new Given(pos, value)));
                }else if(element.getNodeName().equals("givens")){
                    data.addGivens(element.getFirstChild().getNodeValue());
                }else if(element.getNodeName().equals("house")){
                    NodeList cells = element.getChildNodes();
                    ArrayList<Position> cellPositions = new ArrayList<Position>();
                    for(int j=0; j<cells.getLength(); j++){
                        Node cell = cells.item(j);
                        if(!cell.getNodeName().equals("cell")) continue;
                        cellPositions.add(new Position(cell.getAttributes().getNamedItem("pos").getNodeValue()));
                    }
                    data.addElement((GridElement)(new House(cellPositions)));
                }else if(element.getNodeName().equals("blockset")){
                    data.addBlockset(element.getFirstChild().getNodeValue());
                }
            }
            if(solution != null){
                Node solutionData = null;
                NodeList helpers = null;
                for(int i=0; i<solution.getLength(); i++){
                    if(solution.item(i).getNodeName().equals("data")){
                        solutionData = solution.item(i);
                    }else if(solution.item(i).getNodeName().equals("helpers")){
                        helpers = solution.item(i).getChildNodes();
                    }
                }
                if(solutionData != null){
                    data.setGridValues(solutionData.getFirstChild().getNodeValue());
                }
                if(helpers != null){
                    for(int i=0; i<helpers.getLength(); i++){
                        if(helpers.item(i).getNodeName().equals("colors")){
                            NodeList colors = helpers.item(i).getChildNodes();
                            for(int j=0; j<colors.getLength(); j++){
                                if(colors.item(j).getNodeName().equals("color")){
                                    NamedNodeMap color = colors.item(j).getAttributes();
                                    Color value = Utils.getColorFromHTML(color.getNamedItem("value").getNodeValue());
                                    Position pos = new Position(color.getNamedItem("pos").getNodeValue());
                                    Node clue_value = color.getNamedItem("pencilmark_value");
                                    if(clue_value == null){
                                        data.getCell(pos).setColoring(value);
                                    }else{
                                        int clue_value_int = Integer.parseInt(clue_value.getNodeValue(), Character.MAX_RADIX);
                                        data.getCell(pos).setPencilmarkColoring(clue_value_int, value);
                                    }   
                                }
                            }
                        }else if(helpers.item(i).getNodeName().equals("pencilmarks")){
                            Node element = helpers.item(i);
                            data.addPencilmarks(element.getFirstChild().getNodeValue());
                        }
                    }
                }
            }
            
            
        } catch (SAXException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch(Exception ex){
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return data;
    }
}
