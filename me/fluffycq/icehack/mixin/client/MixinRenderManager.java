//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.Era;
import me.fluffycq.icehack.events.RenderEntityEvent;
import me.fluffycq.icehack.util.IRenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {RenderManager.class}, priority = 2147483647)
public abstract class MixinRenderManager implements IRenderManager {
  @Accessor
  public abstract double getRenderPosX();
  
  @Accessor
  public abstract double getRenderPosY();
  
  @Accessor
  public abstract double getRenderPosZ();
  
  @Redirect(method = {"renderEntity"}, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/Render.doRender(Lnet/minecraft/entity/Entity;DDDFF)V"))
  private void doRenderEntity$doRender(Render paramRender, Entity paramEntity, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2) {
    RenderEntityEvent renderEntityEvent1 = new RenderEntityEvent(paramRender, paramEntity, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2, Era.PRE);
    ICEHack.EVENT_BUS.post(renderEntityEvent1);
    if (!renderEntityEvent1.isCancelled())
      paramRender.doRender(paramEntity, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2); 
    RenderEntityEvent renderEntityEvent2 = new RenderEntityEvent(paramRender, paramEntity, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2, Era.POST);
    ICEHack.EVENT_BUS.post(renderEntityEvent2);
  }
}
