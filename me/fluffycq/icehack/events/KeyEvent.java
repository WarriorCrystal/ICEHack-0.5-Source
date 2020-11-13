package me.fluffycq.icehack.events;

public class KeyEvent extends ICEEvent {
  public int getPressed() {
    return this.key;
  }
  
  public KeyEvent(int paramInt) {
    this.key = paramInt;
  }
}
