//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;

public class NoSoundLag extends Module {
  public NoSoundLag() {
    super("NoSoundLag", 0, Category.MISC);
    this.receiveListener = new Listener(paramReceive -> {
          if (mc.player == null)
            return; 
          if (paramReceive.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect)paramReceive.getPacket();
            if (sPacketSoundEffect.getCategory() == SoundCategory.PLAYERS && sPacketSoundEffect.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC)
              paramReceive.cancel(); 
          } 
        }new java.util.function.Predicate[0]);
  }
}
