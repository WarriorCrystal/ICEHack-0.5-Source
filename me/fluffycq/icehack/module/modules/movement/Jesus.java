//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Jesus extends Module {
  private boolean isInLiquid() {
    double d = mc.player.posY + 0.01D;
    for (int i = MathHelper.floor(mc.player.posX); i < MathHelper.ceil(mc.player.posX); i++) {
      for (int j = MathHelper.floor(mc.player.posZ); j < MathHelper.ceil(mc.player.posZ); j++) {
        BlockPos blockPos = new BlockPos(i, (int)d, j);
        if (mc.world.getBlockState(blockPos).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
  
  static {
  
  }
  
  private boolean isOnWater() {
    double d = mc.player.posY - 0.03D;
    for (int i = MathHelper.floor(mc.player.posX); i < MathHelper.ceil(mc.player.posX); i++) {
      for (int j = MathHelper.floor(mc.player.posZ); j < MathHelper.ceil(mc.player.posZ); j++) {
        BlockPos blockPos = new BlockPos(i, MathHelper.floor(d), j);
        if (mc.world.getBlockState(blockPos).getBlock() instanceof net.minecraft.block.BlockLiquid && mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER)
          return true; 
      } 
    } 
    return false;
  }
  
  public Jesus() {
    super("Jesus", 0, Category.MOVEMENT);
  }
  
  private boolean isOnLiquid() {
    double d = mc.player.posY - 0.03D;
    for (int i = MathHelper.floor(mc.player.posX); i < MathHelper.ceil(mc.player.posX); i++) {
      for (int j = MathHelper.floor(mc.player.posZ); j < MathHelper.ceil(mc.player.posZ); j++) {
        BlockPos blockPos = new BlockPos(i, MathHelper.floor(d), j);
        if (mc.world.getBlockState(blockPos).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
}
