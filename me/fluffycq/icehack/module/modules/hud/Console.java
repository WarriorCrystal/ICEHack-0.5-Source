//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.modules.screen.ConsoleScreen;
import net.minecraft.client.gui.GuiScreen;

public class Console extends Module {
  static {
  
  }
  
  public void onToggle(boolean paramBoolean) {
    setState(false);
    mc.displayGuiScreen((GuiScreen)new ConsoleScreen());
  }
  
  public Console() {
    super("Console", 0, Category.HUD);
  }
}
