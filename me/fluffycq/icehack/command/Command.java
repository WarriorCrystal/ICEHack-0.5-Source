package me.fluffycq.icehack.command;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class Command {
  public String getPre() {
    return this.cmd;
  }
  
  public void handleCommand(String paramString, List<String> paramList) {}
  
  public boolean argExists(List<String> paramList, int paramInt) {
    return !(paramList.size() - 1 < paramInt);
  }
}
