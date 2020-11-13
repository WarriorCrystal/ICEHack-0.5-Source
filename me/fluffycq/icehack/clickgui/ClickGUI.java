//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.element.Panel;
import me.fluffycq.icehack.module.Category;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class ClickGUI extends GuiScreen {
  public void mouseReleased(int paramInt1, int paramInt2, int paramInt3) {
    for (Panel panel : this.panels)
      panel.mouseRelease(paramInt1, paramInt2); 
  }
  
  public void handleMouseInput() throws IOException {
    if (Mouse.getEventDWheel() > 0)
      for (Panel panel : this.panels)
        panel.setY(panel.y + 15);  
    if (Mouse.getEventDWheel() < 0)
      for (Panel panel : this.panels)
        panel.setY(panel.y - 15);  
    super.handleMouseInput();
  }
  
  public void mouseClicked(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    boolean bool = false;
    for (Panel panel : this.panels) {
      if (panel.hoveringCategory(paramInt1, paramInt2) && !bool) {
        bool = true;
        panel.categoryClick(paramInt3, paramInt1, paramInt2);
      } 
      panel.mouseClicked(paramInt3, paramInt1, paramInt2);
    } 
  }
  
  public ClickGUI() {
    for (Category category : Category.values()) {
      if (ICEHack.fevents.moduleManager.hasModules(category)) {
        Panel panel = new Panel(category, this.x, 1);
        this.panels.add(panel);
        this.x += panel.cWidth + 5;
      } 
    } 
    ICEHack.fevents.moduleManager.getModule("HUD").enable();
  }
  
  public void drawScreen(int paramInt1, int paramInt2, float paramFloat) {
    drawDefaultBackground();
    for (Panel panel : this.panels)
      panel.drawPanel(paramInt1, paramInt2); 
    this.mx = paramInt1;
    this.my = paramInt2;
    super.drawScreen(paramInt1, paramInt2, paramFloat);
  }
  
  public void onGuiClosed() {
    ICEHack.fevents.moduleManager.getModule("ClickGUI").disable();
    ICEHack.fevents.moduleManager.getModule("HUD").enable();
  }
  
  protected void keyTyped(char paramChar, int paramInt) throws IOException {
    if (paramInt == 1) {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (this.mc.currentScreen == null)
        this.mc.setIngameFocus(); 
    } 
    for (Panel panel : this.panels)
      panel.keyTyped(paramChar, paramInt); 
  }
  
  public void initGui() {
    super.initGui();
  }
}
