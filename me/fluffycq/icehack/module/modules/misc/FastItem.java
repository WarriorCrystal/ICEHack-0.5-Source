//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

public class FastItem extends Module {
  public FastItem() {
    super("FastItem", 0, Category.MISC);
  }
  
  public boolean isHolding(Item paramItem) {
    return mc.player.getHeldItemMainhand().getItem().equals(paramItem);
  }
  
  public void onUpdate() {
    if (this.bow.getValBoolean() && isHolding((Item)Items.BOW) && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
      mc.player.stopActiveHand();
    } 
    if (this.delay.getValDouble() > 0.0D)
      if (tick <= 0L) {
        tick = Math.round((2 * Math.round((float)this.delay.getValDouble() / 2.0F)));
      } else {
        tick--;
        mc.rightClickDelayTimer = 1;
        return;
      }  
    if (isHolding(Items.EXPERIENCE_BOTTLE) && this.xp.getValBoolean())
      mc.rightClickDelayTimer = 0; 
  }
}
