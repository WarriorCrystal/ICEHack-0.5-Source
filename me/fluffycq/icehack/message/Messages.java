//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.message;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public class Messages {
  public static void sendMessage(String paramString) {
    sendRawChatMessage(paramString);
  }
  
  public static void sendRawChatMessage(String paramString) {
    if (Minecraft.getMinecraft() != null && (Minecraft.getMinecraft()).player != null && (Minecraft.getMinecraft()).world != null)
      (Minecraft.getMinecraft()).player.sendMessage((ITextComponent)new ChatMessage(paramString)); 
  }
  
  public static void sendStringChatMessage(String[] paramArrayOfString) {
    sendChatMessage("");
    for (String str : paramArrayOfString)
      sendRawChatMessage(str); 
  }
  
  public static void sendChatMessage(String paramString) {
    sendRawChatMessage(String.valueOf((new StringBuilder()).append("&b[ICEHack] &r").append(paramString)));
  }
  
  static {
  
  }
}
