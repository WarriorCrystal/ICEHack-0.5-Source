package me.fluffycq.icehack.command;

import java.util.ArrayList;
import me.fluffycq.icehack.command.commands.ClearFinder;
import me.fluffycq.icehack.command.commands.Friend;
import me.fluffycq.icehack.command.commands.Help;
import me.fluffycq.icehack.command.commands.HudEditor;
import me.fluffycq.icehack.message.Messages;

public class CommandManager {
  public void handleCMD(String paramString) {
    ArrayList<String> arrayList = new ArrayList();
    for (String str : paramString.split(" "))
      arrayList.add(str); 
    if (arrayList.get(0) == null)
      arrayList.add(paramString); 
    boolean bool = false;
    for (Command command : this.cmds) {
      if (((String)arrayList.get(0)).equalsIgnoreCase(command.getPre()) || command.aliases.contains(((String)arrayList.get(0)).toLowerCase())) {
        command.handleCommand(arrayList.get(0), arrayList);
        bool = true;
      } 
    } 
    if (!bool)
      Messages.sendChatMessage("&cCommand not found. try 'help' for help."); 
  }
  
  public CommandManager() {
    this.cmds.add(new Friend());
    this.cmds.add(new Help());
    this.cmds.add(new HudEditor());
    this.cmds.add(new ClearFinder());
  }
}
