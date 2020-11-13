//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GUI extends Module {
  public void onToggle(boolean paramBoolean) {
    mc.displayGuiScreen((GuiScreen)ICEHack.clickgui);
  }
  
  public GUI() {
    super("ClickGUI", 205, Category.HUD);
  }
}
