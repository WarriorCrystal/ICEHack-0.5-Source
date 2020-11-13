//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public class StrDetect extends Module {
  public StrDetect() {
    super("StrDetect", 0, Category.COMBAT);
  }
  
  public void onEnable() {
    this.users.clear();
  }
  
  public void onDisable() {
    this.users.clear();
  }
  
  public void onUpdate() {
    for (Entity entity : mc.world.loadedEntityList) {
      if (entity instanceof EntityLivingBase && entity != mc.player && ((EntityLivingBase)entity).getHealth() > 0.0F && entity instanceof EntityPlayer) {
        EntityPlayer entityPlayer = (EntityPlayer)entity;
        if (entityPlayer.getActivePotionMap() != null) {
          if (entityPlayer.isPotionActive(MobEffects.STRENGTH) && !this.users.contains(entityPlayer.getName())) {
            this.users.add(entityPlayer.getName());
            Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("&3").append(entityPlayer.getName()).append(" &anow has Strength.")));
          } 
          if (!entityPlayer.isPotionActive(MobEffects.STRENGTH) && this.users.contains(entityPlayer.getName())) {
            this.users.remove(entityPlayer.getName());
            Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("&3").append(entityPlayer.getName()).append(" &4no longer has Strength.")));
          } 
        } 
      } 
    } 
  }
}
