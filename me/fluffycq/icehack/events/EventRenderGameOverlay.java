package me.fluffycq.icehack.events;

import net.minecraft.client.gui.ScaledResolution;

public class EventRenderGameOverlay extends ICEEvent {
  public ScaledResolution getScaledResolution() {
    return this.scaledResolution;
  }
  
  public EventRenderGameOverlay(float paramFloat, ScaledResolution paramScaledResolution) {
    this.PartialTicks = paramFloat;
    this.scaledResolution = paramScaledResolution;
  }
}
