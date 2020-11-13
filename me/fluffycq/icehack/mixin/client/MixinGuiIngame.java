package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventRenderGameOverlay;
import me.fluffycq.icehack.events.PotionHUDEvent;
import me.fluffycq.icehack.util.ICEWrapper;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiIngame.class}, priority = 2147483647)
public class MixinGuiIngame {
  @Inject(method = {"renderPotionEffects"}, at = {@At("HEAD")}, cancellable = true)
  public void renderPotions(ScaledResolution paramScaledResolution, CallbackInfo paramCallbackInfo) {
    PotionHUDEvent potionHUDEvent = new PotionHUDEvent();
    ICEHack.EVENT_BUS.post(potionHUDEvent);
    if (potionHUDEvent.isCancelled())
      paramCallbackInfo.cancel(); 
  }
  
  @Inject(method = {"renderGameOverlay"}, at = {@At("HEAD")}, cancellable = true)
  public void renderGameOverlay(float paramFloat, CallbackInfo paramCallbackInfo) {
    EventRenderGameOverlay eventRenderGameOverlay = new EventRenderGameOverlay(paramFloat, new ScaledResolution(ICEWrapper.getMinecraft()));
    ICEHack.EVENT_BUS.post(eventRenderGameOverlay);
    if (eventRenderGameOverlay.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
