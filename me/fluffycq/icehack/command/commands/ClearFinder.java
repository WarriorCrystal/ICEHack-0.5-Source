package me.fluffycq.icehack.command.commands;

import java.util.List;
import me.fluffycq.icehack.command.Command;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.modules.exploit.EGapFinder;

public class ClearFinder extends Command {
  static {
  
  }
  
  public ClearFinder() {
    this.aliases.add("finderclear");
    this.aliases.add("clearf");
    this.desc = "Clear EGapFinder waypoints";
  }
  
  public void handleCommand(String paramString, List<String> paramList) {
    Messages.sendChatMessage("&aEGapFinder waypoints cleared.");
    EGapFinder.boxes.clear();
    EGapFinder.sent.clear();
  }
}
