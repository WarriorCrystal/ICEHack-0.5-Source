//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.hud;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.font.CFontRenderer;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.RainbowUtil;
import me.fluffycq.icehack.util.UIUtil;

public class ModuleArray extends Module {
  public int getColor(int paramInt) {
    int i = -1;
    if (this.rainbow.getValBoolean()) {
      i = this.rutil.GetRainbowColorAt(paramInt);
    } else {
      i = (new Color((int)this.red.getValDouble(), (int)this.green.getValDouble(), (int)this.blue.getValDouble())).getRGB();
    } 
    return i;
  }
  
  public void initList(Module paramModule) {
    if (this.enabled.contains(paramModule) && (paramModule.isDisabled() || !paramModule.isVisible())) {
      this.enabled.remove(paramModule);
      Comparator<? super Module> comparator = (paramModule1, paramModule2) -> {
          String str1 = String.valueOf((new StringBuilder()).append(paramModule1.getName()).append(paramModule1.modInfo));
          String str2 = String.valueOf((new StringBuilder()).append(paramModule2.getName()).append(paramModule2.modInfo));
          float f = (getStringWidth(str2) - getStringWidth(str1));
          return (f != 0.0F) ? (int)f : str2.compareTo(str1);
        };
      this.enabled.sort(comparator);
    } 
    if (!this.enabled.contains(paramModule) && paramModule.isEnabled() && !paramModule.getCategory().equals(Category.HUD) && paramModule.isVisible()) {
      this.enabled.add(paramModule);
      Comparator<? super Module> comparator = (paramModule1, paramModule2) -> {
          String str1 = String.valueOf((new StringBuilder()).append(paramModule1.getName()).append(paramModule1.modInfo));
          String str2 = String.valueOf((new StringBuilder()).append(paramModule2.getName()).append(paramModule2.modInfo));
          float f = (getStringWidth(str2) - getStringWidth(str1));
          return (f != 0.0F) ? (int)f : str2.compareTo(str1);
        };
      this.enabled.sort(comparator);
    } 
  }
  
  public void drawStringWithShadow(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (ICEHack.fevents.moduleManager.getModule("HUD").getSetting("CustomFont").getValBoolean()) {
      this.fontRenderer.drawStringWithShadow(paramString, paramInt1, paramInt2, paramInt3);
    } else {
      mc.fontRenderer.drawStringWithShadow(paramString, paramInt1, paramInt2, paramInt3);
    } 
  }
  
  public void onRender() {
    int i = 0;
    for (Module module : ICEHack.fevents.moduleManager.moduleList)
      initList(module); 
    this.rutil.OnRender();
    if (this.h.getValString().equalsIgnoreCase("Left")) {
      if (this.v.getValString().equalsIgnoreCase("Top")) {
        int j = (int)(1.0D + this.offsety.getValDouble());
        for (Module module : this.enabled) {
          i = (int)(i + this.rainbowspeed.getValDouble());
          if (i >= 355)
            i = 0; 
          drawStringWithShadow(module.getName(), 0, j, getColor(i));
          drawStringWithShadow(module.getModInfo(), 2 + getStringWidth(module.getName()), j, getInfoColor());
          j += 10;
        } 
      } else if (this.v.getValString().equalsIgnoreCase("Bottom")) {
        int j = (int)((UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 0, 0, 0, 1) - this.enabled.size() * getHeight() - getHeight() - 1) - this.offsety.getValDouble());
        if (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat)
          j = j - getHeight() - getHeight() / 2; 
        for (int k = this.enabled.size() - 1; k >= 0; k--) {
          i = (int)(i + this.rainbowspeed.getValDouble());
          if (i >= 355)
            i = 0; 
          drawStringWithShadow(((Module)this.enabled.get(k)).getName(), 0, j, getColor(i));
          drawStringWithShadow(((Module)this.enabled.get(k)).getModInfo(), 2 + getStringWidth(((Module)this.enabled.get(k)).getName()), j, getInfoColor());
          j += 10;
        } 
      } 
    } else if (this.h.getValString().equalsIgnoreCase("Right")) {
      if (this.v.getValString().equalsIgnoreCase("Top")) {
        int j = (int)(1.0D + this.offsety.getValDouble());
        for (Module module : this.enabled) {
          i = (int)(i + this.rainbowspeed.getValDouble());
          if (i >= 355)
            i = 0; 
          int k = UIUtil.scaleX(UIUtil.ScreenPos.BOTTOM_RIGHT, 0, 0, 0, 1) - getStringWidth(module.getModInfo()) + getStringWidth(module.getName()) - 1;
          drawStringWithShadow(module.getName(), k, j, getColor(i));
          drawStringWithShadow(module.getModInfo(), k + 2 + getStringWidth(module.getName()), j, getInfoColor());
          j += 10;
        } 
      } else if (this.v.getValString().equalsIgnoreCase("Bottom")) {
        int j = (int)((UIUtil.scaleY(UIUtil.ScreenPos.BOTTOM_RIGHT, 0, 0, 0, 1) - this.enabled.size() * getHeight() - getHeight() - 1) - this.offsety.getValDouble());
        if (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat)
          j = j - getHeight() - getHeight() / 2; 
        for (int k = this.enabled.size() - 1; k >= 0; k--) {
          i = (int)(i + this.rainbowspeed.getValDouble());
          if (i >= 355)
            i = 0; 
          int m = UIUtil.scaleX(UIUtil.ScreenPos.BOTTOM_RIGHT, 0, 0, 0, 1) - getStringWidth(((Module)this.enabled.get(k)).getModInfo()) + getStringWidth(((Module)this.enabled.get(k)).getName()) - 1;
          drawStringWithShadow(((Module)this.enabled.get(k)).getName(), m, j, getColor(i));
          drawStringWithShadow(((Module)this.enabled.get(k)).getModInfo(), m + 2 + getStringWidth(((Module)this.enabled.get(k)).getName()), j, getInfoColor());
          j += 10;
        } 
      } 
    } 
  }
  
  public ModuleArray() {
    super("ArrayList", 0, Category.HUD);
    this.hArrangement.add("Left");
    this.hArrangement.add("Right");
    this.vArrangement.add("Top");
    this.vArrangement.add("Bottom");
    this.h = new Setting("Side", this, "Left", this.hArrangement);
    this.v = new Setting("Array V", this, "Bottom", this.vArrangement);
    this.offsety = new Setting("OffsetY", this, 0.0D, 0.0D, 500.0D, true);
  }
  
  public int getInfoColor() {
    return (new Color((int)this.redinfo.getValDouble(), (int)this.greeninfo.getValDouble(), (int)this.blueinfo.getValDouble())).getRGB();
  }
  
  public int getStringWidth(String paramString) {
    return ICEHack.fevents.moduleManager.getModule("HUD").getSetting("CustomFont").getValBoolean() ? this.fontRenderer.getStringWidth(paramString) : mc.fontRenderer.getStringWidth(paramString);
  }
  
  public void onUpdate() {
    if (this.rutil.getTimer() != this.rainbowsped.getMax() - this.rainbowsped.getValDouble())
      this.rutil.SetTimer((int)(this.rainbowsped.getMax() - this.rainbowsped.getValDouble())); 
    for (Module module : ICEHack.fevents.moduleManager.moduleList)
      initList(module); 
  }
  
  public int getHeight() {
    return ICEHack.fevents.moduleManager.getModule("HUD").getSetting("CustomFont").getValBoolean() ? this.fontRenderer.getHeight() : mc.fontRenderer.FONT_HEIGHT;
  }
  
  public void onDisable() {
    for (Module module : ICEHack.fevents.moduleManager.moduleList)
      initList(module); 
  }
}
