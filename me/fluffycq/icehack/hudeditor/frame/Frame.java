package me.fluffycq.icehack.hudeditor.frame;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.util.UIUtil;

public class Frame {
  public void mouseClicked(int paramInt1, int paramInt2, int paramInt3) {
    if (isHovering(paramInt2, paramInt3) && paramInt1 == 0 && this.parent.isEnabled())
      this.dragging = true; 
    if (isHovering(paramInt2, paramInt3) && paramInt1 == 1 && this.parent.isEnabled())
      this.extended = !this.extended; 
  }
  
  public void drawFrame(int paramInt1, int paramInt2) {}
  
  public void handleDrag(int paramInt1, int paramInt2) {
    if (this.dragging) {
      this.x = paramInt1;
      this.y = paramInt2;
      if (this.x > UIUtil.scaleX(UIUtil.ScreenPos.BOTTOM_RIGHT, this.width, 0, 0, 1))
        this.x = UIUtil.scaleX(UIUtil.ScreenPos.BOTTOM_RIGHT, this.width, 0, 0, 1) - 1; 
      if (this.y > UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, this.height, 0, 0, 1))
        this.y = UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, this.height, 0, 0, 1) - 1; 
    } 
  }
  
  public void onRender() {}
  
  public boolean isHovering(int paramInt1, int paramInt2) {
    return (paramInt1 >= this.x && paramInt1 <= this.x + this.width && paramInt2 >= this.y && paramInt2 <= this.y + this.height);
  }
  
  public void mouseRelease(int paramInt1, int paramInt2) {
    if (this.dragging == true) {
      this.x = (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble() * Math.round((this.x / (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble()));
      this.y = (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble() * Math.round((this.y / (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble()));
      this.dragging = false;
    } 
  }
}
