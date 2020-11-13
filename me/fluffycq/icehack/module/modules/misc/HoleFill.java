//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class HoleFill extends Module {
  private List<BlockPos> findCrystalBlocks() {
    NonNullList nonNullList = NonNullList.create();
    if (this.closestTarget != null)
      nonNullList.addAll((Collection)getSphere(getClosestTargetPos(), (float)this.playerRange.getValDouble(), (int)this.range.getValDouble(), false, true, 0).stream().filter(this::IsHole).filter(this::isInRange).collect(Collectors.toList())); 
    return (List<BlockPos>)nonNullList;
  }
  
  private static void setYawAndPitch(float paramFloat1, float paramFloat2) {
    yaw = paramFloat1;
    pitch = paramFloat2;
    isSpoofingAngles = true;
  }
  
  private void lookAtPacket(double paramDouble1, double paramDouble2, double paramDouble3, EntityPlayer paramEntityPlayer) {
    double[] arrayOfDouble = calculateLookAt(paramDouble1, paramDouble2, paramDouble3, paramEntityPlayer);
    setYawAndPitch((float)arrayOfDouble[0], (float)arrayOfDouble[1]);
  }
  
  public void onUpdate() {
    if (mc.world == null)
      return; 
    findClosestTarget();
    List<BlockPos> list = findCrystalBlocks();
    BlockPos blockPos = null;
    double d1 = 0.0D;
    double d2 = 0.0D;
    byte b = (mc.player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) ? mc.player.inventory.currentItem : -1;
    if (b == -1)
      for (byte b1 = 0; b1 < 9; b1++) {
        if (mc.player.inventory.getStackInSlot(b1).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
          b = b1;
          break;
        } 
      }  
    if (b == -1)
      return; 
    for (BlockPos blockPos1 : list) {
      if (mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos1)).isEmpty()) {
        if (isInRange(blockPos1)) {
          blockPos = blockPos1;
          continue;
        } 
        blockPos = blockPos1;
      } 
    } 
    this.render = blockPos;
    if (blockPos != null && mc.player.onGround) {
      int i = mc.player.inventory.currentItem;
      if (mc.player.inventory.currentItem != b)
        mc.player.inventory.currentItem = b; 
      lookAtPacket(blockPos.x + 0.5D, blockPos.y - 0.5D, blockPos.z + 0.5D, (EntityPlayer)mc.player);
      BlockUtil.placeBlockScaffold(this.render);
      mc.player.swingArm(EnumHand.MAIN_HAND);
      mc.player.inventory.currentItem = i;
      resetRotation();
    } 
  }
  
  private boolean IsHole(BlockPos paramBlockPos) {
    BlockPos blockPos1 = paramBlockPos.add(0, 1, 0);
    BlockPos blockPos2 = paramBlockPos.add(0, 0, 0);
    BlockPos blockPos3 = paramBlockPos.add(0, 0, -1);
    BlockPos blockPos4 = paramBlockPos.add(1, 0, 0);
    BlockPos blockPos5 = paramBlockPos.add(-1, 0, 0);
    BlockPos blockPos6 = paramBlockPos.add(0, 0, 1);
    BlockPos blockPos7 = paramBlockPos.add(0, 2, 0);
    BlockPos blockPos8 = paramBlockPos.add(0.5D, 0.5D, 0.5D);
    BlockPos blockPos9 = paramBlockPos.add(0, -1, 0);
    return (mc.world.getBlockState(blockPos1).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos2).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos7).getBlock() == Blocks.AIR && (mc.world.getBlockState(blockPos3).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos3).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(blockPos4).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos4).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(blockPos5).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos5).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(blockPos6).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos6).getBlock() == Blocks.BEDROCK) && mc.world.getBlockState(blockPos8).getBlock() == Blocks.AIR && (mc.world.getBlockState(blockPos9).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos9).getBlock() == Blocks.BEDROCK));
  }
  
  private static void resetRotation() {
    if (isSpoofingAngles) {
      yaw = mc.player.rotationYaw;
      pitch = mc.player.rotationPitch;
      isSpoofingAngles = false;
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
  
  public static BlockPos getPlayerPos() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
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
  
  private double getDistanceToBlockPos(BlockPos paramBlockPos1, BlockPos paramBlockPos2) {
    double d1 = (paramBlockPos1.getX() - paramBlockPos2.getX());
    double d2 = (paramBlockPos1.getY() - paramBlockPos2.getY());
    double d3 = (paramBlockPos1.getZ() - paramBlockPos2.getZ());
    return Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
  }
  
  private boolean isInRange(BlockPos paramBlockPos) {
    NonNullList nonNullList = NonNullList.create();
    nonNullList.addAll((Collection)getSphere(getPlayerPos(), (float)this.playerRange.getValDouble(), (int)this.range.getValDouble(), false, true, 0).stream().filter(this::IsHole).collect(Collectors.toList()));
    return nonNullList.contains(paramBlockPos);
  }
  
  public HoleFill() {
    super("HoleFill", 0, Category.MISC);
    this.packetListener = new Listener(paramSend -> {
          Packet packet = paramSend.getPacket();
          if (packet instanceof CPacketPlayer && isSpoofingAngles) {
            ((CPacketPlayer)packet).yaw = (float)yaw;
            ((CPacketPlayer)packet).pitch = (float)pitch;
          } 
        }new java.util.function.Predicate[0]);
    this.range = new Setting("Range", this, 4.5D, 1.0D, 6.5D, false);
    this.playerRange = new Setting("EnemyRange", this, 4.0D, 1.0D, 7.0D, false);
  }
  
  public BlockPos getClosestTargetPos() {
    return (this.closestTarget != null) ? new BlockPos(Math.floor(this.closestTarget.posX), Math.floor(this.closestTarget.posY), Math.floor(this.closestTarget.posZ)) : null;
  }
}
