package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventHorseSaddled;
import me.fluffycq.icehack.events.EventSteerEntity;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {AbstractHorse.class}, priority = 2147483647)
public class MixinAbstractHorse {
  @Inject(method = {"canBeSteered"}, at = {@At("HEAD")}, cancellable = true)
  public void canBeSteered(CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    EventSteerEntity eventSteerEntity = new EventSteerEntity();
    ICEHack.EVENT_BUS.post(eventSteerEntity);
    if (eventSteerEntity.isCancelled()) {
      paramCallbackInfoReturnable.cancel();
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(true));
    } 
  }
  
  @Inject(method = {"isHorseSaddled"}, at = {@At("HEAD")}, cancellable = true)
  public void isHorseSaddled(CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    EventHorseSaddled eventHorseSaddled = new EventHorseSaddled();
    ICEHack.EVENT_BUS.post(eventHorseSaddled);
    if (eventHorseSaddled.isCancelled()) {
      paramCallbackInfoReturnable.cancel();
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(true));
    } 
  }
}
