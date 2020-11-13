//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
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

public class AutoTrap extends Module {
  private boolean placeBlockInRange(BlockPos paramBlockPos, double paramDouble) {
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
    int i = findObiInHotbar();
    if (i == -1)
      disable(); 
    if (this.lastHotbarSlot != i) {
      mc.player.inventory.currentItem = i;
      this.lastHotbarSlot = i;
    } 
    if ((!this.isSneaking && BlockUtil.blackList.contains(block2)) || BlockUtil.shulkerList.contains(block2)) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      this.isSneaking = true;
    } 
    BlockUtil.faceVectorPacketInstant(vec3d);
    mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing2, vec3d, EnumHand.MAIN_HAND);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 4;
    return true;
  }
  
  private void findClosestTarget() {
    List list = mc.world.playerEntities;
    this.closestTarget = null;
    for (EntityPlayer entityPlayer : list) {
      if (entityPlayer == mc.player || Friends.isFriend(entityPlayer.getName()) || !(entityPlayer instanceof net.minecraft.entity.EntityLivingBase) || entityPlayer.getHealth() <= 0.0F)
        continue; 
      if (this.closestTarget == null) {
        this.closestTarget = entityPlayer;
        continue;
      } 
      if (mc.player.getDistance((Entity)entityPlayer) < mc.player.getDistance((Entity)this.closestTarget))
        this.closestTarget = entityPlayer; 
    } 
  }
  
  public void onDisable() {
    if (mc.player == null)
      return; 
    if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1)
      mc.player.inventory.currentItem = this.playerHotbarSlot; 
    if (this.isSneaking) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
      this.isSneaking = false;
    } 
    this.playerHotbarSlot = -1;
    this.lastHotbarSlot = -1;
  }
  
  public void onUpdate() {
    if (mc.player == null)
      return; 
    if (!this.firstRun) {
      if (this.delayStep < this.delay.getValDouble()) {
        this.delayStep++;
        return;
      } 
      this.delayStep = 0;
    } 
    findClosestTarget();
    if (this.closestTarget == null) {
      if (this.firstRun)
        this.firstRun = false; 
      return;
    } 
    if (this.firstRun) {
      this.firstRun = false;
      this.lastTickTargetName = this.closestTarget.getName();
    } else if (!this.lastTickTargetName.equals(this.closestTarget.getName())) {
      this.lastTickTargetName = this.closestTarget.getName();
      this.offsetStep = 0;
    } 
    ArrayList<? super Vec3d> arrayList = new ArrayList();
    Collections.addAll(arrayList, BlockUtil.TRAP);
    byte b = 0;
    while (b < this.blocksper.getValDouble()) {
      if (this.offsetStep >= arrayList.size()) {
        this.offsetStep = 0;
        break;
      } 
      BlockPos blockPos1 = new BlockPos(arrayList.get(this.offsetStep));
      BlockPos blockPos2 = (new BlockPos(this.closestTarget.getPositionVector())).down().add(blockPos1.x, blockPos1.y, blockPos1.z);
      if (placeBlockInRange(blockPos2, this.range.getValDouble()))
        b++; 
      this.offsetStep++;
    } 
    if (b > 0) {
      if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
        mc.player.inventory.currentItem = this.playerHotbarSlot;
        this.lastHotbarSlot = this.playerHotbarSlot;
      } 
      if (this.isSneaking) {
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.isSneaking = false;
      } 
    } 
    this.modInfo = (this.closestTarget != null) ? this.closestTarget.getName() : "None";
  }
  
  public void onEnable() {
    if (mc.player == null) {
      disable();
      return;
    } 
    this.firstRun = true;
    this.playerHotbarSlot = mc.player.inventory.currentItem;
    this.lastHotbarSlot = -1;
  }
  
  private int findObiInHotbar() {
    byte b = -1;
    for (byte b1 = 0; b1 < 9; b1++) {
      ItemStack itemStack = mc.player.inventory.getStackInSlot(b1);
      if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof ItemBlock) {
        Block block = ((ItemBlock)itemStack.getItem()).getBlock();
        if (block instanceof net.minecraft.block.BlockObsidian) {
          b = b1;
          break;
        } 
      } 
    } 
    return b;
  }
  
  public AutoTrap() {
    super("AutoTrap", 0, Category.COMBAT);
  }
}
