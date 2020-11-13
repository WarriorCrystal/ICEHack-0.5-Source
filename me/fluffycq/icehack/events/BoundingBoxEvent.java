package me.fluffycq.icehack.events;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BoundingBoxEvent extends ICEEvent {
  public void setAabb(AxisAlignedBB paramAxisAlignedBB) {
    this.aabb = paramAxisAlignedBB;
  }
  
  public final Entity getEntity() {
    return this.entity;
  }
  
  public final Block getBlock() {
    return this.block;
  }
  
  public BoundingBoxEvent(Block paramBlock, BlockPos paramBlockPos, AxisAlignedBB paramAxisAlignedBB, List<AxisAlignedBB> paramList, @Nullable Entity paramEntity) {
    this.block = paramBlock;
    this.pos = paramBlockPos;
    this.aabb = paramAxisAlignedBB;
    this.collidingBoxes = paramList;
    this.entity = paramEntity;
  }
  
  public final AxisAlignedBB getBoundingBox() {
    return this.aabb;
  }
  
  public final BlockPos getPos() {
    return this.pos;
  }
  
  public final List<AxisAlignedBB> getCollidingBoxes() {
    return this.collidingBoxes;
  }
}
