package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.BlockSoulSandSlowdownEvent;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BlockSoulSand.class}, priority = 2147483647)
public class MixinBlockSoulSand {
  @Inject(method = {"onEntityCollision"}, at = {@At("HEAD")}, cancellable = true)
  public void onEntityCollision(World paramWorld, BlockPos paramBlockPos, IBlockState paramIBlockState, Entity paramEntity, CallbackInfo paramCallbackInfo) {
    BlockSoulSandSlowdownEvent blockSoulSandSlowdownEvent = new BlockSoulSandSlowdownEvent();
    ICEHack.EVENT_BUS.post(blockSoulSandSlowdownEvent);
    if (blockSoulSandSlowdownEvent.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
