package me.fluffycq.icehack.events;

public class Render3DEvent extends ICEEvent {
  public Render3DEvent(float paramFloat) {
    partialTicks = paramFloat;
  }
  
  public float getPartialTicks() {
    return partialTicks;
  }
}
