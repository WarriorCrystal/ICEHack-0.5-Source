package me.fluffycq.icehack.clickgui.util;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.element.Panel;

public class PanelUtil {
  static {
  
  }
  
  public static Panel getPanel(String paramString) {
    Panel panel = null;
    for (Panel panel1 : ICEHack.clickgui.panels) {
      if (panel1.title.equalsIgnoreCase(paramString)) {
        panel = panel1;
        break;
      } 
    } 
    return panel;
  }
}
