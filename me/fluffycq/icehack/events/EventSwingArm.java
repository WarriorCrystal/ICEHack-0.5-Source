package me.fluffycq.icehack.events;

import net.minecraft.util.EnumHand;

public class EventSwingArm extends ICEEvent {
  public EventSwingArm(EnumHand paramEnumHand) {
    this.Hand = paramEnumHand;
  }
}
