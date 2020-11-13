package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventRenderUpdateEquippedItem;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class}, priority = 2147483647)
public class MixinItemRenderer {
  @Inject(method = {"updateEquippedItem"}, at = {@At("HEAD")}, cancellable = true)
  public void updateEquippedItem(CallbackInfo paramCallbackInfo) {
    EventRenderUpdateEquippedItem eventRenderUpdateEquippedItem = new EventRenderUpdateEquippedItem();
    ICEHack.EVENT_BUS.post(eventRenderUpdateEquippedItem);
    if (eventRenderUpdateEquippedItem.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
