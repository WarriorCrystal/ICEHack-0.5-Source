//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.screen;

import java.awt.Color;
import java.io.IOException;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.command.CommandManager;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.TabCompleter;
import org.lwjgl.input.Keyboard;

public class ConsoleScreen extends GuiChat {
  public void initGui() {
    int i;
    Keyboard.enableRepeatEvents(true);
    this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
    this.inputField = new GuiTextField(0, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
    this.inputField.setMaxStringLength(256);
    this.inputField.setEnableBackgroundDrawing(false);
    this.inputField.setFocused(true);
    this.inputField.setText("");
    this.inputField.setCanLoseFocus(false);
    if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
      i = Color.getHSBColor((float)(System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
    } else {
      i = (new Color((int)ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
    } 
    this.inputField.setTextColor(i);
    this.tabCompleter = (TabCompleter)new GuiChat.ChatTabCompleter(this.inputField);
  }
  
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    this.mc.ingameGUI.getChatGUI().resetScroll();
  }
  
  protected void keyTyped(char paramChar, int paramInt) throws IOException {
    this.tabCompleter.resetRequested();
    if (paramInt == 15) {
      this.tabCompleter.complete();
    } else {
      this.tabCompleter.resetDidComplete();
    } 
    if (paramInt == 1) {
      this.mc.displayGuiScreen((GuiScreen)null);
    } else if (paramInt != 28 && paramInt != 156) {
      if (paramInt == 200) {
        getSentHistory(-1);
      } else if (paramInt == 208) {
        getSentHistory(1);
      } else if (paramInt == 201) {
        this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
      } else if (paramInt == 209) {
        this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
      } else {
        this.inputField.textboxKeyTyped(paramChar, paramInt);
      } 
    } else {
      String str = this.inputField.getText();
      System.out.println(String.valueOf((new StringBuilder()).append("ICEHack processed command using text: ").append(str)));
      sendChatMessage(str);
    } 
  }
  
  public void sendChatMessage(String paramString) {
    if (paramString.isEmpty())
      this.mc.displayGuiScreen((GuiScreen)null); 
    this.cmd.handleCMD(paramString);
  }
}
