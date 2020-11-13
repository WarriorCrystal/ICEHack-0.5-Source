package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LayerArmorBase.class}, priority = 2147483647)
public class MixinLayerArmorBase {
  @Inject(method = {"renderArmorLayer"}, at = {@At("HEAD")}, cancellable = true)
  public void renderArmorLayer(EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, EntityEquipmentSlot paramEntityEquipmentSlot, CallbackInfo paramCallbackInfo) {
    if (ICEHack.fevents.moduleManager != null && 
      ICEHack.fevents.moduleManager.getModule("NoArmor") != null && ICEHack.fevents.moduleManager.getModule("NoArmor").isEnabled())
      paramCallbackInfo.cancel(); 
  }
}
