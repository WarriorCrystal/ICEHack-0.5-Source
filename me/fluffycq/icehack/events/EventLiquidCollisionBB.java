package me.fluffycq.icehack.events;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EventLiquidCollisionBB extends ICEEvent {
  public EventLiquidCollisionBB() {}
  
  public BlockPos getBlockPos() {
    return this.blockPos;
  }
  
  public AxisAlignedBB getBoundingBox() {
    return this.boundingBox;
  }
  
  public EventLiquidCollisionBB(BlockPos paramBlockPos) {
    this.blockPos = paramBlockPos;
  }
  
  public void setBoundingBox(AxisAlignedBB paramAxisAlignedBB) {
    this.boundingBox = paramAxisAlignedBB;
  }
  
  public void setBlockPos(BlockPos paramBlockPos) {
    this.blockPos = paramBlockPos;
  }
}
