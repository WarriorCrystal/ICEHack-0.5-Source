package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventSteerEntity;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityLlama.class}, priority = 2147483647)
public class MixinEntityLlama {
  @Inject(method = {"canBeSteered"}, at = {@At("HEAD")}, cancellable = true)
  public void canBeSteered(CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    EventSteerEntity eventSteerEntity = new EventSteerEntity();
    ICEHack.EVENT_BUS.post(eventSteerEntity);
    if (eventSteerEntity.isCancelled()) {
      paramCallbackInfoReturnable.cancel();
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(true));
    } 
  }
}
