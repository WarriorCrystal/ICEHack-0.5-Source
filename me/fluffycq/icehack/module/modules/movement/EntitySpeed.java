//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MovementInput;

public class EntitySpeed extends Module {
  public void onUpdate() {
    if (mc.player.getRidingEntity() != null) {
      MovementInput movementInput = mc.player.movementInput;
      double d1 = movementInput.moveForward;
      double d2 = movementInput.moveStrafe;
      float f = mc.player.rotationYaw;
      if (d1 == 0.0D && d2 == 0.0D) {
        (mc.player.getRidingEntity()).motionX = 0.0D;
        (mc.player.getRidingEntity()).motionZ = 0.0D;
      } else {
        if (d1 != 0.0D) {
          if (d2 > 0.0D) {
            f += ((d1 > 0.0D) ? -45 : 45);
          } else if (d2 < 0.0D) {
            f += ((d1 > 0.0D) ? 45 : -45);
          } 
          d2 = 0.0D;
          if (d1 > 0.0D) {
            d1 = 1.0D;
          } else if (d1 < 0.0D) {
            d1 = -1.0D;
          } 
        } 
        (mc.player.getRidingEntity()).motionX = d1 * this.speed.getValDouble() * Math.cos(Math.toRadians((f + 90.0F))) + d2 * this.speed.getValDouble() * Math.sin(Math.toRadians((f + 90.0F)));
        (mc.player.getRidingEntity()).motionZ = d1 * this.speed.getValDouble() * Math.sin(Math.toRadians((f + 90.0F))) - d2 * this.speed.getValDouble() * Math.cos(Math.toRadians((f + 90.0F)));
        if (mc.player.getRidingEntity() instanceof EntityMinecart)
          ((EntityMinecart)mc.player.getRidingEntity()).setVelocity(d1 * this.speed.getValDouble() * Math.cos(Math.toRadians((f + 90.0F))) + d2 * this.speed.getValDouble() * Math.sin(Math.toRadians((f + 90.0F))), ((EntityMinecart)mc.player.getRidingEntity()).motionY, d1 * this.speed.getValDouble() * Math.sin(Math.toRadians((f + 90.0F))) - d2 * this.speed.getValDouble() * Math.cos(Math.toRadians((f + 90.0F)))); 
      } 
    } 
  }
  
  public EntitySpeed() {
    super("EntitySpeed", 0, Category.MOVEMENT);
  }
  
  public String getModInfo() {
    return String.valueOf(this.speed.getValDouble());
  }
}
