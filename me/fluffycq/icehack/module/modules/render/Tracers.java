//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.EntityUtil;
import me.fluffycq.icehack.util.MathUtil;
import me.fluffycq.icehack.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Tracers extends Module {
  private int getColor(Entity paramEntity) {
    return (paramEntity instanceof net.minecraft.entity.player.EntityPlayer) ? (Friends.isFriend(paramEntity.getName()) ? -16711698 : -16711936) : (paramEntity.isInvisible() ? -16777216 : ((EntityUtil.isHostileMob(paramEntity) || EntityUtil.isNeutralMob(paramEntity)) ? -65536 : (EntityUtil.isPassive(paramEntity) ? -29440 : ((paramEntity instanceof net.minecraft.entity.item.EntityBoat || paramEntity instanceof net.minecraft.entity.item.EntityMinecart) ? -256 : ((paramEntity instanceof net.minecraft.entity.item.EntityItem) ? -5635841 : -1)))));
  }
  
  public boolean shouldRenderTracer(Entity paramEntity) {
    return (paramEntity == (Minecraft.getMinecraft()).player) ? false : ((paramEntity instanceof net.minecraft.entity.player.EntityPlayer) ? this.Players.getValBoolean() : ((EntityUtil.isHostileMob(paramEntity) || EntityUtil.isNeutralMob(paramEntity)) ? this.Monsters.getValBoolean() : (EntityUtil.isPassive(paramEntity) ? this.Animals.getValBoolean() : ((paramEntity instanceof net.minecraft.entity.item.EntityBoat || paramEntity instanceof net.minecraft.entity.item.EntityMinecart) ? this.Vehicles.getValBoolean() : ((paramEntity instanceof net.minecraft.entity.item.EntityItem) ? this.Items.getValBoolean() : this.Others.getValBoolean())))));
  }
  
  public Tracers() {
    super("Tracers", 0, Category.RENDER);
  }
  
  public void onWorld(RenderEvent paramRenderEvent) {
    if (mc.getRenderManager() == null || (mc.getRenderManager()).options == null)
      return; 
    for (Entity entity : mc.world.loadedEntityList) {
      if (shouldRenderTracer(entity)) {
        Vec3d vec3d = MathUtil.interpolateEntity(entity, paramRenderEvent.getPartialTicks()).subtract((mc.getRenderManager()).renderPosX, (mc.getRenderManager()).renderPosY, (mc.getRenderManager()).renderPosZ);
        if (vec3d != null) {
          boolean bool = mc.gameSettings.viewBobbing;
          mc.gameSettings.viewBobbing = false;
          mc.entityRenderer.setupCameraTransform(paramRenderEvent.getPartialTicks(), 0);
          Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, 1.0D)).rotatePitch(-((float)Math.toRadians((Minecraft.getMinecraft()).player.rotationPitch))).rotateYaw(-((float)Math.toRadians((Minecraft.getMinecraft()).player.rotationYaw)));
          RenderUtil.drawLine3D((float)vec3d1.x, (float)vec3d1.y + mc.player.getEyeHeight(), (float)vec3d1.z, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z, 0.5F, getColor(entity));
          mc.gameSettings.viewBobbing = bool;
          mc.entityRenderer.setupCameraTransform(paramRenderEvent.getPartialTicks(), 0);
        } 
      } 
    } 
  }
}
