package me.fluffycq.icehack.clickgui.util;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Module;

public class ColorUtil {
  static {
  
  }
  
  public static int getGuiColor() {
    int i = -1;
    if (ICEHack.fevents.moduleManager.getModule("ClickGUI") != null) {
      Module module = ICEHack.fevents.moduleManager.getModule("ClickGUI");
      if (module.getSetting("Rainbow").getValBoolean()) {
        i = Color.getHSBColor((float)(System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
      } else {
        i = (new Color((int)module.getSetting("Red").getValDouble(), (int)module.getSetting("Green").getValDouble(), (int)module.getSetting("Blue").getValDouble())).getRGB();
      } 
    } 
    return i;
  }
}
