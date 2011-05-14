package utils.buttons;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class AddPencilmarksButton extends utils.MyButton{
    public AddPencilmarksButton(ActionListener l){
            this();
            this.addActionListener(l);
    }
    public AddPencilmarksButton(){
            super("A");
            calcSize();
            setToolTipText(TR.t("Add all pencilmarks"));
    }
    private void calcSize(){
            setPreferredSize(new Dimension(fieldH, fieldH));
            setSize(getPreferredSize());
    }
}
