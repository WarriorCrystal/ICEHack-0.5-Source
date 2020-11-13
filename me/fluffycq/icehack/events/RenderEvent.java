//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.events;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

public class RenderEvent extends ICEEvent {
  public void resetTranslation() {
    setTranslation(this.renderPos);
  }
  
  public Vec3d getRenderPos() {
    return this.renderPos;
  }
  
  public BufferBuilder getBuffer() {
    return this.tessellator.getBuffer();
  }
  
  public Tessellator getTessellator() {
    return this.tessellator;
  }
  
  public RenderEvent(Tessellator paramTessellator, Vec3d paramVec3d) {
    this.tessellator = paramTessellator;
    this.renderPos = paramVec3d;
  }
  
  public void setTranslation(Vec3d paramVec3d) {
    getBuffer().setTranslation(-paramVec3d.x, -paramVec3d.y, -paramVec3d.z);
  }
}
