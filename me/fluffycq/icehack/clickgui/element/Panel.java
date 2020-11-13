package me.fluffycq.icehack.clickgui.element;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.setting.eSetting;
import me.fluffycq.icehack.clickgui.util.ColorUtil;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.util.UIUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Panel {
  public void categoryClick(int paramInt1, int paramInt2, int paramInt3) {
    if (hoveringCategory(paramInt2, paramInt3) && paramInt1 == 1)
      toggleExtend(); 
    if (hoveringCategory(paramInt2, paramInt3) && paramInt1 == 0)
      this.dragging = true; 
  }
  
  public void mouseRelease(int paramInt1, int paramInt2) {
    if (this.dragging == true)
      this.dragging = false; 
    for (Button button : this.modButtons) {
      for (eSetting eSetting : button.settings)
        eSetting.mouseRelease(paramInt1, paramInt2); 
    } 
    int i = this.y;
    this.x = (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble() * Math.round((this.x / (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble()));
    this.y = (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble() * Math.round((this.y / (int)ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble()));
    for (Button button : this.modButtons) {
      button.x = this.x;
      button.y += this.y - i;
      for (eSetting eSetting : button.settings) {
        eSetting.x = this.x;
        eSetting.y += this.y - i;
      } 
    } 
  }
  
  public void toggleExtend() {
    for (Button button : this.modButtons) {
      if (button.visible) {
        this.extended = false;
      } else {
        this.extended = true;
      } 
      button.visible = !button.visible;
    } 
  }
  
  public void drawCategoryButton() {
    int i = ColorUtil.getGuiColor();
    GuiUtil.drawHorizontalLine(this.x, this.x + this.cWidth - 1, this.y, i);
    GuiUtil.drawHorizontalLine(this.x, this.x + this.cWidth - 1, this.y + 14, i);
    GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y, i);
    GuiUtil.drawVerticalLine(this.x + this.cWidth - 1, this.y + 15, this.y, i);
    GuiUtil.drawRect(this.x + 1, this.y + 1, this.x + this.cWidth - 1, this.y + 14, this.defaultBG);
    GuiUtil.drawCenteredString(this.category.categoryName, this.x + this.cWidth / 2, this.y + 3, -1);
  }
  
  public boolean hoveringPanel(int paramInt) {
    return (paramInt >= this.x && paramInt <= this.x + this.cWidth);
  }
  
  public void mouseClicked(int paramInt1, int paramInt2, int paramInt3) {
    for (Button button : this.modButtons) {
      if (button.isHovering(paramInt2, paramInt3) && paramInt1 == 1) {
        for (Button button1 : this.modButtons) {
          if (!button.extended) {
            if (button1.y + button1.height > button.y + button.height)
              button1.y += button.getSettingsSpace(); 
            for (eSetting eSetting : button1.settings) {
              if (eSetting.y + eSetting.height > button.y + button.height)
                eSetting.y += button.getSettingsSpace(); 
            } 
            continue;
          } 
          if (button1.y + button1.height > button.y + button.height)
            button1.y -= button.getSettingsSpace(); 
          for (eSetting eSetting : button1.settings) {
            if (eSetting.y + eSetting.height > button.y + button.height)
              eSetting.y -= button.getSettingsSpace(); 
          } 
        } 
        button.setExtended(!button.extended);
      } 
    } 
    for (Button button : this.modButtons) {
      if (button.isHovering(paramInt2, paramInt3) && paramInt1 != 1)
        button.mouseClicked(paramInt1, paramInt2, paramInt3); 
      for (eSetting eSetting : button.settings)
        eSetting.mouseClicked(paramInt2, paramInt3, paramInt1); 
    } 
  }
  
  public void keyTyped(char paramChar, int paramInt) throws IOException {
    for (Button button : this.modButtons) {
      for (eSetting eSetting : button.settings)
        eSetting.keyTyped(paramChar, paramInt); 
    } 
  }
  
  public void setY(int paramInt) {
    int i = this.y;
    this.y = paramInt;
    for (Button button : this.modButtons) {
      button.y += paramInt - i;
      for (eSetting eSetting : button.settings)
        eSetting.y += paramInt - i; 
    } 
  }
  
  public void initWidth() {
    this.cWidth = 100;
  }
  
  public void addModules() {
    for (Module module : ICEHack.fevents.moduleManager.moduleList) {
      if (module.getCategory().equals(this.category)) {
        this.modButtons.add(new Button(this, module, this.x, this.y + this.bottomY, this.cWidth, 15, module.name));
        this.bottomY += 15;
      } 
    } 
  }
  
  public Panel(Category paramCategory, int paramInt1, int paramInt2) {
    this.category = paramCategory;
    this.x = paramInt1;
    this.y = paramInt2;
    this.dragging = false;
    this.extended = true;
    this.title = paramCategory.categoryName;
    initWidth();
  }
  
  public void drawPanel(int paramInt1, int paramInt2) {
    drawCategoryButton();
    for (Button button : this.modButtons)
      button.drawButton(paramInt1, paramInt2); 
    if (this.dragging) {
      int i = this.y;
      this.x = paramInt1;
      this.y = paramInt2;
      if (this.x > UIUtil.scaleX(UIUtil.ScreenPos.BOTTOM_RIGHT, 100, 0, 0, 1))
        this.x = UIUtil.scaleX(UIUtil.ScreenPos.BOTTOM_RIGHT, 100, 0, 0, 1) - 1; 
      if (this.y > UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 15, 0, 0, 1))
        this.y = UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 15, 0, 0, 1) - 1; 
      for (Button button : this.modButtons) {
        button.x = this.x;
        button.y += this.y - i;
        for (eSetting eSetting : button.settings) {
          eSetting.x = this.x;
          eSetting.y += this.y - i;
        } 
      } 
    } 
  }
  
  public boolean hoveringCategory(int paramInt1, int paramInt2) {
    return (paramInt1 >= this.x && paramInt1 <= this.x + this.cWidth && paramInt2 >= this.y && paramInt2 <= this.y + 15);
  }
}
