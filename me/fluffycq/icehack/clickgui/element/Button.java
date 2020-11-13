//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.clickgui.element;

import java.awt.Color;
import java.util.ArrayList;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.setting.eSetting;
import me.fluffycq.icehack.clickgui.util.ColorUtil;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.clickgui.util.PanelUtil;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.UIUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Button {
  public void setExtended(boolean paramBoolean) {
    this.extended = paramBoolean;
    if (this.extended) {
      byte b = 15;
      for (eSetting eSetting : this.settings) {
        eSetting.x = this.x;
        eSetting.y = this.y + b;
        eSetting.visible = true;
        b += 15;
      } 
    } else {
      for (eSetting eSetting : this.settings) {
        eSetting.x = 0;
        eSetting.y = 0;
        eSetting.visible = false;
      } 
    } 
  }
  
  public boolean isHovering(int paramInt1, int paramInt2) {
    return !this.visible ? false : ((paramInt1 >= this.x && paramInt1 <= this.x + this.width && paramInt2 >= this.y && paramInt2 <= this.y + this.height));
  }
  
  public int panelY() {
    return (PanelUtil.getPanel((this.parent.getCategory()).categoryName)).y + 15;
  }
  
  public boolean isToggled() {
    return this.parent.getState();
  }
  
  public void initSettings() {
    if (ICEHack.setmgr.getSettingsByMod(this.parent) != null)
      for (Setting setting : ICEHack.setmgr.getSettingsByMod(this.parent))
        this.settings.add(new eSetting(setting, 0, 0, 100, 15));  
  }
  
  public Button(Panel paramPanel, Module paramModule, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString) {
    this.parent = paramModule;
    this.panel = paramPanel;
    this.x = paramInt1;
    this.y = paramInt2;
    this.width = paramInt3;
    this.height = paramInt4;
    this.text = paramString;
    this.enabled = false;
    this.visible = true;
    this.extended = false;
    initSettings();
  }
  
  public void drawButton(int paramInt1, int paramInt2) {
    if (this.y > UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 15, 0, 0, 1) + 15 || this.y < panelY()) {
      this.visible = false;
    } else if (this.y < UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 15, 0, 0, 1) + 15 || this.y > panelY()) {
      this.visible = true;
    } 
    Color color = new Color(ColorUtil.getGuiColor());
    if (this.visible) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GuiUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, isToggled() ? color.darker().getRGB() : this.idleBG);
      GuiUtil.drawString(this.text, this.x + 3, this.y + (this.height - 8) / 2, -1);
    } 
    if (this.extended && this.visible)
      for (eSetting eSetting : this.settings) {
        if (eSetting.visible)
          eSetting.drawSetting(paramInt1, paramInt2); 
      }  
  }
  
  public int getSettingsSpace() {
    return this.settings.size() * 15;
  }
  
  public void mouseClicked(int paramInt1, int paramInt2, int paramInt3) {
    if (isHovering(paramInt2, paramInt3) && paramInt1 == 0 && !this.parent.getName().equalsIgnoreCase("HUD"))
      this.parent.setState(!this.parent.getState()); 
  }
}
