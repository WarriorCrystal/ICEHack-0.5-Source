package me.fluffycq.icehack.events;

import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends ICEEvent {
  public Render2DEvent(float paramFloat, ScaledResolution paramScaledResolution) {
    this.partialTicks = paramFloat;
    this.scaledResolution = paramScaledResolution;
  }
  
  public ScaledResolution getScaledResolution() {
    return this.scaledResolution;
  }
  
  public float getPartialTicks() {
    return this.partialTicks;
  }
}
