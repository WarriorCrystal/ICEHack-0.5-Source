//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import java.util.ArrayList;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.util.math.RayTraceResult;

public class NoEntityBlock extends Module {
  public NoEntityBlock() {
    super("NoEntityBlock", 0, Category.RENDER);
    INST = this;
    this.modes.add("Normal");
    this.modes.add("Dev");
    this.mode = new Setting("Mode", this, "Normal", this.modes);
  }
  
  public static boolean doBlock() {
    return INST.mode.getValString().equalsIgnoreCase("Normal") ? ((mc.objectMouseOver != null) ? ((INST.isEnabled() && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe && mc.objectMouseOver.getBlockPos() != null)) : false) : (INST.mode.getValString().equalsIgnoreCase("Dev") ? ((mc.objectMouseOver != null) ? ((mc.objectMouseOver.typeOfHit != null) ? ((INST.isEnabled() && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)) : false) : false) : false);
  }
}
