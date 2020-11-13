package me.fluffycq.icehack.command.commands;

import java.util.List;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.command.Command;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.message.Messages;

public class Friend extends Command {
  public Friend() {
    this.aliases.add("f");
    this.aliases.add("fr");
    this.aliases.add("friends");
    this.desc = "Change the friends list";
  }
  
  static {
  
  }
  
  public void handleCommand(String paramString, List<String> paramList) {
    if (!argExists(paramList, 1)) {
      Messages.sendChatMessage("Friends command: ");
      Messages.sendMessage("friend <username/add/del> <player>");
    } else if (!argExists(paramList, 2) && !((String)paramList.get(1)).equalsIgnoreCase("add") && !((String)paramList.get(1)).equalsIgnoreCase("del")) {
      Messages.sendChatMessage(Friends.isFriend(paramList.get(1)) ? String.valueOf((new StringBuilder()).append("&a").append(paramList.get(1)).append("&f is friended.")) : String.valueOf((new StringBuilder()).append("&c").append(paramList.get(1)).append("&f is not friended.")));
    } else if (argExists(paramList, 2)) {
      if (((String)paramList.get(1)).equalsIgnoreCase("add"))
        if (!Friends.isFriend(paramList.get(2))) {
          ICEHack.friends.addFriend(paramList.get(2));
          Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("&aAdded ").append(paramList.get(2)).append(" to the friendslist.")));
        } else {
          Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("&a").append(paramList.get(2)).append(" is already on the friendslist!")));
        }  
      if (((String)paramList.get(1)).equalsIgnoreCase("del"))
        if (Friends.isFriend(paramList.get(2))) {
          ICEHack.friends.removeFriend(paramList.get(2));
          Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("&cRemoved ").append(paramList.get(2)).append(" from the friendslist.")));
        } else {
          Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("&c").append(paramList.get(2)).append(" isn't on the friendslist!")));
        }  
    } else {
      Messages.sendChatMessage("&cYou must specify a user to remove from the friendslist!");
    } 
  }
}
