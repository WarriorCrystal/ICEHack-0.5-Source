package me.fluffycq.icehack;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.fluffycq.icehack.module.modules.misc.Discord;

public class DiscordPresence {
  private static void setRpcFromSettings() {
    discordSettings = (Discord)ICEHack.fevents.moduleManager.getModule("DiscordRPC");
    details = discordSettings.getLine(discordSettings.getSetting("Line1").getValString());
    state = discordSettings.getLine(discordSettings.getSetting("Line2").getValString());
    presence.details = details;
    presence.state = state;
    presence.largeImageKey = "icehacklogo";
    presence.largeImageText = "ICEHack";
    rpc.Discord_UpdatePresence(presence);
  }
  
  private static void setRpcFromSettingsNonInt() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        rpc.Discord_RunCallbacks();
        discordSettings = (Discord)ICEHack.fevents.moduleManager.getModule("DiscordRPC");
        details = discordSettings.getLine(discordSettings.getSetting("Line1").getValString());
        state = discordSettings.getLine(discordSettings.getSetting("Line2").getValString());
        presence.details = details;
        presence.state = state;
        rpc.Discord_UpdatePresence(presence);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      try {
        Thread.sleep(4000L);
      } catch (InterruptedException interruptedException) {
        interruptedException.printStackTrace();
      } 
    } 
  }
  
  static {
    rpc = DiscordRPC.INSTANCE;
    presence = new DiscordRichPresence();
    hasStarted = false;
  }
  
  public static void start() {
    if (hasStarted)
      return; 
    hasStarted = true;
    DiscordEventHandlers discordEventHandlers = new DiscordEventHandlers();
    rpc.Discord_Initialize("715377199724757085", discordEventHandlers, true, "");
    presence.startTimestamp = System.currentTimeMillis() / 1000L;
    setRpcFromSettings();
    (new Thread(DiscordPresence::setRpcFromSettingsNonInt, "Discord-RPC-Callback-Handler")).start();
  }
}
