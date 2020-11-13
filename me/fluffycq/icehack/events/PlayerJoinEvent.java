package me.fluffycq.icehack.events;

public class PlayerJoinEvent extends ICEEvent {
  public PlayerJoinEvent(String paramString) {
    this.name = paramString;
  }
  
  public String getName() {
    return this.name;
  }
}
