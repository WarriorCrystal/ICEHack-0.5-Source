//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PlayerHelper {
  public static Block getBlockAbovePlayer(double paramDouble) {
    return minecraft.world.getBlockState(new BlockPos(minecraft.player.posX, minecraft.player.posY + paramDouble, minecraft.player.posZ)).getBlock();
  }
  
  public static boolean isInsideBlock() {
    for (int i = MathHelper.floor((minecraft.player.getEntityBoundingBox()).minX); i < MathHelper.floor((minecraft.player.getEntityBoundingBox()).maxX) + 1; i++) {
      for (int j = MathHelper.floor((minecraft.player.getEntityBoundingBox()).minY); j < MathHelper.floor((minecraft.player.getEntityBoundingBox()).maxY) + 1; j++) {
        for (int k = MathHelper.floor((minecraft.player.getEntityBoundingBox()).minZ); k < MathHelper.floor((minecraft.player.getEntityBoundingBox()).maxZ) + 1; k++) {
          Block block = minecraft.world.getBlockState(new BlockPos(i, j, k)).getBlock();
          if (block != null && !(block instanceof net.minecraft.block.BlockAir)) {
            AxisAlignedBB axisAlignedBB = block.getSelectedBoundingBox(minecraft.world.getBlockState(new BlockPos(i, j, k)), (World)(Minecraft.getMinecraft()).world, new BlockPos(i, j, k));
            if (axisAlignedBB != null && minecraft.player.getEntityBoundingBox().intersects(axisAlignedBB))
              return true; 
          } 
        } 
      } 
    } 
    return false;
  }
  
  public static boolean isMoving() {
    return (minecraft.player.moveForward != 0.0D || minecraft.player.moveStrafing != 0.0D);
  }
  
  public static void drownPlayer() {
    for (byte b = 0; b < 'Ǵ'; b++) {
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer());
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer());
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer());
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer());
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer());
    } 
  }
  
  public static boolean isInLiquid() {
    if (minecraft.player == null)
      return false; 
    boolean bool = false;
    int i = (int)(minecraft.player.getEntityBoundingBox()).minY;
    for (int j = MathHelper.floor((minecraft.player.getEntityBoundingBox()).minX); j < MathHelper.floor((minecraft.player.getEntityBoundingBox()).maxX) + 1; j++) {
      for (int k = MathHelper.floor((minecraft.player.getEntityBoundingBox()).minZ); k < MathHelper.floor((minecraft.player.getEntityBoundingBox()).maxZ) + 1; k++) {
        Block block = minecraft.world.getBlockState(new BlockPos(j, i, k)).getBlock();
        if (block != null && !(block instanceof net.minecraft.block.BlockAir)) {
          if (!(block instanceof net.minecraft.block.BlockLiquid))
            return false; 
          bool = true;
        } 
      } 
    } 
    return bool;
  }
  
  public static boolean isInLiquid(double paramDouble) {
    return getBlockBelowPlayer(-paramDouble) instanceof net.minecraft.block.BlockLiquid;
  }
  
  public static boolean isOnLiquid() {
    AxisAlignedBB axisAlignedBB = minecraft.player.getEntityBoundingBox();
    axisAlignedBB = axisAlignedBB.contract(0.0D, 0.0D, 0.0D).offset(0.0D, -0.02D, 0.0D);
    boolean bool = false;
    int i = (int)axisAlignedBB.minY;
    for (int j = MathHelper.floor(axisAlignedBB.minX); j < MathHelper.floor(axisAlignedBB.maxX + 1.0D); j++) {
      for (int k = MathHelper.floor(axisAlignedBB.minZ); k < MathHelper.floor(axisAlignedBB.maxZ + 1.0D); k++) {
        Block block = minecraft.world.getBlockState(new BlockPos(j, i, k)).getBlock();
        if (block != Blocks.AIR) {
          if (!(block instanceof net.minecraft.block.BlockLiquid))
            return false; 
          bool = true;
        } 
      } 
    } 
    return bool;
  }
  
  public static Block getBlockBelowPlayer(double paramDouble) {
    return minecraft.world.getBlockState(new BlockPos(minecraft.player.posX, minecraft.player.posY - paramDouble, minecraft.player.posZ)).getBlock();
  }
  
  public static float getFOV(float[] paramArrayOffloat) {
    float f1 = paramArrayOffloat[0];
    float f2 = paramArrayOffloat[1];
    f1 = wrapAngleTo180(f1);
    f2 = wrapAngleTo180(f2);
    float f3 = wrapAngleTo180(minecraft.player.rotationYaw);
    float f4 = wrapAngleTo180(minecraft.player.rotationPitch);
    float f5 = Math.abs(f1 - f3);
    float f6 = Math.abs(f2 - f4);
    return f5 + f6;
  }
  
  public static boolean isAiming(float paramFloat1, float paramFloat2, int paramInt) {
    paramFloat1 = wrapAngleTo180(paramFloat1);
    paramFloat2 = wrapAngleTo180(paramFloat2);
    float f1 = wrapAngleTo180(minecraft.player.rotationYaw);
    float f2 = wrapAngleTo180(minecraft.player.rotationPitch);
    float f3 = Math.abs(paramFloat1 - f1);
    float f4 = Math.abs(paramFloat2 - f2);
    return (f3 + f4 <= paramInt);
  }
  
  public static boolean isPressingMoveKeybinds() {
    return (minecraft.gameSettings.keyBindForward.isKeyDown() || minecraft.gameSettings.keyBindBack.isKeyDown() || minecraft.gameSettings.keyBindLeft.isKeyDown() || minecraft.gameSettings.keyBindRight.isKeyDown());
  }
  
  public static float wrapAngleTo180(float paramFloat) {
    paramFloat %= 360.0F;
    if (paramFloat >= 180.0F)
      paramFloat -= 360.0F; 
    if (paramFloat < -180.0F)
      paramFloat += 360.0F; 
    return paramFloat;
  }
  
  public static String getFacingWithProperCapitals() {
    String str = minecraft.player.getHorizontalFacing().getName();
    switch (str) {
      case "north":
        str = "North";
        break;
      case "south":
        str = "South";
        break;
      case "west":
        str = "West";
        break;
      case "east":
        str = "East";
        break;
    } 
    return str;
  }
  
  public static void damagePlayer() {
    for (byte b = 0; b < 81; b++) {
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer.Position(minecraft.player.posX, minecraft.player.posY + 0.05D, minecraft.player.posZ, false));
      minecraft.player.connection.sendPacket((Packet)new CPacketPlayer.Position(minecraft.player.posX, minecraft.player.posY, minecraft.player.posZ, false));
    } 
  }
}
