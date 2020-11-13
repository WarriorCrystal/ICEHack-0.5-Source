package me.fluffycq.icehack.events;

public class PlayerLeaveEvent extends ICEEvent {
  public String getName() {
    return this.name;
  }
  
  public PlayerLeaveEvent(String paramString) {
    this.name = paramString;
  }
}
