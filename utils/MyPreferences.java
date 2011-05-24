package utils;

import java.awt.Color;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 *
 * @author zeroos
 */
public class MyPreferences {

    private static MyPreferences pref;
    private HashMap map;
    EventListenerList changeListenerList = new EventListenerList();

    private MyPreferences() {
        map = new HashMap();
    }

    static public MyPreferences getInstance() {
        if (pref == null) {
            pref = new MyPreferences();
            pref.setInt("fieldW", 30);
            pref.setInt("fieldH", 30);
            pref.setInt("offsetX", 15);
            pref.setInt("offsetY", 15);
            pref.setInt("lineColor", new Color(0x77,0x77,0x77).getRGB());
            pref.setInt("fieldColor", new Color(0xff,0xff,0xff).getRGB());
            pref.setInt("bgColor", new Color(0xdd,0xdd,0xdd).getRGB());
            pref.setInt("givenValueColor", new Color(0x44,0x44,0xaa).getRGB());
            pref.setInt("cellValueColor", new Color(0x22,0x22,0x22).getRGB());
            pref.setInt("warningColor", new Color(0xaa,0x00,0x00).getRGB());
            pref.setInt("activePencilmarkColor", new Color(0x22, 0x22, 0x22).getRGB());
            pref.setInt("inactivePencilmarkColor", new Color(0xaa, 0xaa, 0xaa).getRGB());
            pref.setInt("minPencilmarkSize", 15);
            pref.setInt("buttonHeight", 20);
        }
        return pref;
    }

    public int getInt(String key) {
        Object val = map.get(key);
        if (val != null) {
            return ((Integer) val).intValue();
        } else {
            throw new NullPointerException();
        }
    }

    public int getInt(String key, int def) {
        Object val = map.get(key);
        if (val != null) {
            return ((Integer) val).intValue();
        } else {
            return def;
        }
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, String def) {
        String val = (String) map.get(key);
        if (val != null) {
            return val;
        } else {
            return def;
        }
    }

    public void setInt(String key, int val) {
        map.put(key, val);
        fireChangeEvent();
    }

    public void addChangeListener(ChangeListener l) {
        changeListenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeListenerList.remove(ChangeListener.class, l);
    }

    public void fireChangeEvent() {
        ChangeListener listeners[] =
                changeListenerList.getListeners(ChangeListener.class);
        for (ChangeListener l : listeners) {
            l.stateChanged(new ChangeEvent(this));
        }
    }
}
