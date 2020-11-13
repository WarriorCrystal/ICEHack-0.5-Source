//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import java.util.ArrayList;
import me.fluffycq.icehack.DiscordPresence;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;

public class Discord extends Module {
  public void onEnable() {
    DiscordPresence.start();
  }
  
  public Discord() {
    super("DiscordRPC", 0, Category.MISC);
    this.set1.add("Ver");
    this.set1.add("World");
    this.set1.add("Name");
    this.set1.add("Health");
    this.set1.add("Ip");
    this.set1.add("Invite");
    this.set1.add("None");
    this.set2.add("Ver");
    this.set2.add("World");
    this.set2.add("Name");
    this.set2.add("Health");
    this.set2.add("Ip");
    this.set2.add("Invite");
    this.set2.add("None");
    this.line1 = new Setting("Line1", this, "Ver", this.set1);
    this.line2 = new Setting("Line2", this, "Ip", this.set2);
  }
  
  public void onUpdate() {
    if (startTime == 0L)
      startTime = System.currentTimeMillis(); 
    if (startTime + 10000L <= System.currentTimeMillis())
      startTime = System.currentTimeMillis(); 
  }
  
  public String getLine(String paramString) {
    switch (paramString) {
      case "Ver":
        return "ICEHack b1.5";
      case "World":
        return mc.isIntegratedServerRunning() ? "Singleplayer" : ((mc.getCurrentServerData() != null) ? "Multiplayer" : "Main Menu");
      case "Name":
        return (mc.player != null) ? mc.player.getName() : "Logged out";
      case "Health":
        return (mc.player != null) ? String.valueOf((new StringBuilder()).append((int)mc.player.getHealth()).append(" hp")) : "0 hp";
      case "Ip":
        return (mc.getCurrentServerData() != null) ? (mc.getCurrentServerData()).serverIP : "Offline";
      default:
        return "";
      case "Invite":
        break;
    } 
    return "https://discord.gg/8w9XWH6";
  }
}
