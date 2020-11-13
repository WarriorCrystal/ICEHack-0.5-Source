//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.screen;

import me.fluffycq.icehack.module.modules.exploit.MountBypass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DupeScreen {
  @SubscribeEvent
  public void onClick(GuiScreenEvent.ActionPerformedEvent.Pre paramPre) {
    if (paramPre.getGui() instanceof GuiScreenHorseInventory && (paramPre.getButton()).id == 133769420)
      for (Entity entity : (Minecraft.getMinecraft()).world.loadedEntityList) {
        if (entity instanceof EntityLivingBase && entity != (Minecraft.getMinecraft()).player && ((EntityLivingBase)entity).getHealth() > 0.0F && (Minecraft.getMinecraft()).player.getDistance(entity) <= 2.5D && entity instanceof net.minecraft.entity.passive.AbstractChestHorse) {
          MountBypass.ignoring = true;
          Vec3d vec3d = new Vec3d(entity.posX, entity.posY, entity.posZ);
          (Minecraft.getMinecraft()).player.connection.sendPacket((Packet)new CPacketUseEntity(entity, EnumHand.MAIN_HAND, vec3d));
          MountBypass.ignoring = false;
        } 
      }  
  }
  
  @SubscribeEvent
  public void onOpenGui(GuiScreenEvent.InitGuiEvent paramInitGuiEvent) {
    if (paramInitGuiEvent.getGui() instanceof GuiScreenHorseInventory) {
      GuiScreenHorseInventory guiScreenHorseInventory = (GuiScreenHorseInventory)paramInitGuiEvent.getGui();
      paramInitGuiEvent.getButtonList().add(new DupeBut(133769420, guiScreenHorseInventory.getGuiLeft(), guiScreenHorseInventory.getGuiTop() - 20, 50, 20, "Dupe"));
    } 
  }
}
