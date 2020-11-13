//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.init.Blocks;

public class IceSpeed extends Module {
  public void onUpdate() {
    Blocks.ICE.slipperiness = (float)this.speed.getValDouble();
    Blocks.PACKED_ICE.slipperiness = (float)this.speed.getValDouble();
    Blocks.FROSTED_ICE.slipperiness = (float)this.speed.getValDouble();
  }
  
  public void onDisable() {
    Blocks.ICE.slipperiness = 0.98F;
    Blocks.PACKED_ICE.slipperiness = 0.98F;
    Blocks.FROSTED_ICE.slipperiness = 0.98F;
  }
  
  public IceSpeed() {
    super("IceSpeed", 0, Category.MOVEMENT);
  }
}
