//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AutoAnvil extends Module {
  public void onDisable() {
    if (mc != null && mc.player != null && mc.player.connection != null) {
      this.tick = 0;
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    } 
  }
  
  public List<BlockPos> bestPositions(EntityPlayer paramEntityPlayer, int paramInt) {
    ArrayList<BlockPos> arrayList = new ArrayList();
    ArrayList<Vec3d> arrayList1 = new ArrayList();
    byte b;
    for (b = 0; b < (int)this.droprange.getValDouble(); b++)
      arrayList1.add(new Vec3d(0.0D, b, 0.0D)); 
    for (b = 0; b < arrayList1.size(); b++) {
      if (b >= arrayList1.size()) {
        b = 0;
        break;
      } 
      if (arrayList.size() >= paramInt)
        break; 
      BlockPos blockPos1 = new BlockPos(arrayList1.get(b));
      BlockPos blockPos2 = (new BlockPos(paramEntityPlayer.getPositionVector())).down().add(blockPos1.x, blockPos1.y, blockPos1.z);
      if (canPlace(blockPos2, this.placerange.getValDouble()))
        arrayList.add(blockPos1); 
    } 
    return arrayList;
  }
  
  public boolean isTrapped(EntityPlayer paramEntityPlayer) {
    boolean bool = false;
    ArrayList<Vec3d> arrayList = new ArrayList();
    byte b;
    for (b = 0; b < (int)this.droprange.getValDouble(); b++)
      arrayList.add(new Vec3d(0.0D, b, 0.0D)); 
    for (b = 0; b < arrayList.size(); b++) {
      if (b >= arrayList.size()) {
        b = 0;
        break;
      } 
      BlockPos blockPos1 = new BlockPos(arrayList.get(b));
      BlockPos blockPos2 = (new BlockPos(paramEntityPlayer.getPositionVector())).down().add(blockPos1.x, blockPos1.y, blockPos1.z);
      if (canPlace(blockPos2, this.placerange.getValDouble())) {
        bool = true;
        break;
      } 
    } 
    return bool;
  }
  
  private boolean placeAnvil(BlockPos paramBlockPos, double paramDouble) {
    Block block1 = mc.world.getBlockState(paramBlockPos).getBlock();
    if (!(block1 instanceof net.minecraft.block.BlockAir) && !(block1 instanceof net.minecraft.block.BlockLiquid))
      return false; 
    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(paramBlockPos))) {
      if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb))
        return false; 
    } 
    EnumFacing enumFacing1 = BlockUtil.getPlaceableSide(paramBlockPos);
    if (enumFacing1 == null)
      return false; 
    BlockPos blockPos = paramBlockPos.offset(enumFacing1);
    EnumFacing enumFacing2 = enumFacing1.getOpposite();
    if (!BlockUtil.canBeClicked(blockPos))
      return false; 
    Vec3d vec3d = (new Vec3d((Vec3i)blockPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
    Block block2 = mc.world.getBlockState(blockPos).getBlock();
    if (mc.player.getPositionVector().distanceTo(vec3d) > paramDouble)
      return false; 
    int i = findAnvil();
    if (i == -1 || i > 8 || i < 0) {
      disable();
      return false;
    } 
    if (mc.player.inventory.currentItem != i)
      mc.player.inventory.currentItem = i; 
    if (BlockUtil.blackList.contains(block2) || BlockUtil.shulkerList.contains(block2))
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING)); 
    BlockUtil.faceVectorPacketInstant(vec3d);
    mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing2, vec3d, EnumHand.MAIN_HAND);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 4;
    return true;
  }
  
  private boolean canPlace(BlockPos paramBlockPos, double paramDouble) {
    Block block = mc.world.getBlockState(paramBlockPos).getBlock();
    if (!(block instanceof net.minecraft.block.BlockAir) && !(block instanceof net.minecraft.block.BlockLiquid) && !(block instanceof net.minecraft.block.BlockAnvil))
      return false; 
    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(paramBlockPos))) {
      if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb))
        return false; 
    } 
    EnumFacing enumFacing1 = BlockUtil.getPlaceableSide(paramBlockPos);
    if (enumFacing1 == null)
      return false; 
    BlockPos blockPos = paramBlockPos.offset(enumFacing1);
    EnumFacing enumFacing2 = enumFacing1.getOpposite();
    if (!BlockUtil.canBeClicked(blockPos))
      return false; 
    Vec3d vec3d = (new Vec3d((Vec3i)blockPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
    return !(mc.player.getPositionVector().distanceTo(vec3d) > paramDouble);
  }
  
  private int findAnvil() {
    byte b = -1;
    for (byte b1 = 0; b1 < 9; b1++) {
      ItemStack itemStack = mc.player.inventory.getStackInSlot(b1);
      if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof ItemBlock) {
        Block block = ((ItemBlock)itemStack.getItem()).getBlock();
        if (block instanceof net.minecraft.block.BlockAnvil) {
          b = b1;
          break;
        } 
      } 
    } 
    return b;
  }
  
  public void onUpdate() {
    if (mc.world != null && mc.world.playerEntities != null) {
      if (findAnvil() == -1)
        disable(); 
      if (findClosestTrapped() != null) {
        this.modInfo = (findClosestTrapped().getName() != null) ? findClosestTrapped().getName() : "";
        this.tick++;
        if (this.tick >= (int)this.delay.getValDouble() * 2) {
          if (this.multiplace.getValBoolean()) {
            for (BlockPos blockPos : bestPositions(findClosestTrapped(), (int)this.multiplaceamt.getValDouble()))
              placeAnvil((new BlockPos(findClosestTrapped().getPositionVector())).down().add((Vec3i)blockPos), this.placerange.getValDouble()); 
          } else {
            for (BlockPos blockPos : bestPositions(findClosestTrapped(), 1))
              placeAnvil((new BlockPos(findClosestTrapped().getPositionVector())).down().add((Vec3i)blockPos), this.placerange.getValDouble()); 
          } 
          this.tick = 0;
        } 
      } 
    } 
  }
  
  public AutoAnvil() {
    super("AutoAnvil", 0, Category.COMBAT);
  }
  
  public void onEnable() {
    if (mc != null && mc.player != null && mc.player.connection != null) {
      this.tick = 0;
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    } 
  }
  
  public EntityPlayer findClosestTrapped() {
    List list = mc.world.playerEntities;
    EntityPlayer entityPlayer = null;
    for (EntityPlayer entityPlayer1 : list) {
      if (entityPlayer1 == mc.player || Friends.isFriend(entityPlayer1.getName()) || !(entityPlayer1 instanceof net.minecraft.entity.EntityLivingBase) || entityPlayer1.getHealth() <= 0.0F)
        continue; 
      if (entityPlayer == null) {
        entityPlayer = entityPlayer1;
        continue;
      } 
      if (mc.player.getDistance((Entity)entityPlayer1) < mc.player.getDistance((Entity)entityPlayer) && isTrapped(entityPlayer1))
        entityPlayer = entityPlayer1; 
    } 
    return entityPlayer;
  }
}
