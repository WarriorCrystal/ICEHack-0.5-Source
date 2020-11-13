package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventCanCollideCheck;
import me.fluffycq.icehack.events.EventLiquidCollisionBB;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {BlockLiquid.class}, priority = 2147483647)
public class MixinBlockLiquid {
  @Inject(method = {"canCollideCheck"}, at = {@At("HEAD")}, cancellable = true)
  public void canCollideCheck(IBlockState paramIBlockState, boolean paramBoolean, CallbackInfoReturnable<Boolean> paramCallbackInfoReturnable) {
    EventCanCollideCheck eventCanCollideCheck = new EventCanCollideCheck();
    ICEHack.EVENT_BUS.post(eventCanCollideCheck);
    paramCallbackInfoReturnable.setReturnValue(Boolean.valueOf(eventCanCollideCheck.isCancelled()));
  }
  
  @Inject(method = {"getCollisionBoundingBox"}, at = {@At("HEAD")}, cancellable = true)
  public void getCollisionBoundingBox(IBlockState paramIBlockState, IBlockAccess paramIBlockAccess, BlockPos paramBlockPos, CallbackInfoReturnable<AxisAlignedBB> paramCallbackInfoReturnable) {
    EventLiquidCollisionBB eventLiquidCollisionBB = new EventLiquidCollisionBB(paramBlockPos);
    ICEHack.EVENT_BUS.post(eventLiquidCollisionBB);
    if (eventLiquidCollisionBB.isCancelled()) {
      paramCallbackInfoReturnable.setReturnValue(eventLiquidCollisionBB.getBoundingBox());
      paramCallbackInfoReturnable.cancel();
    } 
  }
}
