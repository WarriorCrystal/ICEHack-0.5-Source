//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;

public class NoBob extends Module {
  public NoBob() {
    super("NoBob", 0, Category.RENDER);
  }
  
  static {
  
  }
  
  public void onUpdate() {
    mc.player.distanceWalkedModified = 4.0F;
  }
}
