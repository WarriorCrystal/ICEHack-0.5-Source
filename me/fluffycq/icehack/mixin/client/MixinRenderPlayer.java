package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {RenderPlayer.class}, priority = 2147483647)
public class MixinRenderPlayer {
  @Inject(method = {"renderEntityName"}, at = {@At("HEAD")}, cancellable = true)
  public void renderLivingLabel(AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, String paramString, double paramDouble4, CallbackInfo paramCallbackInfo) {
    if (ICEHack.fevents.moduleManager != null && 
      ICEHack.fevents.moduleManager.getModule("Nametags") != null && ICEHack.fevents.moduleManager.getModule("Nametags").isEnabled())
      paramCallbackInfo.cancel(); 
  }
}
