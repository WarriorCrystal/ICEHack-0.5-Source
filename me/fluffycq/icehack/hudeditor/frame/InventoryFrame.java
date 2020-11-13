package me.fluffycq.icehack.hudeditor.frame;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Module;

public class InventoryFrame extends Frame {
  static {
  
  }
  
  public InventoryFrame(String paramString, int paramInt1, int paramInt2, Module paramModule) {
    this.title = paramString;
    this.x = paramInt1;
    this.y = paramInt2;
    this.parent = paramModule;
    this.dragging = false;
    this.extended = true;
  }
  
  public void drawFrame(int paramInt1, int paramInt2) {
    int i;
    handleDrag(paramInt1, paramInt2);
    this.width = 162;
    this.height = 54;
    if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
      i = Color.getHSBColor((float)(System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
    } else {
      i = (new Color((int)ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
    } 
    GuiUtil.drawHorizontalLine(this.x, this.x + 162 - 1, this.y, i);
    GuiUtil.drawHorizontalLine(this.x, this.x + 162 - 1, this.y + 53, i);
    GuiUtil.drawVerticalLine(this.x, this.y + 54, this.y, i);
    GuiUtil.drawVerticalLine(this.x + 162 - 1, this.y + 54, this.y, i);
  }
}
