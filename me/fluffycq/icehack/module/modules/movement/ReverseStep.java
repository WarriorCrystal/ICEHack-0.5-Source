//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class ReverseStep extends Module {
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
  
  private boolean isBedrockHole(BlockPos paramBlockPos) {
    BlockPos[] arrayOfBlockPos = { paramBlockPos.north(), paramBlockPos.south(), paramBlockPos.east(), paramBlockPos.west(), paramBlockPos.down() };
    for (BlockPos blockPos : arrayOfBlockPos) {
      IBlockState iBlockState = mc.world.getBlockState(blockPos);
      if (iBlockState.getBlock() == Blocks.AIR || iBlockState.getBlock() != Blocks.BEDROCK)
        return false; 
    } 
    return true;
  }
  
  private boolean isElseHole(BlockPos paramBlockPos) {
    BlockPos[] arrayOfBlockPos = { paramBlockPos.north(), paramBlockPos.south(), paramBlockPos.east(), paramBlockPos.west(), paramBlockPos.down() };
    for (BlockPos blockPos : arrayOfBlockPos) {
      IBlockState iBlockState = mc.world.getBlockState(blockPos);
      if (iBlockState.getBlock() == Blocks.AIR || !iBlockState.isFullBlock())
        return false; 
    } 
    return true;
  }
  
  private boolean isBothHole(BlockPos paramBlockPos) {
    BlockPos[] arrayOfBlockPos = { paramBlockPos.north(), paramBlockPos.south(), paramBlockPos.east(), paramBlockPos.west(), paramBlockPos.down() };
    for (BlockPos blockPos : arrayOfBlockPos) {
      IBlockState iBlockState = mc.world.getBlockState(blockPos);
      if (iBlockState.getBlock() == Blocks.AIR || (iBlockState.getBlock() != Blocks.BEDROCK && iBlockState.getBlock() != Blocks.OBSIDIAN))
        return false; 
    } 
    return true;
  }
  
  private boolean isBlockValid(IBlockState paramIBlockState, BlockPos paramBlockPos) {
    return (paramIBlockState.getBlock() != Blocks.AIR) ? false : ((mc.player.getDistanceSq(paramBlockPos) < 1.0D) ? false : ((mc.world.getBlockState(paramBlockPos.up()).getBlock() != Blocks.AIR) ? false : ((mc.world.getBlockState(paramBlockPos.up(2)).getBlock() != Blocks.AIR) ? false : ((isBedrockHole(paramBlockPos) || isObbyHole(paramBlockPos) || isBothHole(paramBlockPos) || isElseHole(paramBlockPos))))));
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
  
  private double getNearestBlockBelow() {
    for (double d = mc.player.posY; d > 0.0D; d -= 0.001D) {
      if (!(mc.world.getBlockState(new BlockPos(mc.player.posX, d, mc.player.posZ)).getBlock() instanceof net.minecraft.block.BlockSlab) && mc.world.getBlockState(new BlockPos(mc.player.posX, d, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox((IBlockAccess)mc.world, new BlockPos(0, 0, 0)) != null)
        return d; 
    } 
    return -1.0D;
  }
  
  private boolean isObbyHole(BlockPos paramBlockPos) {
    BlockPos[] arrayOfBlockPos = { paramBlockPos.north(), paramBlockPos.south(), paramBlockPos.east(), paramBlockPos.west(), paramBlockPos.down() };
    for (BlockPos blockPos : arrayOfBlockPos) {
      IBlockState iBlockState = mc.world.getBlockState(blockPos);
      if (iBlockState.getBlock() == Blocks.AIR || iBlockState.getBlock() != Blocks.OBSIDIAN)
        return false; 
    } 
    return true;
  }
  
  public void onUpdate() {
    if (mc.world == null || mc.player == null || (ICEHack.fevents.moduleManager.getModule("Speed") != null && ICEHack.fevents.moduleManager.getModule("Speed").isEnabled()))
      return; 
    if (!mc.player.onGround) {
      if (mc.gameSettings.keyBindJump.isKeyDown())
        this.jumped = true; 
    } else {
      this.jumped = false;
    } 
    if (!this.jumped && mc.player.fallDistance < 0.5D && isInHole() && mc.player.posY - getNearestBlockBelow() <= 1.125D && mc.player.posY - getNearestBlockBelow() <= 0.95D && !isOnLiquid() && !isInLiquid()) {
      if (!mc.player.onGround)
        this.packets++; 
      if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        for (double d : this.oneblockPositions)
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position((blockPos.getX() + 0.5F), mc.player.posY - d, (blockPos.getZ() + 0.5F), true)); 
        mc.player.setPosition((blockPos.getX() + 0.5F), getNearestBlockBelow() + 0.1D, (blockPos.getZ() + 0.5F));
        this.packets = 0;
      } 
    } 
  }
  
  private boolean isInHole() {
    BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    IBlockState iBlockState = mc.world.getBlockState(blockPos);
    return isBlockValid(iBlockState, blockPos);
  }
  
  public ReverseStep() {
    super("ReverseStep", 0, Category.MOVEMENT);
  }
}
