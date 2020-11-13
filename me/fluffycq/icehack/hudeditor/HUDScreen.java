//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.hudeditor;

import java.io.IOException;
import java.util.ArrayList;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.hudeditor.frame.CombatInfo;
import me.fluffycq.icehack.hudeditor.frame.Frame;
import me.fluffycq.icehack.hudeditor.frame.InventoryFrame;
import net.minecraft.client.gui.GuiScreen;

public class HUDScreen extends GuiScreen {
  public void drawScreen(int paramInt1, int paramInt2, float paramFloat) {
    for (Frame frame : this.frames) {
      if (frame.parent.isEnabled()) {
        frame.drawFrame(paramInt1, paramInt2);
        frame.parent.onRender();
      } 
    } 
    this.mx = paramInt1;
    this.my = paramInt2;
    super.drawScreen(paramInt1, paramInt2, paramFloat);
  }
  
  public void mouseReleased(int paramInt1, int paramInt2, int paramInt3) {
    for (Frame frame : this.frames)
      frame.mouseRelease(paramInt1, paramInt2); 
  }
  
  public void mouseClicked(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    for (Frame frame : this.frames)
      frame.mouseClicked(paramInt3, paramInt1, paramInt2); 
  }
  
  public HUDScreen() {
    this.frames.add(new CombatInfo("CombatInfo", 0, 0, ICEHack.fevents.moduleManager.getModule("PvPInfo")));
    this.frames.add(new InventoryFrame("Inventory", 0, 0, ICEHack.fevents.moduleManager.getModule("Inventory")));
  }
  
  public void initGui() {
    super.initGui();
  }
}
