//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FaceUtil {
  public static double[] calculateLookAt(double paramDouble1, double paramDouble2, double paramDouble3, EntityPlayer paramEntityPlayer) {
    double d1 = paramEntityPlayer.posX - paramDouble1;
    double d2 = paramEntityPlayer.posY - paramDouble2;
    double d3 = paramEntityPlayer.posZ - paramDouble3;
    double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
    d1 /= d4;
    d2 /= d4;
    d3 /= d4;
    double d5 = Math.asin(d2);
    double d6 = Math.atan2(d3, d1);
    d5 = d5 * 180.0D / Math.PI;
    d6 = d6 * 180.0D / Math.PI;
    d6 += 90.0D;
    return new double[] { d6, d5 };
  }
  
  public static float getFacePitch(Entity paramEntity) {
    double d3;
    double d1 = paramEntity.posX - mc.player.posX;
    double d2 = paramEntity.posZ - mc.player.posZ;
    if (paramEntity instanceof EntityLivingBase) {
      EntityLivingBase entityLivingBase = (EntityLivingBase)paramEntity;
      d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - mc.player.posY + mc.player.getEyeHeight();
    } else {
      d3 = ((paramEntity.getEntityBoundingBox()).minY + (paramEntity.getEntityBoundingBox()).maxY) / 2.0D - mc.player.posY + mc.player.getEyeHeight();
    } 
    double d4 = MathHelper.sqrt(d1 * d1 + d2 * d2);
    float f = (float)-(MathHelper.atan2(d3, d4) * 57.29577951308232D);
    return updateRotation(mc.player.rotationPitch, f, Float.MAX_VALUE);
  }
  
  public static float getFaceYaw(Entity paramEntity) {
    double d1 = paramEntity.posX - mc.player.posX;
    double d2 = paramEntity.posZ - mc.player.posZ;
    if (paramEntity instanceof EntityLivingBase) {
      EntityLivingBase entityLivingBase = (EntityLivingBase)paramEntity;
      double d = entityLivingBase.posY + entityLivingBase.getEyeHeight() - mc.player.posY + mc.player.getEyeHeight();
    } else {
      double d = ((paramEntity.getEntityBoundingBox()).minY + (paramEntity.getEntityBoundingBox()).maxY) / 2.0D - mc.player.posY + mc.player.getEyeHeight();
    } 
    float f = (float)(MathHelper.atan2(d2, d1) * 57.29577951308232D) - 90.0F;
    return updateRotation(mc.player.rotationYaw, f, Float.MAX_VALUE);
  }
  
  private static float updateRotation(float paramFloat1, float paramFloat2, float paramFloat3) {
    float f = MathHelper.wrapDegrees(paramFloat2 - paramFloat1);
    if (f > paramFloat3)
      f = paramFloat3; 
    if (f < -paramFloat3)
      f = -paramFloat3; 
    return paramFloat1 + f;
  }
  
  public static void faceBlock(BlockPos paramBlockPos) {
    double d1 = paramBlockPos.getX() + 0.5D - (Minecraft.getMinecraft()).player.posX;
    double d2 = paramBlockPos.getZ() + 0.5D - (Minecraft.getMinecraft()).player.posZ;
    double d3 = ((paramBlockPos.getY() + paramBlockPos.up().getY()) + 0.75D) / 2.0D - (Minecraft.getMinecraft()).player.posY + (Minecraft.getMinecraft()).player.getEyeHeight();
    double d4 = MathHelper.sqrt(d1 * d1 + d2 * d2);
    float f1 = (float)(MathHelper.atan2(d2, d1) * 57.29577951308232D) - 90.0F;
    float f2 = (float)-(MathHelper.atan2(d3, d4) * 57.29577951308232D);
    float f3 = updateRotation((Minecraft.getMinecraft()).player.rotationPitch, f2, Float.MAX_VALUE);
    float f4 = updateRotation((Minecraft.getMinecraft()).player.rotationYaw, f1, Float.MAX_VALUE);
    Minecraft.getMinecraft().getConnection().sendPacket((Packet)new CPacketPlayer.Rotation(f4, f3, (Minecraft.getMinecraft()).player.onGround));
  }
}
