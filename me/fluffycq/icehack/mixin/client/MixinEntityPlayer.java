//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventPlayerApplyCollision;
import me.fluffycq.icehack.events.EventPlayerJump;
import me.fluffycq.icehack.events.EventPlayerPushedByWater;
import me.fluffycq.icehack.events.EventPlayerTravel;
import me.fluffycq.icehack.events.OpaqueEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityPlayer.class}, priority = 2147483647)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {
  @Inject(method = {"travel"}, at = {@At("HEAD")}, cancellable = true)
  public void travel(float paramFloat1, float paramFloat2, float paramFloat3, CallbackInfo paramCallbackInfo) {
    EventPlayerTravel eventPlayerTravel = new EventPlayerTravel();
    ICEHack.EVENT_BUS.post(eventPlayerTravel);
    if (eventPlayerTravel.isCancelled()) {
      move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      paramCallbackInfo.cancel();
    } 
  }
  
  @Inject(method = {"isEntityInsideOpaqueBlock"}, at = {@At("HEAD")}, cancellable = true)
  private void onIsEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    OpaqueEvent opaqueEvent = new OpaqueEvent();
    ICEHack.EVENT_BUS.post(opaqueEvent);
    if (opaqueEvent.isCancelled())
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(false)); 
  }
  
  @Inject(method = {"applyEntityCollision"}, at = {@At("HEAD")}, cancellable = true)
  public void applyEntityCollision(Entity paramEntity, CallbackInfo paramCallbackInfo) {
    EventPlayerApplyCollision eventPlayerApplyCollision = new EventPlayerApplyCollision(paramEntity);
    ICEHack.EVENT_BUS.post(eventPlayerApplyCollision);
    if (eventPlayerApplyCollision.isCancelled())
      paramCallbackInfo.cancel(); 
  }
  
  @Inject(method = {"isPushedByWater()Z"}, at = {@At("HEAD")}, cancellable = true)
  public void isPushedByWater(CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    EventPlayerPushedByWater eventPlayerPushedByWater = new EventPlayerPushedByWater();
    ICEHack.EVENT_BUS.post(eventPlayerPushedByWater);
    if (eventPlayerPushedByWater.isCancelled())
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(false)); 
  }
  
  @Inject(method = {"jump"}, at = {@At("HEAD")}, cancellable = true)
  public void jump(CallbackInfo paramCallbackInfo) {
    try {
      EventPlayerJump eventPlayerJump = new EventPlayerJump(this.motionZ, this.motionZ);
      ICEHack.EVENT_BUS.post(eventPlayerJump);
      if (!eventPlayerJump.isCancelled())
        jump(); 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}
