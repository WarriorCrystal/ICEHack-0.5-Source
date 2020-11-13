//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventPlayerIsPotionActive;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityLivingBase.class}, priority = 2147483647)
public abstract class MixinEntityLivingBase extends MixinEntity {
  @Shadow
  public void jump() {}
  
  @Inject(method = {"isPotionActive"}, at = {@At("HEAD")}, cancellable = true)
  public void isPotionActive(Potion paramPotion, CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    EventPlayerIsPotionActive eventPlayerIsPotionActive = new EventPlayerIsPotionActive(paramPotion);
    ICEHack.EVENT_BUS.post(eventPlayerIsPotionActive);
    if (eventPlayerIsPotionActive.isCancelled())
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(false)); 
  }
}
