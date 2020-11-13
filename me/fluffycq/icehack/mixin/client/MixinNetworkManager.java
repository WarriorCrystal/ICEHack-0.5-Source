package me.fluffycq.icehack.mixin.client;

import io.netty.channel.ChannelHandlerContext;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {NetworkManager.class}, priority = 2147483647)
public class MixinNetworkManager {
  @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
  private void onSendPacket(Packet<?> paramPacket, CallbackInfo paramCallbackInfo) {
    PacketEvent.Send send = new PacketEvent.Send(paramPacket);
    ICEHack.EVENT_BUS.post(send);
    if (send.isCancelled())
      paramCallbackInfo.cancel(); 
  }
  
  @Inject(method = {"channelRead0"}, at = {@At("HEAD")}, cancellable = true)
  private void onChannelRead(ChannelHandlerContext paramChannelHandlerContext, Packet<?> paramPacket, CallbackInfo paramCallbackInfo) {
    PacketEvent.Receive receive = new PacketEvent.Receive(paramPacket);
    ICEHack.EVENT_BUS.post(receive);
    if (receive.isCancelled())
      paramCallbackInfo.cancel(); 
  }
}
