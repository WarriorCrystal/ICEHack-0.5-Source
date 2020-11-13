package me.fluffycq.icehack.events;

import net.minecraft.entity.Entity;

public class PopTotemEvent extends ICEEvent {
  public Entity getEntity() {
    return this.entity;
  }
  
  public PopTotemEvent(Entity paramEntity) {
    this.entity = paramEntity;
  }
}
