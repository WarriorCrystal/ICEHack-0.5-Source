package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventSetOpaqueCube;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {VisGraph.class}, priority = 2147483647)
public class MixinVisGraph {
  @Inject(method = {"setOpaqueCube"}, at = {@At("HEAD")}, cancellable = true)
  public void setOpaqueCube(BlockPos paramBlockPos, CallbackInfo paramCallbackInfo) {
    EventSetOpaqueCube eventSetOpaqueCube = new EventSetOpaqueCube();
    ICEHack.EVENT_BUS.post(eventSetOpaqueCube);
    if (eventSetOpaqueCube.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
