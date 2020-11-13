package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.BlockWebSlowdownEvent;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BlockWeb.class}, priority = 2147483647)
public class MixinBlockWeb {
  @Inject(method = {"onEntityCollision"}, at = {@At("HEAD")}, cancellable = true)
  public void onEntityCollision(World paramWorld, BlockPos paramBlockPos, IBlockState paramIBlockState, Entity paramEntity, CallbackInfo paramCallbackInfo) {
    BlockWebSlowdownEvent blockWebSlowdownEvent = new BlockWebSlowdownEvent();
    ICEHack.EVENT_BUS.post(blockWebSlowdownEvent);
    if (blockWebSlowdownEvent.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
