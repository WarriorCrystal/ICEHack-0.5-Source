//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.clickgui.setting;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.ColorUtil;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.clickgui.util.PanelUtil;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.UIUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class eSetting {
  public void mouseClicked(int paramInt1, int paramInt2, int paramInt3) {
    if (this.visible && isHovering(paramInt1, paramInt2) && paramInt3 == 0) {
      if (this.setting.isCheck())
        this.setting.setValBoolean(!this.setting.getValBoolean()); 
      if (this.setting.isSlider())
        this.dragging = true; 
      if (this.setting.isBind() && !this.listening)
        this.listening = true; 
      if (this.setting.isCombo())
        if (this.setting.getOptions().indexOf(this.setting.getValString()) == this.setting.getOptions().size() - 1) {
          this.setting.setValString(this.setting.getOptions().get(0));
        } else {
          this.setting.setValString(this.setting.getOptions().get(this.setting.getOptions().indexOf(this.setting.getValString()) + 1));
        }  
    } 
  }
  
  public boolean isHovering(int paramInt1, int paramInt2) {
    return (paramInt1 >= this.x && paramInt1 <= this.x + this.width && paramInt2 >= this.y && paramInt2 <= this.y + this.height);
  }
  
  public void keyTyped(char paramChar, int paramInt) throws IOException {
    if (this.listening)
      if (paramInt == 42) {
        this.setting.getParentMod().setKey(0);
        this.listening = false;
      } else if (paramInt == 1) {
        this.listening = false;
      } else {
        this.setting.getParentMod().setKey(paramInt);
        this.listening = false;
      }  
  }
  
  public void drawSetting(int paramInt1, int paramInt2) {
    if (this.y > UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 15, 0, 0, 1) + 15 || this.y < (PanelUtil.getPanel((this.setting.getParentMod().getCategory()).categoryName)).y + 15) {
      this.visible = false;
    } else if (this.y < UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 15, 0, 0, 1) + 15 || this.y > (PanelUtil.getPanel((this.setting.getParentMod().getCategory()).categoryName)).y + 15) {
      this.visible = true;
    } 
    int i = ColorUtil.getGuiColor();
    if (this.visible) {
      if (this.dragging) {
        double d1 = Math.min(100, Math.max(0, paramInt1 - this.x));
        BigDecimal bigDecimal = new BigDecimal(d1 / 100.0D * (this.setting.getMax() - this.setting.getMin()) + this.setting.getMin());
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        double d2 = bigDecimal.doubleValue();
        this.setting.setValDouble(d2);
      } 
      if (this.setting.isCheck()) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
        if (this.setting.getValBoolean())
          GuiUtil.drawRect(this.x + 2, this.y, this.x + 100 - 2, this.y + this.height, i); 
        GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, i);
        GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, i);
        GuiUtil.drawString(this.setting.getName(), this.x + 3, this.y + (this.height - 8) / 2, -1);
        if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1)
          GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, i); 
      } 
      if (this.setting.isSlider()) {
        double d = this.setting.getValDouble() / this.setting.getMax();
        int j = this.width - 2;
        int k = (int)(j * d);
        GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
        if (this.setting.getValDouble() < 0.0D || this.setting.getValDouble() > 0.1D)
          GuiUtil.drawRect(this.x + 2, this.y, this.x + k - 2, this.y + this.height, i); 
        GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, i);
        GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, i);
        GuiUtil.drawString(String.valueOf((new StringBuilder()).append(this.setting.getName()).append(": ").append(String.valueOf(this.setting.getValDouble()))), this.x + 3, this.y + (this.height - 8) / 2, -1);
        if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1)
          GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, i); 
      } 
      if (this.setting.isBind()) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (((Setting)ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).get(0)).equals(this.setting) && ICEHack.setmgr.getSettingByMod("TopLine", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
          GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y, i);
          GuiUtil.drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height, -11382190);
        } else {
          GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
        } 
        GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, i);
        GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, i);
        if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1)
          GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, i); 
        if (!this.listening) {
          GuiUtil.drawString(String.valueOf((new StringBuilder()).append("Bind: ").append((this.setting.getKeyBind() > -1) ? Keyboard.getKeyName(this.setting.getKeyBind()) : "")), this.x + 3, this.y + (this.height - 8) / 2, -1);
        } else {
          GuiUtil.drawString("Bind: ...", this.x + 3, this.y + (this.height - 8) / 2, -1);
        } 
      } 
      if (this.setting.isCombo()) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
        GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, i);
        GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, i);
        GuiUtil.drawString(String.valueOf((new StringBuilder()).append(this.setting.getName()).append(": ").append(String.valueOf(this.setting.getValString()))), this.x + 3, this.y + (this.height - 8) / 2, -1);
        if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1)
          GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, i); 
      } 
    } 
  }
  
  public void mouseRelease(int paramInt1, int paramInt2) {
    if (this.setting.isSlider())
      this.dragging = false; 
  }
  
  public eSetting(Setting paramSetting, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.x = paramInt1;
    this.y = paramInt2;
    this.width = paramInt3;
    this.height = paramInt4;
    this.setting = paramSetting;
    this.visible = false;
    this.dragging = false;
    this.listening = false;
  }
}
