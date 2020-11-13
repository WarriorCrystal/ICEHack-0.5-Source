package me.fluffycq.icehack.hudeditor.frame;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Module;

public class CombatInfo extends Frame {
  public void drawFrame(int paramInt1, int paramInt2) {
    int i;
    handleDrag(paramInt1, paramInt2);
    this.height = 15;
    this.width = 100;
    if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
      i = Color.getHSBColor((float)(System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
    } else {
      i = (new Color((int)ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
    } 
    GuiUtil.drawHorizontalLine(this.x, this.x + 100 - 1, this.y, i);
    GuiUtil.drawHorizontalLine(this.x, this.x + 100 - 1, this.y + 14, i);
    GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y, i);
    GuiUtil.drawVerticalLine(this.x + 100 - 1, this.y + 15, this.y, i);
    GuiUtil.drawRect(this.x + 1, this.y + 1, this.x + 100 - 1, this.y + 14, this.defaultBG);
    GuiUtil.drawCenteredString("PvP Info", this.x + 50, this.y + 3, -1);
  }
  
  static {
  
  }
  
  public CombatInfo(String paramString, int paramInt1, int paramInt2, Module paramModule) {
    this.title = paramString;
    this.x = paramInt1;
    this.y = paramInt2;
    this.parent = paramModule;
    this.dragging = false;
    this.extended = true;
  }
}
