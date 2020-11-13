package me.fluffycq.icehack.events;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends ICEEvent {
  public float getEntityYaw() {
    return this.entityYaw;
  }
  
  public Entity getEntity() {
    return this.entity;
  }
  
  public float getPartialTicks() {
    return this.partialTicks;
  }
  
  public RenderEntityEvent(Render paramRender, Entity paramEntity, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2, Era paramEra) {
    this.renderer = paramRender;
    this.entity = paramEntity;
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
    this.entityYaw = paramFloat1;
    this.partialTicks = paramFloat2;
    this.eventType = paramEra;
  }
  
  public Era getEventType() {
    return this.eventType;
  }
  
  public Render getRenderer() {
    return this.renderer;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getZ() {
    return this.z;
  }
  
  public double getX() {
    return this.x;
  }
}
