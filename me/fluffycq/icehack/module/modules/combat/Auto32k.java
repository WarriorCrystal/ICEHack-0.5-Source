//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import me.fluffycq.icehack.util.FaceUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Auto32k extends Module {
  public Block getBlock(BlockPos paramBlockPos) {
    return mc.world.getBlockState(paramBlockPos).getBlock();
  }
  
  public boolean isAir(BlockPos paramBlockPos) {
    return (getBlock(paramBlockPos) instanceof net.minecraft.block.BlockAir);
  }
  
  private static boolean hasNeighbour(BlockPos paramBlockPos) {
    for (EnumFacing enumFacing : EnumFacing.values()) {
      BlockPos blockPos = paramBlockPos.offset(enumFacing);
      if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable())
        return true; 
    } 
    return false;
  }
  
  public Auto32k() {
    super("Auto32k", 0, Category.COMBAT);
    this.modes.add("Normal");
    this.modes.add("Bypass");
    this.mode = new Setting("Mode", this, "Normal", this.modes);
    this.delay = new Setting("Delay", this, 12.0D, 3.0D, 50.0D, true);
    this.walls = new Setting("Walls", this, false);
  }
  
  public void changeItem(int paramInt) {
    mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(paramInt));
    mc.player.inventory.currentItem = paramInt;
  }
  
  public void onEnable() {
    this.finished = false;
    this.doneSlot = false;
    this.placeTick = 1;
    this.swordSlot = -1;
    this.horizontalfixed = null;
    this.placePos = null;
    this.placedHopperPos = null;
    this.hopperposthingfixed = null;
    this.hopperIndex = -1;
    this.shulkerIndex = -1;
    this.redstoneIndex = -1;
    this.dispenserIndex = -1;
    this.obiIndex = -1;
    if (Minecraft.getMinecraft() != null && (Minecraft.getMinecraft()).player != null && (Minecraft.getMinecraft()).objectMouseOver != null && (Minecraft.getMinecraft()).objectMouseOver.getBlockPos() != null) {
      this.placePos = new BlockPos((Minecraft.getMinecraft()).objectMouseOver.getBlockPos().getX(), (Minecraft.getMinecraft()).objectMouseOver.getBlockPos().getY(), (Minecraft.getMinecraft()).objectMouseOver.getBlockPos().getZ());
      this.hopperposthingfixed = this.placePos.offset(mc.player.getHorizontalFacing().getOpposite()).up();
      this.horizontalfixed = mc.player.getHorizontalFacing();
    } 
    if (mc != null && mc.player != null) {
      if (mc.player.isDead)
        return; 
      if (this.mode.getValString().equalsIgnoreCase("Normal")) {
        for (byte b = 0; b < 9; b++) {
          ItemStack itemStack = (ItemStack)(Minecraft.getMinecraft()).player.inventory.mainInventory.get(b);
          if (itemStack.getItem().equals(Item.getItemFromBlock((Block)Blocks.HOPPER)))
            this.hopperIndex = b; 
          if (itemStack.getItem() instanceof net.minecraft.item.ItemShulkerBox)
            this.shulkerIndex = b; 
        } 
        if (this.hopperIndex != -1 && this.shulkerIndex != -1) {
          if ((Minecraft.getMinecraft()).objectMouseOver != null && (Minecraft.getMinecraft()).objectMouseOver.getBlockPos() != null) {
            this.placePos = new BlockPos((Minecraft.getMinecraft()).objectMouseOver.getBlockPos().getX(), (Minecraft.getMinecraft()).objectMouseOver.getBlockPos().getY(), (Minecraft.getMinecraft()).objectMouseOver.getBlockPos().getZ());
            if (this.placePos != null && !((Minecraft.getMinecraft()).world.getBlockState(this.placePos).getBlock() instanceof net.minecraft.block.BlockAir)) {
              if (!this.walls.getValBoolean()) {
                place32k(this.hopperIndex, this.shulkerIndex, this.placePos, EnumFacing.UP, new Vec3d(this.placePos.getX(), this.placePos.getY(), this.placePos.getZ()));
              } else {
                changeItem(this.hopperIndex);
                BlockPos blockPos = mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit);
                Vec3d vec3d = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                placeRedstoneTop(blockPos);
                changeItem(this.shulkerIndex);
                placeBlock(blockPos, EnumFacing.UP, vec3d);
                mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)(Minecraft.getMinecraft()).player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.playerController.processRightClickBlock((Minecraft.getMinecraft()).player, (Minecraft.getMinecraft()).world, blockPos, EnumFacing.UP, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), EnumHand.MAIN_HAND);
                disable();
              } 
            } else {
              disable();
            } 
          } else {
            disable();
          } 
        } else {
          disable();
        } 
      } 
    } 
  }
  
  public void place32k(int paramInt1, int paramInt2, BlockPos paramBlockPos, EnumFacing paramEnumFacing, Vec3d paramVec3d) {
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)(Minecraft.getMinecraft()).player, CPacketEntityAction.Action.START_SNEAKING));
    if (isAir(paramBlockPos.up())) {
      changeItem(paramInt1);
      placeBlock(paramBlockPos, paramEnumFacing, paramVec3d);
      mc.playerController.updateController();
    } 
    FaceUtil.faceBlock(paramBlockPos.up());
    this.placedHopperPos = paramBlockPos.up();
    if (getBlock(this.placedHopperPos).equals(Blocks.HOPPER) && isAir(this.placedHopperPos.up())) {
      changeItem(paramInt2);
      placeBlock(this.placedHopperPos, EnumFacing.UP, new Vec3d(this.placedHopperPos.getX(), this.placedHopperPos.getY(), this.placedHopperPos.getZ()));
      mc.playerController.updateController();
    } 
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)(Minecraft.getMinecraft()).player, CPacketEntityAction.Action.STOP_SNEAKING));
    mc.playerController.processRightClickBlock((Minecraft.getMinecraft()).player, (Minecraft.getMinecraft()).world, this.placedHopperPos, paramEnumFacing, new Vec3d(this.placedHopperPos.getX(), this.placedHopperPos.getY(), this.placedHopperPos.getZ()), EnumHand.MAIN_HAND);
    disable();
  }
  
  public void placeBlock(BlockPos paramBlockPos, EnumFacing paramEnumFacing, Vec3d paramVec3d) {
    mc.playerController.processRightClickBlock((Minecraft.getMinecraft()).player, (Minecraft.getMinecraft()).world, paramBlockPos, paramEnumFacing, paramVec3d, EnumHand.MAIN_HAND);
    mc.player.swingArm(EnumHand.MAIN_HAND);
  }
  
  private static boolean checkForNeighbours(BlockPos paramBlockPos) {
    if (!hasNeighbour(paramBlockPos)) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
        BlockPos blockPos = paramBlockPos.offset(enumFacing);
        if (hasNeighbour(blockPos))
          return true; 
      } 
      return false;
    } 
    return true;
  }
  
  private static void placeRedstoneTop(BlockPos paramBlockPos) {
    if (!mc.world.getBlockState(paramBlockPos).getMaterial().isReplaceable())
      return; 
    if (!checkForNeighbours(paramBlockPos))
      return; 
    EnumFacing[] arrayOfEnumFacing = EnumFacing.values();
    int i = arrayOfEnumFacing.length;
    byte b = 0;
    while (b < i) {
      EnumFacing enumFacing1 = arrayOfEnumFacing[b];
      BlockPos blockPos = paramBlockPos.offset(enumFacing1);
      EnumFacing enumFacing2 = enumFacing1.getOpposite();
      if (!mc.world.getBlockState(blockPos).getBlock().canCollideCheck(mc.world.getBlockState(blockPos), false)) {
        b++;
        continue;
      } 
      Vec3d vec3d = (new Vec3d((Vec3i)blockPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
      Block block = mc.world.getBlockState(blockPos).getBlock();
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      BlockUtil.faceVectorPacketInstant(vec3d);
      mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing2, vec3d, EnumHand.MAIN_HAND);
      mc.player.swingArm(EnumHand.MAIN_HAND);
      mc.rightClickDelayTimer = 4;
      return;
    } 
  }
  
  public void onUpdate() {
    if (mc != null && mc.player != null) {
      this.placeTick++;
      if (mc.player.isDead)
        return; 
      if (this.mode.getValString().equalsIgnoreCase("Bypass")) {
        for (byte b = 0; b < 9; b++) {
          ItemStack itemStack = (ItemStack)(Minecraft.getMinecraft()).player.inventory.mainInventory.get(b);
          if (itemStack.getItem().equals(Item.getItemFromBlock((Block)Blocks.HOPPER)))
            this.hopperIndex = b; 
          if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.OBSIDIAN)))
            this.obiIndex = b; 
          if (itemStack.getItem() instanceof net.minecraft.item.ItemShulkerBox)
            this.shulkerIndex = b; 
          if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)))
            this.redstoneIndex = b; 
          if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.DISPENSER)))
            this.dispenserIndex = b; 
        } 
        if (this.hopperIndex != -1 && this.shulkerIndex != -1 && this.redstoneIndex != -1 && this.dispenserIndex != -1 && this.obiIndex != -1) {
          if ((Minecraft.getMinecraft()).objectMouseOver != null && (Minecraft.getMinecraft()).objectMouseOver.getBlockPos() != null && this.placePos != null && mc.player != null) {
            if (this.placePos != null && !((Minecraft.getMinecraft()).world.getBlockState(this.placePos).getBlock() instanceof net.minecraft.block.BlockAir) && this.placeTick == 3 && this.hopperposthingfixed != null && this.horizontalfixed != null) {
              Vec3d vec3d = new Vec3d(this.placePos.getX(), this.placePos.getY(), this.placePos.getZ());
              changeItem(this.obiIndex);
              placeBlock(this.placePos, EnumFacing.UP, vec3d);
              mc.playerController.updateController();
              changeItem(this.dispenserIndex);
              FaceUtil.faceBlock(this.placePos.up());
              placeBlock(this.placePos.up(), EnumFacing.UP, vec3d);
              mc.playerController.updateController();
              FaceUtil.faceBlock(this.placePos.up().up());
              BlockPos blockPos = this.placePos.up().up();
              mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, EnumFacing.UP, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), EnumHand.MAIN_HAND);
              mc.player.swingArm(EnumHand.MAIN_HAND);
              mc.playerController.updateController();
              changeItem(this.shulkerIndex);
              this.placeTick = 4;
            } 
            if (this.placeTick == (int)this.delay.getValDouble() + 7 && this.placePos != null) {
              mc.playerController.windowClick(mc.player.openContainer.windowId, 0, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)mc.player);
              mc.player.closeScreen();
              this.placeTick = (int)this.delay.getValDouble() + 7;
              mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)(Minecraft.getMinecraft()).player, CPacketEntityAction.Action.START_SNEAKING));
              EnumFacing enumFacing1 = null;
              EnumFacing enumFacing2 = null;
              if (this.horizontalfixed == EnumFacing.NORTH) {
                enumFacing1 = EnumFacing.WEST;
                enumFacing2 = EnumFacing.EAST;
              } else if (this.horizontalfixed == EnumFacing.EAST) {
                enumFacing1 = EnumFacing.NORTH;
                enumFacing2 = EnumFacing.SOUTH;
              } else if (this.horizontalfixed == EnumFacing.SOUTH) {
                enumFacing1 = EnumFacing.EAST;
                enumFacing2 = EnumFacing.WEST;
              } else if (this.horizontalfixed == EnumFacing.WEST) {
                enumFacing1 = EnumFacing.SOUTH;
                enumFacing2 = EnumFacing.NORTH;
              } 
              changeItem(this.redstoneIndex);
              if (!isAir(this.placePos.up().up().offset(enumFacing2.getOpposite())) && !isAir(this.placePos.up().up().offset(enumFacing1.getOpposite()))) {
                placeRedstoneTop(this.placePos.up().up().up());
              } else if (enumFacing1 != null && enumFacing2 != null) {
                BlockPos blockPos = this.placePos.up().up();
                if (isAir(blockPos.offset(enumFacing1.getOpposite())) || mc.world.getBlockState(blockPos.offset(enumFacing1.getOpposite())).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(blockPos.offset(enumFacing1.getOpposite())).getBlock().equals(Blocks.LAVA)) {
                  Vec3d vec3d = (new Vec3d((Vec3i)blockPos.offset(enumFacing1.getOpposite()))).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing1.getDirectionVec())).scale(0.5D));
                  BlockUtil.faceVectorPacketInstant(vec3d);
                  placeBlock(blockPos, enumFacing1.getOpposite(), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                } else if (isAir(blockPos.offset(enumFacing2.getOpposite())) || mc.world.getBlockState(blockPos.offset(enumFacing2.getOpposite())).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(blockPos.offset(enumFacing2.getOpposite())).getBlock().equals(Blocks.LAVA)) {
                  Vec3d vec3d = (new Vec3d((Vec3i)blockPos.offset(enumFacing2.getOpposite()))).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumFacing2.getDirectionVec())).scale(0.5D));
                  BlockUtil.faceVectorPacketInstant(vec3d);
                  placeBlock(blockPos, enumFacing2.getOpposite(), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                } 
              } 
            } 
            if (this.placePos != null && shulkerList.contains(mc.world.getBlockState(this.hopperposthingfixed.up()).getBlock())) {
              changeItem(this.hopperIndex);
              BlockPos blockPos = this.hopperposthingfixed.up();
              FaceUtil.faceBlock(this.hopperposthingfixed.down());
              placeBlock(blockPos, EnumFacing.DOWN, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
              FaceUtil.faceBlock(blockPos.down());
              mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)(Minecraft.getMinecraft()).player, CPacketEntityAction.Action.STOP_SNEAKING));
              mc.playerController.processRightClickBlock(mc.player, mc.world, this.hopperposthingfixed, EnumFacing.DOWN, new Vec3d(this.hopperposthingfixed.getX(), this.hopperposthingfixed.getY(), this.hopperposthingfixed.getZ()), EnumHand.MAIN_HAND);
              mc.player.swingArm(EnumHand.MAIN_HAND);
              disable();
            } 
          } 
        } else {
          disable();
        } 
      } 
    } 
  }
  
  private enum RedstoneMode {
    SIDE, TOP;
    
    static {
    
    }
  }
}
