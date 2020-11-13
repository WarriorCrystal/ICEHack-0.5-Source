package me.fluffycq.icehack.command.commands;

import java.util.List;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.command.Command;
import me.fluffycq.icehack.message.Messages;

public class Help extends Command {
  static {
  
  }
  
  public void handleCommand(String paramString, List<String> paramList) {
    Messages.sendChatMessage("List of available commands in ICEHack:");
    for (Command command : ICEHack.cmdmanager.cmds)
      Messages.sendMessage(String.valueOf((new StringBuilder()).append("&a").append(command.cmd).append(" &7- ").append(command.desc))); 
  }
}
