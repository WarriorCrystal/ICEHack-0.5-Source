//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.command.commands;

import java.util.List;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.command.Command;
import net.minecraft.client.gui.GuiScreen;

public class HudEditor extends Command {
  public void handleCommand(String paramString, List<String> paramList) {
    this.mc.displayGuiScreen((GuiScreen)ICEHack.hudeditor);
  }
  
  public HudEditor() {
    this.aliases.add("hud");
    this.aliases.add("he");
    this.desc = "Toggle and edit the position of HUD Elements.";
  }
  
  static {
  
  }
}
