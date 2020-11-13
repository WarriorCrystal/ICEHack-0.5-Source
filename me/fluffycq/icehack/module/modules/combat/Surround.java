//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Surround extends Module {
  public ArrayList<Vec3d> getSurround() {
    ArrayList<Vec3d> arrayList = new ArrayList();
    Vec3d vec3d1 = null;
    Vec3d vec3d2 = null;
    Vec3d vec3d3 = null;
    Vec3d vec3d4 = null;
    EnumFacing enumFacing = mc.player.getHorizontalFacing();
    if (enumFacing == EnumFacing.NORTH) {
      vec3d3 = new Vec3d(0.0D, 0.0D, -1.0D);
      vec3d1 = new Vec3d(-1.0D, 0.0D, 0.0D);
      vec3d2 = new Vec3d(1.0D, 0.0D, 0.0D);
      vec3d4 = new Vec3d(0.0D, 0.0D, 1.0D);
    } else if (enumFacing == EnumFacing.EAST) {
      vec3d3 = new Vec3d(1.0D, 0.0D, 0.0D);
      vec3d1 = new Vec3d(0.0D, 0.0D, -1.0D);
      vec3d2 = new Vec3d(0.0D, 0.0D, 1.0D);
      vec3d4 = new Vec3d(-1.0D, 0.0D, 0.0D);
    } else if (enumFacing == EnumFacing.SOUTH) {
      vec3d3 = new Vec3d(0.0D, 0.0D, 1.0D);
      vec3d1 = new Vec3d(1.0D, 0.0D, 0.0D);
      vec3d2 = new Vec3d(-1.0D, 0.0D, 0.0D);
      vec3d4 = new Vec3d(0.0D, 0.0D, -1.0D);
    } else if (enumFacing == EnumFacing.WEST) {
      vec3d3 = new Vec3d(-1.0D, 0.0D, 0.0D);
      vec3d1 = new Vec3d(0.0D, 0.0D, 1.0D);
      vec3d2 = new Vec3d(0.0D, 0.0D, -1.0D);
      vec3d4 = new Vec3d(1.0D, 0.0D, 0.0D);
    } 
    Vec3d[] arrayOfVec3d = { vec3d3.subtract(0.0D, 1.0D, 0.0D), vec3d3, vec3d2.subtract(0.0D, 1.0D, 0.0D), vec3d2, vec3d4.subtract(0.0D, 1.0D, 0.0D), vec3d4, vec3d1.subtract(0.0D, 1.0D, 0.0D), vec3d1 };
    for (Vec3d vec3d : arrayOfVec3d) {
      BlockPos blockPos1 = new BlockPos(vec3d);
      BlockPos blockPos2 = (new BlockPos(mc.player.getPositionVector())).add(blockPos1.x, blockPos1.y, blockPos1.z);
      Block block = mc.world.getBlockState(blockPos2).getBlock();
      if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid)
        arrayList.add(vec3d); 
    } 
    return arrayList;
  }
  
  public void onEnable() {
    if (mc.player == null) {
      disable();
      return;
    } 
    this.firstRun = true;
    this.playerHotbarSlot = mc.player.inventory.currentItem;
    this.lastHotbarSlot = -1;
    if (this.tp.getValBoolean()) {
      double d1 = mc.player.getPosition().getY();
      double d2 = mc.player.getPosition().getX();
      double d3 = mc.player.getPosition().getZ();
      Vec3d vec3d1 = new Vec3d(d2 + 0.5D, d1, d3 + 0.5D);
      Vec3d vec3d2 = new Vec3d(d2 + 0.5D, d1, d3 - 0.5D);
      Vec3d vec3d3 = new Vec3d(d2 - 0.5D, d1, d3 - 0.5D);
      Vec3d vec3d4 = new Vec3d(d2 - 0.5D, d1, d3 + 0.5D);
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
    this.isPlacing = false;
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
  
  static {
    togglePitch = false;
  }
  
  public boolean isSurrounded() {
    byte b = 0;
    for (BlockPos blockPos : this.cover) {
      Block block = mc.world.getBlockState(mc.player.getPosition().add(blockPos.x, blockPos.y, blockPos.z)).getBlock();
      if (!(block instanceof net.minecraft.block.BlockAir) && !(block instanceof net.minecraft.block.BlockLiquid))
        b++; 
    } 
    return (b == 4);
  }
  
  private double getDst(Vec3d paramVec3d) {
    return mc.player.getPositionVector().distanceTo(paramVec3d);
  }
  
  private static void setYawAndPitch(float paramFloat1, float paramFloat2) {
    yaw = paramFloat1;
    pitch = paramFloat2;
    isSpoofingAngles = true;
  }
  
  private static void resetRotation() {
    if (isSpoofingAngles) {
      yaw = mc.player.rotationYaw;
      pitch = mc.player.rotationPitch;
      isSpoofingAngles = false;
    } 
  }
  
  private void centerPlayer(double paramDouble1, double paramDouble2, double paramDouble3) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(paramDouble1, paramDouble2, paramDouble3, true));
    mc.player.setPosition(paramDouble1, paramDouble2, paramDouble3);
  }
  
  public void onUpdate() {
    if (mc.player == null)
      return; 
    if (findObiInHotbar() == -1 || findObiInHotbar() < 0 || findObiInHotbar() > 8) {
      disable();
      return;
    } 
    if (!this.firstRun) {
      if (this.delayStep < this.delay.getValDouble()) {
        this.delayStep++;
        return;
      } 
      this.delayStep = 0;
    } 
    if (this.firstRun)
      this.firstRun = false; 
    if (isSurrounded())
      this.isPlacing = false; 
    byte b = 0;
    while (b < this.bpt.getValDouble()) {
      ArrayList<Vec3d> arrayList = new ArrayList();
      int i = 0;
      arrayList = getSurround();
      i = getSurround().size();
      if (this.offsetStep >= i) {
        this.offsetStep = 0;
        break;
      } 
      BlockPos blockPos1 = new BlockPos(arrayList.get(this.offsetStep));
      BlockPos blockPos2 = (new BlockPos(mc.player.getPositionVector())).add(blockPos1.x, blockPos1.y, blockPos1.z);
      if (placeBlock(blockPos2, blockPos1.y))
        b++; 
      this.offsetStep++;
    } 
    if (b > 0) {
      if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.playerHotbarSlot));
        this.lastHotbarSlot = this.playerHotbarSlot;
      } 
      if (this.isSneaking) {
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.isSneaking = false;
      } 
    } 
    this.totalTicksRunning++;
  }
  
  public void onDisable() {
    if (mc.player == null)
      return; 
    if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1)
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.playerHotbarSlot)); 
    if (this.isSneaking) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
      this.isSneaking = false;
    } 
    this.playerHotbarSlot = -1;
    this.lastHotbarSlot = -1;
    this.isPlacing = false;
  }
  
  public Surround() {
    super("Surround", 0, Category.COMBAT);
    this.packetEvent = new Listener(paramSend -> {
          if (paramSend.getPacket() instanceof CPacketPlayer && this.isPlacing)
            mc.player.setRotationYawHead(((CPacketPlayer)paramSend.getPacket()).yaw); 
        }new java.util.function.Predicate[0]);
    this.tp = new Setting("Center", this, true);
    this.rotate = new Setting("Rotate", this, true);
    this.bpt = new Setting("BPT", this, 4.0D, 1.0D, 8.0D, true);
    this.delay = new Setting("Delay", this, 1.0D, 1.0D, 20.0D, true);
    this.cover.add(new BlockPos(1, -1, 0));
    this.cover.add(new BlockPos(0, -1, 1));
    this.cover.add(new BlockPos(-1, -1, 0));
    this.cover.add(new BlockPos(0, -1, -1));
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
        if (block instanceof net.minecraft.block.BlockEnderChest) {
          b = b1;
          break;
        } 
      } 
    } 
    return b;
  }
  
  private boolean placeBlock(BlockPos paramBlockPos, int paramInt) {
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
    if (this.rotate.getValBoolean() && paramInt != 1) {
      this.isPlacing = true;
      BlockUtil.faceVectorPacketInstant(vec3d);
    } 
    if (this.lastHotbarSlot != i) {
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(i));
      this.lastHotbarSlot = i;
    } 
    if ((!this.isSneaking && BlockUtil.blackList.contains(block2)) || BlockUtil.shulkerList.contains(block2)) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      this.isSneaking = true;
    } 
    mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing2, vec3d, EnumHand.MAIN_HAND);
    mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
    mc.rightClickDelayTimer = 4;
    if (isSpoofingAngles)
      if (togglePitch) {
        EntityPlayerSP entityPlayerSP = mc.player;
        entityPlayerSP.rotationPitch += 4.0E-4F;
        togglePitch = false;
      } else {
        EntityPlayerSP entityPlayerSP = mc.player;
        entityPlayerSP.rotationPitch -= 4.0E-4F;
        togglePitch = true;
      }  
    return true;
  }
  
  private void lookAtPacket(double paramDouble1, double paramDouble2, double paramDouble3, EntityPlayer paramEntityPlayer) {
    double[] arrayOfDouble = calculateLookAt(paramDouble1, paramDouble2, paramDouble3, paramEntityPlayer);
    setYawAndPitch((float)arrayOfDouble[0], (float)arrayOfDouble[1]);
  }
  
  private static class Offsets {}
}
