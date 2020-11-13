//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.LightmapEvent;
import me.fluffycq.icehack.events.Render3DEvent;
import me.fluffycq.icehack.module.modules.render.NoEntityBlock;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {EntityRenderer.class}, priority = 2147483647)
public class MixinEntityRenderer {
  @Redirect(method = {"getMouseOver"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
  public List<Entity> getEntitiesInAABBexcluding(WorldClient paramWorldClient, Entity paramEntity, AxisAlignedBB paramAxisAlignedBB, Predicate paramPredicate) {
    if (NoEntityBlock.doBlock())
      return new ArrayList<>(); 
    return paramWorldClient.getEntitiesInAABBexcluding(paramEntity, paramAxisAlignedBB, paramPredicate);
  }
  
  @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z")})
  private void renderWorldPassPre(int paramInt, float paramFloat, long paramLong, CallbackInfo paramCallbackInfo) {
    Render3DEvent render3DEvent = new Render3DEvent(paramFloat);
    ICEHack.EVENT_BUS.post(render3DEvent);
  }
  
  @Inject(method = {"updateLightmap"}, at = {@At("HEAD")}, cancellable = true)
  public void updateLightmap(float paramFloat, CallbackInfo paramCallbackInfo) {
    LightmapEvent lightmapEvent = new LightmapEvent();
    ICEHack.EVENT_BUS.post(lightmapEvent);
    if (lightmapEvent.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
