package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventPlayerUpdateMoveState;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {MovementInputFromOptions.class}, priority = 2147483647)
public abstract class MixinMovementInputFromOptions extends MovementInput {
  @Inject(method = {"updatePlayerMoveState"}, at = {@At("RETURN")})
  public void updatePlayerMoveStateReturn(CallbackInfo paramCallbackInfo) {
    ICEHack.EVENT_BUS.post(new EventPlayerUpdateMoveState());
  }
}
