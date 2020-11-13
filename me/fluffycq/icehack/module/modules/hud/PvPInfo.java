//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.hud;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.font.CFontRenderer;
import me.fluffycq.icehack.hudeditor.frame.Frame;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.RainbowUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PvPInfo extends Module {
  public int getColor(int paramInt) {
    int i = -1;
    if (this.rainbow.getValBoolean()) {
      i = this.rutil.GetRainbowColorAt(paramInt);
    } else {
      i = (new Color((int)this.red.getValDouble(), (int)this.green.getValDouble(), (int)this.blue.getValDouble())).getRGB();
    } 
    return i;
  }
  
  public int getHeight() {
    return ICEHack.fevents.moduleManager.getModule("HUD").getSetting("CustomFont").getValBoolean() ? this.fontRenderer.getHeight() : mc.fontRenderer.FONT_HEIGHT;
  }
  
  public void onRender() {
    byte b = 0;
    initSetting(this.totemcount);
    initSetting(this.crystalcount);
    initSetting(this.gapcount);
    initSetting(this.obicount);
    initSetting(this.xpcount);
    this.rutil.OnRender();
    this.infocolor = getInfoColor();
    this.frame = ICEHack.hudeditor.frames.get(0);
    if (this.frame.extended) {
      this.infocolor = (new Color((int)this.redinfo.getValDouble(), (int)this.greeninfo.getValDouble(), (int)this.blueinfo.getValDouble())).getRGB();
      if (this.frame.x + 100 <= this.resolution.getScaledWidth() / 2 + 100) {
        int i = this.frame.y + 15;
        for (String str : this.info) {
          b += true;
          if (b >= 'ţ')
            b = 0; 
          int j = 0;
          for (byte b1 = 0; b1 < 45; b1++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(b1);
            if (itemStack.getItem().equals(getItem(str)) && str.equalsIgnoreCase("Gapple") && itemStack.getItemDamage() == 1)
              j += itemStack.stackSize; 
            if (itemStack.getItem().equals(getItem(str)) && !str.equalsIgnoreCase("Gapple"))
              j += itemStack.stackSize; 
          } 
          drawStringWithShadow(str, this.frame.x, i, getColor(b));
          drawStringWithShadow(String.valueOf(j), this.frame.x + getStringWidth(str) + (int)this.pixelwidth.getValDouble(), i, this.infocolor);
          i += 10;
        } 
      } 
      if (this.frame.x + 100 >= this.resolution.getScaledWidth() / 2 + 100) {
        int i = this.frame.y + 15;
        for (String str : this.info) {
          int j = 0;
          int k;
          for (k = 0; k < 45; k++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(k);
            if (itemStack.getItem().equals(getItem(str)) && str.equalsIgnoreCase("Gapple") && itemStack.getItemDamage() == 1)
              j += itemStack.stackSize; 
            if (itemStack.getItem().equals(getItem(str)) && !str.equalsIgnoreCase("Gapple"))
              j += itemStack.stackSize; 
          } 
          b += 20;
          if (b >= 'ţ')
            b = 0; 
          k = this.frame.x + 100 - getStringWidth(str) - (int)this.pixelwidth.getValDouble() - getStringWidth(String.valueOf(j));
          drawStringWithShadow(str, k, i, getColor(b));
          drawStringWithShadow(String.valueOf(j), k + getStringWidth(str) + (int)this.pixelwidth.getValDouble(), i, this.infocolor);
          i += 10;
        } 
      } 
    } 
  }
  
  public void drawStringWithShadow(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (ICEHack.fevents.moduleManager.getModule("HUD").getSetting("CustomFont").getValBoolean()) {
      this.fontRenderer.drawStringWithShadow(paramString, paramInt1, paramInt2, paramInt3);
    } else {
      mc.fontRenderer.drawStringWithShadow(paramString, paramInt1, paramInt2, paramInt3);
    } 
  }
  
  public PvPInfo() {
    super("PvPInfo", 0, Category.HUD);
  }
  
  public int getInfoColor() {
    return (new Color((int)this.redinfo.getValDouble(), (int)this.greeninfo.getValDouble(), (int)this.blueinfo.getValDouble())).getRGB();
  }
  
  public Item getItem(String paramString) {
    Item item = null;
    switch (paramString) {
      case "Totems":
        item = Items.TOTEM_OF_UNDYING;
        break;
      case "Crystals":
        item = Items.END_CRYSTAL;
        break;
      case "Gapples":
        item = Items.GOLDEN_APPLE;
        break;
      case "Obi":
        item = Item.getItemFromBlock(Blocks.OBSIDIAN);
        break;
      case "XP":
        item = Items.EXPERIENCE_BOTTLE;
        break;
    } 
    return item;
  }
  
  public int getStringWidth(String paramString) {
    return ICEHack.fevents.moduleManager.getModule("HUD").getSetting("CustomFont").getValBoolean() ? this.fontRenderer.getStringWidth(paramString) : mc.fontRenderer.getStringWidth(paramString);
  }
  
  public void initSetting(Setting paramSetting) {
    if (!this.info.contains(paramSetting.getName()) && paramSetting.getValBoolean())
      this.info.add(paramSetting.getName()); 
    if (this.info.contains(paramSetting.getName()) && !paramSetting.getValBoolean())
      this.info.remove(paramSetting.getName()); 
  }
}
