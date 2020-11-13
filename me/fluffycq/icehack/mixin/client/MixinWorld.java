package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventRenderRainStrength;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {World.class}, priority = 2147483647)
public class MixinWorld {
  @Inject(method = {"getRainStrength"}, at = {@At("HEAD")}, cancellable = true)
  public void getRainStrength(float paramFloat, CallbackInfoReturnable<Float> paramCallbackInfoReturnable) {
    EventRenderRainStrength eventRenderRainStrength = new EventRenderRainStrength();
    ICEHack.EVENT_BUS.post(eventRenderRainStrength);
    if (eventRenderRainStrength.isCancelled()) {
      paramCallbackInfoReturnable.cancel();
      paramCallbackInfoReturnable.setReturnValue(Float.valueOf(0.0F));
    } 
  }
}
