//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {
  public static boolean isFakeLocalPlayer(Entity paramEntity) {
    return (paramEntity != null && paramEntity.getEntityId() == -100 && ICEWrapper.getPlayer() != paramEntity);
  }
  
  public static Vec3d getInterpolatedAmount(Entity paramEntity, double paramDouble1, double paramDouble2, double paramDouble3) {
    return new Vec3d((paramEntity.posX - paramEntity.lastTickPosX) * paramDouble1, (paramEntity.posY - paramEntity.lastTickPosY) * paramDouble2, (paramEntity.posZ - paramEntity.lastTickPosZ) * paramDouble3);
  }
  
  public static boolean isNeutralMob(Entity paramEntity) {
    return (paramEntity instanceof EntityPigZombie || paramEntity instanceof EntityWolf || paramEntity instanceof EntityEnderman);
  }
  
  public static boolean isAboveWater(Entity paramEntity, boolean paramBoolean) {
    if (paramEntity == null)
      return false; 
    double d = paramEntity.posY - (paramBoolean ? 0.03D : (isPlayer(paramEntity) ? 0.2D : 0.5D));
    for (int i = MathHelper.floor(paramEntity.posX); i < MathHelper.ceil(paramEntity.posX); i++) {
      for (int j = MathHelper.floor(paramEntity.posZ); j < MathHelper.ceil(paramEntity.posZ); j++) {
        BlockPos blockPos = new BlockPos(i, MathHelper.floor(d), j);
        if (ICEWrapper.getWorld().getBlockState(blockPos).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
  
  public static double getRelativeX(float paramFloat) {
    return MathHelper.sin(-paramFloat * 0.017453292F);
  }
  
  public static boolean isPlayer(Entity paramEntity) {
    return paramEntity instanceof EntityPlayer;
  }
  
  public static boolean isFriendlyMob(Entity paramEntity) {
    return ((paramEntity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(paramEntity)) || paramEntity.isCreatureType(EnumCreatureType.AMBIENT, false) || paramEntity instanceof net.minecraft.entity.passive.EntityVillager || paramEntity instanceof EntityIronGolem || (isNeutralMob(paramEntity) && !isMobAggressive(paramEntity)));
  }
  
  static {
  
  }
  
  public static double getRelativeZ(float paramFloat) {
    return MathHelper.cos(paramFloat * 0.017453292F);
  }
  
  public static Vec3d getInterpolatedAmount(Entity paramEntity, Vec3d paramVec3d) {
    return getInterpolatedAmount(paramEntity, paramVec3d.x, paramVec3d.y, paramVec3d.z);
  }
  
  public static boolean isAboveWater(Entity paramEntity) {
    return isAboveWater(paramEntity, false);
  }
  
  public static Vec3d getInterpolatedRenderPos(Entity paramEntity, float paramFloat) {
    return getInterpolatedPos(paramEntity, paramFloat).subtract((ICEWrapper.getMinecraft().getRenderManager()).renderPosX, (ICEWrapper.getMinecraft().getRenderManager()).renderPosY, (ICEWrapper.getMinecraft().getRenderManager()).renderPosZ);
  }
  
  public static boolean isInWater(Entity paramEntity) {
    if (paramEntity == null)
      return false; 
    double d = paramEntity.posY + 0.01D;
    for (int i = MathHelper.floor(paramEntity.posX); i < MathHelper.ceil(paramEntity.posX); i++) {
      for (int j = MathHelper.floor(paramEntity.posZ); j < MathHelper.ceil(paramEntity.posZ); j++) {
        BlockPos blockPos = new BlockPos(i, (int)d, j);
        if (ICEWrapper.getWorld().getBlockState(blockPos).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
  
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
  
  public static boolean isPassive(Entity paramEntity) {
    return (paramEntity instanceof EntityWolf && ((EntityWolf)paramEntity).isAngry()) ? false : ((paramEntity instanceof net.minecraft.entity.passive.EntityAnimal || paramEntity instanceof net.minecraft.entity.EntityAgeable || paramEntity instanceof net.minecraft.entity.passive.EntityTameable || paramEntity instanceof net.minecraft.entity.passive.EntityAmbientCreature || paramEntity instanceof net.minecraft.entity.passive.EntitySquid) ? true : ((paramEntity instanceof EntityIronGolem && ((EntityIronGolem)paramEntity).getRevengeTarget() == null)));
  }
  
  public static boolean isDrivenByPlayer(Entity paramEntity) {
    return (ICEWrapper.getPlayer() != null && paramEntity != null && paramEntity.equals(ICEWrapper.getPlayer().getRidingEntity()));
  }
  
  public static boolean isHostileMob(Entity paramEntity) {
    return (paramEntity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(paramEntity));
  }
  
  public static boolean isMobAggressive(Entity paramEntity) {
    if (paramEntity instanceof EntityPigZombie) {
      if (((EntityPigZombie)paramEntity).isArmsRaised() || ((EntityPigZombie)paramEntity).isAngry())
        return true; 
    } else {
      if (paramEntity instanceof EntityWolf)
        return (((EntityWolf)paramEntity).isAngry() && !ICEWrapper.getPlayer().equals(((EntityWolf)paramEntity).getOwner())); 
      if (paramEntity instanceof EntityEnderman)
        return ((EntityEnderman)paramEntity).isScreaming(); 
    } 
    return isHostileMob(paramEntity);
  }
  
  public static boolean isLiving(Entity paramEntity) {
    return paramEntity instanceof net.minecraft.entity.EntityLivingBase;
  }
  
  public static Vec3d getInterpolatedAmount(Entity paramEntity, double paramDouble) {
    return getInterpolatedAmount(paramEntity, paramDouble, paramDouble, paramDouble);
  }
  
  public static Vec3d getInterpolatedPos(Entity paramEntity, float paramFloat) {
    return (new Vec3d(paramEntity.lastTickPosX, paramEntity.lastTickPosY, paramEntity.lastTickPosZ)).add(getInterpolatedAmount(paramEntity, paramFloat));
  }
}
