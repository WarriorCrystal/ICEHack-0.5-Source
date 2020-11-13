//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.Era;
import me.fluffycq.icehack.events.EventPlayerMotionUpdate;
import me.fluffycq.icehack.events.PlayerMoveEvent;
import me.fluffycq.icehack.events.PlayerUpdateEvent;
import me.fluffycq.icehack.events.PushOutBlockEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityPlayerSP.class}, priority = 2147483647)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer {
  @Inject(method = {"move"}, at = {@At("HEAD")}, cancellable = true)
  public void move(MoverType paramMoverType, double paramDouble1, double paramDouble2, double paramDouble3, CallbackInfo paramCallbackInfo) {
    PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(paramDouble1, paramDouble2, paramDouble3, (Minecraft.getMinecraft()).player.onGround);
    ICEHack.EVENT_BUS.post(playerMoveEvent);
    if (playerMoveEvent.isCancelled()) {
      move(paramMoverType, playerMoveEvent.x, playerMoveEvent.y, playerMoveEvent.z);
      paramCallbackInfo.cancel();
    } 
  }
  
  @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("HEAD")}, cancellable = true)
  public void OnPreUpdateWalkingPlayer(CallbackInfo paramCallbackInfo) {
    EventPlayerMotionUpdate eventPlayerMotionUpdate = new EventPlayerMotionUpdate(Era.PRE);
    ICEHack.EVENT_BUS.post(eventPlayerMotionUpdate);
    if (eventPlayerMotionUpdate.isCancelled())
      paramCallbackInfo.cancel(); 
  }
  
  @Inject(method = {"onUpdate"}, at = {@At("HEAD")}, cancellable = true)
  public void onUpdate(CallbackInfo paramCallbackInfo) {
    PlayerUpdateEvent playerUpdateEvent = new PlayerUpdateEvent();
    ICEHack.EVENT_BUS.post(playerUpdateEvent);
    if (playerUpdateEvent.isCancelled())
      paramCallbackInfo.cancel(); 
  }
  
  @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("RETURN")}, cancellable = true)
  public void OnPostUpdateWalkingPlayer(CallbackInfo paramCallbackInfo) {
    EventPlayerMotionUpdate eventPlayerMotionUpdate = new EventPlayerMotionUpdate(Era.POST);
    ICEHack.EVENT_BUS.post(eventPlayerMotionUpdate);
    if (eventPlayerMotionUpdate.isCancelled())
      paramCallbackInfo.cancel(); 
  }
  
  @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
  private void onPushOutOfBlocks(double paramDouble1, double paramDouble2, double paramDouble3, CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    PushOutBlockEvent pushOutBlockEvent = new PushOutBlockEvent();
    ICEHack.EVENT_BUS.post(pushOutBlockEvent);
    if (pushOutBlockEvent.isCancelled())
      paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(false)); 
  }
}
