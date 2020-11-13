//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.PlayerJoinEvent;
import me.fluffycq.icehack.events.PlayerLeaveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {NetHandlerPlayClient.class}, priority = 2147483647)
public class MixinNetHandlerPlayClient {
  @Shadow
  private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
  
  @Inject(method = {"Lnet/minecraft/client/network/NetHandlerPlayClient;handlePlayerListItem(Lnet/minecraft/network/play/server/SPacketPlayerListItem;)V"}, at = {@At("HEAD")})
  public void preHandlePlayerListItem(SPacketPlayerListItem paramSPacketPlayerListItem, CallbackInfo paramCallbackInfo) {
    try {
      if (paramSPacketPlayerListItem.getEntries().size() <= 1)
        if (paramSPacketPlayerListItem.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
          paramSPacketPlayerListItem.getEntries().forEach(paramAddPlayerData -> {
                if (!paramAddPlayerData.getProfile().getId().equals((Minecraft.getMinecraft()).player.getGameProfile().getId()) && paramAddPlayerData.getProfile().getName() != null)
                  ICEHack.EVENT_BUS.post(new PlayerJoinEvent(paramAddPlayerData.getProfile().getName())); 
              });
        } else if (paramSPacketPlayerListItem.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
          paramSPacketPlayerListItem.getEntries().forEach(paramAddPlayerData -> {
                if (paramAddPlayerData.getProfile().getId() != null && !paramAddPlayerData.getProfile().getId().equals((Minecraft.getMinecraft()).player.getGameProfile().getId()))
                  ICEHack.EVENT_BUS.post(new PlayerLeaveEvent(((NetworkPlayerInfo)this.playerInfoMap.get(paramAddPlayerData.getProfile().getId())).getGameProfile().getName())); 
              });
        }  
    } catch (Exception exception) {}
  }
}
