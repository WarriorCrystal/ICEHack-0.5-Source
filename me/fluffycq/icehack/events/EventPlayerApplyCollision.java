package me.fluffycq.icehack.events;

import net.minecraft.entity.Entity;

public class EventPlayerApplyCollision extends ICEEvent {
  public EventPlayerApplyCollision(Entity paramEntity) {
    this.entity = paramEntity;
  }
}
