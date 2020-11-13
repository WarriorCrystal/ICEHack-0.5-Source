//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import java.util.ArrayList;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

public class ChatSuffix extends Module {
  public String getMode(String paramString) {
    String str = "";
    switch (paramString) {
      case "ICEHack":
        str = this.ICEHACK;
        break;
      case "AnvilGod":
        str = this.ANVILGOD;
        break;
      case "SpleefGod":
        str = this.SPLEEFGOD;
        break;
    } 
    return str;
  }
  
  public ChatSuffix() {
    super("ChatSuffix", 0, Category.MISC);
    this.listener = new Listener(paramSend -> {
          if (paramSend.getPacket() instanceof CPacketChatMessage) {
            String str = ((CPacketChatMessage)paramSend.getPacket()).getMessage();
            if (str.startsWith("/") && !this.commands.getValBoolean())
              return; 
            str = String.valueOf((new StringBuilder()).append(str).append(getMode(this.mode.getValString())));
            if (str.length() >= 256)
              str = str.substring(0, 256); 
            ((CPacketChatMessage)paramSend.getPacket()).message = str;
          } 
        }new java.util.function.Predicate[0]);
    this.commands = new Setting("CMD", this, false);
    this.modes.add("ICEHack");
    this.modes.add("AnvilGod");
    this.modes.add("SpleefGod");
    this.mode = new Setting("Mode", this, "ICEHack", this.modes);
  }
}
