//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public class ChatMessage extends TextComponentBase {
  public String getUnformattedComponentText() {
    return this.text;
  }
  
  public ITextComponent createCopy() {
    return (ITextComponent)new ChatMessage(this.text);
  }
  
  public ChatMessage(String paramString) {
    Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
    Matcher matcher = pattern.matcher(paramString);
    StringBuffer stringBuffer = new StringBuffer();
    while (matcher.find()) {
      String str = String.valueOf((new StringBuilder()).append("§").append(matcher.group().substring(1)));
      matcher.appendReplacement(stringBuffer, str);
    } 
    matcher.appendTail(stringBuffer);
    this.text = stringBuffer.toString();
  }
}
