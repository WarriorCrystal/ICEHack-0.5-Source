package me.fluffycq.icehack.events;

public class EventPlayerJump extends ICEEvent {
  public EventPlayerJump(double paramDouble1, double paramDouble2) {
    this.MotionX = paramDouble1;
    this.MotionY = paramDouble2;
  }
}
