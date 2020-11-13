//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SelfTrap extends Module {
  public int getViewYaw() {
    return (int)Math.abs(Math.floor(((Minecraft.getMinecraft()).player.rotationYaw * 8.0F / 360.0F)));
  }
  
  public void onDisable() {
    this.closestTarget = null;
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
  
  public static BlockPos getPlayerPos() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
  
  private boolean placeBlockInRange(BlockPos paramBlockPos) {
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
    mc.rightClickDelayTimer = 0;
    return true;
  }
  
  private boolean isInRange(BlockPos paramBlockPos) {
    NonNullList nonNullList = NonNullList.create();
    nonNullList.addAll((Collection)getSphere(getPlayerPos(), (float)this.range.getValDouble(), (int)this.range.getValDouble(), false, true, 0).stream().collect(Collectors.toList()));
    return nonNullList.contains(paramBlockPos);
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
  
  public List<BlockPos> getSphere(BlockPos paramBlockPos, float paramFloat, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2) {
    ArrayList<BlockPos> arrayList = new ArrayList();
    int i = paramBlockPos.getX();
    int j = paramBlockPos.getY();
    int k = paramBlockPos.getZ();
    for (int m = i - (int)paramFloat; m <= i + paramFloat; m++) {
      int n = k - (int)paramFloat;
      while (n <= k + paramFloat) {
        int i1 = paramBoolean2 ? (j - (int)paramFloat) : j;
        while (true) {
          if (i1 < (paramBoolean2 ? (j + paramFloat) : (j + paramInt1))) {
            double d = ((i - m) * (i - m) + (k - n) * (k - n) + (paramBoolean2 ? ((j - i1) * (j - i1)) : 0));
            if (d < (paramFloat * paramFloat) && (!paramBoolean1 || d >= ((paramFloat - 1.0F) * (paramFloat - 1.0F)))) {
              BlockPos blockPos = new BlockPos(m, i1 + paramInt2, n);
              arrayList.add(blockPos);
            } 
            i1++;
            continue;
          } 
          n++;
        } 
      } 
    } 
    return arrayList;
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
  
  private void centerPlayer(double paramDouble1, double paramDouble2, double paramDouble3) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(paramDouble1, paramDouble2, paramDouble3, true));
    mc.player.setPosition(paramDouble1, paramDouble2, paramDouble3);
  }
  
  private double getDst(Vec3d paramVec3d) {
    return mc.player.getPositionVector().distanceTo(paramVec3d);
  }
  
  public void onEnable() {
    if (mc.player == null) {
      disable();
      return;
    } 
    double d1 = mc.player.getPosition().getY();
    double d2 = mc.player.getPosition().getX();
    double d3 = mc.player.getPosition().getZ();
    Vec3d vec3d1 = new Vec3d(d2 + 0.5D, d1, d3 + 0.5D);
    Vec3d vec3d2 = new Vec3d(d2 + 0.5D, d1, d3 - 0.5D);
    Vec3d vec3d3 = new Vec3d(d2 - 0.5D, d1, d3 - 0.5D);
    Vec3d vec3d4 = new Vec3d(d2 - 0.5D, d1, d3 + 0.5D);
    if (this.autocenter.getValBoolean()) {
      if (getDst(vec3d1) < getDst(vec3d2) && getDst(vec3d1) < getDst(vec3d3) && getDst(vec3d1) < getDst(vec3d4)) {
        d2 = mc.player.getPosition().getX() + 0.5D;
        d3 = mc.player.getPosition().getZ() + 0.5D;
        centerPlayer(d2, d1, d3);
      } 
      if (getDst(vec3d2) < getDst(vec3d1) && getDst(vec3d2) < getDst(vec3d3) && getDst(vec3d2) < getDst(vec3d4)) {
        d2 = mc.player.getPosition().getX() + 0.5D;
        d3 = mc.player.getPosition().getZ() - 0.5D;
        centerPlayer(d2, d1, d3);
      } 
      if (getDst(vec3d3) < getDst(vec3d1) && getDst(vec3d3) < getDst(vec3d2) && getDst(vec3d3) < getDst(vec3d4)) {
        d2 = mc.player.getPosition().getX() - 0.5D;
        d3 = mc.player.getPosition().getZ() - 0.5D;
        centerPlayer(d2, d1, d3);
      } 
      if (getDst(vec3d4) < getDst(vec3d1) && getDst(vec3d4) < getDst(vec3d2) && getDst(vec3d4) < getDst(vec3d3)) {
        d2 = mc.player.getPosition().getX() - 0.5D;
        d3 = mc.player.getPosition().getZ() + 0.5D;
        centerPlayer(d2, d1, d3);
      } 
    } 
    this.firstRun = true;
    this.playerHotbarSlot = mc.player.inventory.currentItem;
    this.lastHotbarSlot = -1;
  }
  
  public void onUpdate() {
    if (this.smart.getValBoolean())
      findClosestTarget(); 
    if (mc.player == null)
      return; 
    if (!this.firstRun) {
      if (this.delayStep < (int)this.delay.getValDouble()) {
        this.delayStep++;
        return;
      } 
      this.delayStep = 0;
    } 
    ArrayList<? super Vec3d> arrayList = new ArrayList();
    if (!this.smart.getValBoolean()) {
      Collections.addAll(arrayList, BlockUtil.TRAP);
    } else if (getViewYaw() <= 315 && getViewYaw() >= 225) {
      Collections.addAll(arrayList, BlockUtil.BLOCKOVERHEADFACINGNEGX);
    } else if ((getViewYaw() < 45 && getViewYaw() > 0) || (getViewYaw() > 315 && getViewYaw() < 360)) {
      Collections.addAll(arrayList, BlockUtil.BLOCKOVERHEADFACINGPOSZ);
    } else if (getViewYaw() <= 135 && getViewYaw() >= 45) {
      Collections.addAll(arrayList, BlockUtil.BLOCKOVERHEADFACINGPOSX);
    } else if (getViewYaw() < 225 && getViewYaw() > 135) {
      Collections.addAll(arrayList, BlockUtil.BLOCKOVERHEADFACINGNEGZ);
    } 
    byte b = 0;
    while (b < (int)this.blocksper.getValDouble()) {
      if (this.offsetStep >= arrayList.size()) {
        this.offsetStep = 0;
        break;
      } 
      BlockPos blockPos1 = new BlockPos(arrayList.get(this.offsetStep));
      BlockPos blockPos2 = (new BlockPos(mc.player.getPositionVector())).down().add(blockPos1.x, blockPos1.y, blockPos1.z);
      if (this.closestTarget != null && this.smart.getValBoolean()) {
        if (isInRange(getClosestTargetPos()) && placeBlockInRange(blockPos2))
          b++; 
      } else if (!this.smart.getValBoolean() && placeBlockInRange(blockPos2)) {
        b++;
      } 
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
    Vec3d vec3d = new Vec3d(0.0D, 3.0D, 0.0D);
    BlockPos blockPos = (new BlockPos(mc.player.getPositionVector())).down().add(vec3d.x, vec3d.y, vec3d.z);
    Block block = mc.world.getBlockState(blockPos).getBlock();
  }
  
  public BlockPos getClosestTargetPos() {
    return (this.closestTarget != null) ? new BlockPos(Math.floor(this.closestTarget.posX), Math.floor(this.closestTarget.posY), Math.floor(this.closestTarget.posZ)) : null;
  }
  
  public SelfTrap() {
    super("SelfTrap", 0, Category.COMBAT);
  }
}
