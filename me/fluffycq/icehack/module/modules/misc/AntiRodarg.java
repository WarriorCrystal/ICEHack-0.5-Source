//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;

public class AntiRodarg extends Module {
  public AntiRodarg() {
    super("AntiRodarg", 0, Category.MISC);
    this.recListener = new Listener(paramReceive -> {
          if (paramReceive.getPacket() instanceof SPacketPlayerListItem && ((SPacketPlayerListItem)paramReceive.getPacket()).getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
            SPacketPlayerListItem sPacketPlayerListItem = (SPacketPlayerListItem)paramReceive.getPacket();
            for (SPacketPlayerListItem.AddPlayerData addPlayerData : sPacketPlayerListItem.getEntries()) {
              String str = (addPlayerData.getProfile() != null) ? addPlayerData.getProfile().getName() : "null";
              if (str.equalsIgnoreCase("Rodarg"))
                FMLClientHandler.instance().getClientToServerNetworkManager().closeChannel((ITextComponent)new TextComponentString("REMO IS COMING!!! HIDE YOUR BASES!")); 
            } 
          } 
        }new java.util.function.Predicate[0]);
  }
}
