//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.events.EventRenderUpdateEquippedItem;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.EnumHand;

public class SmallShield extends Module {
  public SmallShield() {
    super("SmallShield", 0, Category.RENDER);
  }
  
  public void onUpdate() {
    mc.entityRenderer.itemRenderer.equippedProgressOffHand = (float)this.height.getValDouble();
  }
}
