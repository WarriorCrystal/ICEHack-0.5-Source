//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockUtil {
  public BlockPos getClosestTargetPos(Entity paramEntity) {
    return (paramEntity != null) ? new BlockPos(Math.floor(paramEntity.posX), Math.floor(paramEntity.posY), Math.floor(paramEntity.posZ)) : null;
  }
  
  private static Block getBlock(BlockPos paramBlockPos) {
    return getState(paramBlockPos).getBlock();
  }
  
  private static Vec3d getEyesPos() {
    return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
  }
  
  public static boolean hasNeighbour(BlockPos paramBlockPos) {
    for (EnumFacing enumFacing : EnumFacing.values()) {
      BlockPos blockPos = paramBlockPos.offset(enumFacing);
      if (!(Minecraft.getMinecraft()).world.getBlockState(blockPos).getMaterial().isReplaceable())
        return true; 
    } 
    return false;
  }
  
  static {
    shulkerList = Arrays.asList(new Block[] { 
          Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, 
          Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX });
    mc = Minecraft.getMinecraft();
    TRAP = new Vec3d[] { 
        new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), 
        new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    NOROOF = new Vec3d[] { 
        new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), 
        new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(-1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 4.0D, -1.0D), new Vec3d(1.0D, 4.0D, 0.0D), new Vec3d(0.0D, 4.0D, 1.0D), new Vec3d(-1.0D, 4.0D, 0.0D) };
    YTHREE = new Vec3d[] { new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(-1.0D, 3.0D, 0.0D) };
    BLOCKOVERHEADFACINGPOSX = new Vec3d[] { new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    BLOCKOVERHEADFACINGPOSZ = new Vec3d[] { new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    BLOCKOVERHEADFACINGNEGX = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(-1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    BLOCKOVERHEADFACINGNEGZ = new Vec3d[] { new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
  }
  
  public static List<BlockPos> getSphere(BlockPos paramBlockPos, float paramFloat, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2) {
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
  
  private static void processRightClickBlock(BlockPos paramBlockPos, EnumFacing paramEnumFacing, Vec3d paramVec3d) {
    mc.playerController.processRightClickBlock(mc.player, mc.world, paramBlockPos, paramEnumFacing, paramVec3d, EnumHand.MAIN_HAND);
  }
  
  public static ValidResult valid(BlockPos paramBlockPos) {
    if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(paramBlockPos)))
      return ValidResult.NoEntityCollision; 
    if (!checkForNeighbours(paramBlockPos))
      return ValidResult.NoNeighbors; 
    IBlockState iBlockState = mc.world.getBlockState(paramBlockPos);
    if (iBlockState.getBlock() == Blocks.AIR) {
      BlockPos[] arrayOfBlockPos = { paramBlockPos.north(), paramBlockPos.south(), paramBlockPos.east(), paramBlockPos.west(), paramBlockPos.up(), paramBlockPos.down() };
      for (BlockPos blockPos : arrayOfBlockPos) {
        IBlockState iBlockState1 = mc.world.getBlockState(blockPos);
        if (iBlockState1.getBlock() != Blocks.AIR)
          for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos blockPos1 = paramBlockPos.offset(enumFacing);
            boolean bool = (mc.world.getBlockState(blockPos1).getBlock() == Blocks.WATER) ? true : false;
            if (mc.world.getBlockState(blockPos1).getBlock().canCollideCheck(mc.world.getBlockState(blockPos1), false))
              return ValidResult.Ok; 
          }  
      } 
      return ValidResult.NoNeighbors;
    } 
    return ValidResult.AlreadyBlockThere;
  }
  
  public static boolean placeBlockInRange(BlockPos paramBlockPos, int paramInt, Module paramModule, boolean paramBoolean) {
    Block block1 = mc.world.getBlockState(paramBlockPos).getBlock();
    if (!(block1 instanceof net.minecraft.block.BlockAir) && !(block1 instanceof net.minecraft.block.BlockLiquid))
      return false; 
    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(paramBlockPos))) {
      if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb))
        return false; 
    } 
    EnumFacing enumFacing1 = getPlaceableSide(paramBlockPos);
    if (enumFacing1 == null)
      return false; 
    BlockPos blockPos = paramBlockPos.offset(enumFacing1);
    EnumFacing enumFacing2 = enumFacing1.getOpposite();
    if (!canBeClicked(blockPos))
      return false; 
    Vec3d vec3d = (new Vec3d((Vec3i)blockPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
    Block block2 = mc.world.getBlockState(blockPos).getBlock();
    int i = findObiInHotbar();
    if (i == -1)
      paramModule.disable(); 
    if (paramInt != i) {
      mc.player.inventory.currentItem = i;
      paramInt = i;
    } 
    if ((!paramBoolean && blackList.contains(block2)) || shulkerList.contains(block2)) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      paramBoolean = true;
    } 
    faceVectorPacketInstant(vec3d);
    mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing2, vec3d, EnumHand.MAIN_HAND);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 0;
    return true;
  }
  
  public int getViewYaw() {
    return (int)Math.abs(Math.floor(((Minecraft.getMinecraft()).player.rotationYaw * 8.0F / 360.0F)));
  }
  
  public static boolean checkForNeighbours(BlockPos paramBlockPos) {
    if (!hasNeighbour(paramBlockPos)) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
        BlockPos blockPos = paramBlockPos.offset(enumFacing);
        if (hasNeighbour(blockPos))
          return true; 
        if (enumFacing == EnumFacing.UP && mc.world.getBlockState(paramBlockPos).getBlock() == Blocks.WATER && mc.world.getBlockState(paramBlockPos.up()).getBlock() == Blocks.AIR)
          return true; 
      } 
      return false;
    } 
    return true;
  }
  
  public static void findClosestTarget() {
    List list = mc.world.playerEntities;
    EntityPlayer entityPlayer = null;
    for (EntityPlayer entityPlayer1 : list) {
      if (entityPlayer1 == mc.player || Friends.isFriend(entityPlayer1.getName()) || !(entityPlayer1 instanceof net.minecraft.entity.EntityLivingBase) || entityPlayer1.getHealth() <= 0.0F)
        continue; 
      if (entityPlayer == null) {
        entityPlayer = entityPlayer1;
        continue;
      } 
      if (mc.player.getDistance((Entity)entityPlayer1) < mc.player.getDistance((Entity)entityPlayer))
        entityPlayer = entityPlayer1; 
    } 
  }
  
  public static int findObiInHotbar() {
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
  
  public static BlockPos getPlayerPos() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
  
  private static IBlockState getState(BlockPos paramBlockPos) {
    return mc.world.getBlockState(paramBlockPos);
  }
  
  public boolean isApplicable(BlockPos paramBlockPos, float paramFloat) {
    NonNullList nonNullList = NonNullList.create();
    nonNullList.addAll((Collection)getSphere(getPlayerPos(), paramFloat, (int)paramFloat, false, true, 0).stream().collect(Collectors.toList()));
    return nonNullList.contains(paramBlockPos);
  }
  
  public static EnumFacing getPlaceableSide(BlockPos paramBlockPos) {
    for (EnumFacing enumFacing : EnumFacing.values()) {
      BlockPos blockPos = paramBlockPos.offset(enumFacing);
      if (mc.world.getBlockState(blockPos).getBlock().canCollideCheck(mc.world.getBlockState(blockPos), false)) {
        IBlockState iBlockState = mc.world.getBlockState(blockPos);
        if (!iBlockState.getMaterial().isReplaceable())
          return enumFacing; 
      } 
    } 
    return null;
  }
  
  public static void placeBlockScaffold(BlockPos paramBlockPos) {
    Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    for (EnumFacing enumFacing1 : EnumFacing.values()) {
      BlockPos blockPos = paramBlockPos.offset(enumFacing1);
      EnumFacing enumFacing2 = enumFacing1.getOpposite();
      if (canBeClicked(blockPos)) {
        Vec3d vec3d1 = (new Vec3d((Vec3i)blockPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
        if (vec3d.squareDistanceTo(vec3d1) <= 18.0625D) {
          faceVectorPacketInstant(vec3d1);
          processRightClickBlock(blockPos, enumFacing2, vec3d1);
          mc.player.swingArm(EnumHand.MAIN_HAND);
          mc.rightClickDelayTimer = 4;
          return;
        } 
      } 
    } 
  }
  
  public static void faceVectorPacketInstant(Vec3d paramVec3d) {
    float[] arrayOfFloat = getLegitRotations(paramVec3d);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(arrayOfFloat[0], arrayOfFloat[1], mc.player.onGround));
  }
  
  public static PlaceResult place(BlockPos paramBlockPos, float paramFloat, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    IBlockState iBlockState = mc.world.getBlockState(paramBlockPos);
    boolean bool1 = iBlockState.getMaterial().isReplaceable();
    boolean bool2 = iBlockState.getBlock() instanceof net.minecraft.block.BlockSlab;
    if (!bool1 && !bool2)
      return PlaceResult.NotReplaceable; 
    if (!checkForNeighbours(paramBlockPos))
      return PlaceResult.Neighbors; 
    if (!bool2) {
      ValidResult validResult = valid(paramBlockPos);
      if (validResult != ValidResult.Ok && !bool1)
        return PlaceResult.CantPlace; 
    } 
    if (paramBoolean2 && bool2 && !iBlockState.isFullCube())
      return PlaceResult.CantPlace; 
    Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    for (EnumFacing enumFacing1 : EnumFacing.values()) {
      BlockPos blockPos = paramBlockPos.offset(enumFacing1);
      EnumFacing enumFacing2 = enumFacing1.getOpposite();
      if (mc.world.getBlockState(blockPos).getBlock().canCollideCheck(mc.world.getBlockState(blockPos), false)) {
        Vec3d vec3d1 = (new Vec3d((Vec3i)blockPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
        if (vec3d.distanceTo(vec3d1) <= paramFloat) {
          Block block = mc.world.getBlockState(blockPos).getBlock();
          boolean bool = block.onBlockActivated((World)mc.world, paramBlockPos, mc.world.getBlockState(paramBlockPos), (EntityPlayer)mc.player, EnumHand.MAIN_HAND, enumFacing1, 0.0F, 0.0F, 0.0F);
          if (blackList.contains(block) || shulkerList.contains(block) || bool)
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING)); 
          if (paramBoolean1)
            faceVectorPacketInstant(vec3d1); 
          EnumActionResult enumActionResult = mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing2, vec3d1, EnumHand.MAIN_HAND);
          if (enumActionResult != EnumActionResult.FAIL) {
            if (paramBoolean3) {
              mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            } else {
              mc.player.swingArm(EnumHand.MAIN_HAND);
            } 
            if (bool)
              mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
            return PlaceResult.Placed;
          } 
        } 
      } 
    } 
    return PlaceResult.CantPlace;
  }
  
  public static float[] getLegitRotations(Vec3d paramVec3d) {
    Vec3d vec3d = getEyesPos();
    double d1 = paramVec3d.x - vec3d.x;
    double d2 = paramVec3d.y - vec3d.y;
    double d3 = paramVec3d.z - vec3d.z;
    double d4 = Math.sqrt(d1 * d1 + d3 * d3);
    float f1 = (float)Math.toDegrees(Math.atan2(d3, d1)) - 90.0F;
    float f2 = (float)-Math.toDegrees(Math.atan2(d2, d4));
    return new float[] { mc.player.rotationYaw + MathHelper.wrapDegrees(f1 - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(f2 - mc.player.rotationPitch) };
  }
  
  public static boolean canBeClicked(BlockPos paramBlockPos) {
    return getBlock(paramBlockPos).canCollideCheck(getState(paramBlockPos), false);
  }
  
  public static PlaceResult place(BlockPos paramBlockPos, float paramFloat, boolean paramBoolean1, boolean paramBoolean2) {
    return place(paramBlockPos, paramFloat, paramBoolean1, paramBoolean2, false);
  }
  
  public enum PlaceResult {
    NotReplaceable, CantPlace, Placed, Neighbors;
    
    static {
    
    }
  }
  
  public enum ValidResult {
    NoEntityCollision, NoNeighbors, AlreadyBlockThere, Ok;
    
    static {
      $VALUES = new ValidResult[] { NoEntityCollision, AlreadyBlockThere, NoNeighbors, Ok };
    }
  }
}
