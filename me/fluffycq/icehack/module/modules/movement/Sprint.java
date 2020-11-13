//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;

public class Sprint extends Module {
  public Sprint() {
    super("Sprint", 0, Category.MOVEMENT);
  }
  
  public void onUpdate() {
    super.onUpdate();
    if (mc.player == null || mc.world == null)
      return; 
    this.tick++;
    if (this.tick >= this.delay.getValDouble() * 20.0D && this.popbob.getValBoolean()) {
      mc.player.sendChatMessage("popbob is sprinting");
      this.tick = 0;
    } 
    if (canSprint() && !mc.player.isSprinting())
      mc.player.setSprinting(true); 
  }
  
  private boolean canSprint() {
    return (mc.player.moveForward > 0.0F && !mc.player.isActiveItemStackBlocking() && !mc.player.isOnLadder() && !mc.player.collidedHorizontally && mc.player.getFoodStats().getFoodLevel() > 6);
  }
  
  public void onToggle(boolean paramBoolean) {
    this.tick = 0;
  }
}
