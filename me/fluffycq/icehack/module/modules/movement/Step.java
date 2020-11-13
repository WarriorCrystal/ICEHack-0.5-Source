//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import java.util.ArrayList;
import me.fluffycq.icehack.events.EventPlayerUpdateMoveState;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class Step extends Module {
  public Step() {
    super("Step", 0, Category.MOVEMENT);
    this.modes.add("Normal");
    this.modes.add("AAC");
    this.mode = new Setting("Mode", this, "AAC", this.modes);
    this.height = new Setting("Height", this, 2.0D, 1.0D, 2.0D, true);
    this.entitystep = new Setting("EntityStep", this, true);
  }
  
  public void onUpdate() {
    if (mc == null || mc.player == null || mc.world == null)
      return; 
    setModInfo(this.mode.getValString());
    this.modInfo = this.mode.getValString();
    if (this.mode.getValString().equalsIgnoreCase("Normal")) {
      AxisAlignedBB axisAlignedBB = mc.player.getEntityBoundingBox().offset(0.0D, 0.05D, 0.0D).grow(0.05D);
      if (!mc.world.getCollisionBoxes((Entity)mc.player, axisAlignedBB.offset(0.0D, 1.0D, 0.0D)).isEmpty())
        return; 
      double d1 = -1.0D;
      for (AxisAlignedBB axisAlignedBB1 : mc.world.getCollisionBoxes((Entity)mc.player, axisAlignedBB)) {
        if (axisAlignedBB1.maxY > d1)
          d1 = axisAlignedBB1.maxY; 
      } 
      double d2 = d1 - mc.player.posY;
      if (d2 < 0.0D || d2 > 1.0D)
        return; 
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D * d2, mc.player.posZ, mc.player.onGround));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.753D * d2, mc.player.posZ, mc.player.onGround));
      mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D * d2, mc.player.posZ);
    } else if (this.mode.getValString().equalsIgnoreCase("AAC")) {
      double d1 = -1.0D;
      AxisAlignedBB axisAlignedBB = mc.player.getEntityBoundingBox().offset(0.0D, 0.05D, 0.0D).grow(0.05D);
      if (!mc.world.getCollisionBoxes((Entity)mc.player, axisAlignedBB.offset(0.0D, 2.0D, 0.0D)).isEmpty())
        return; 
      for (AxisAlignedBB axisAlignedBB1 : mc.world.getCollisionBoxes((Entity)mc.player, axisAlignedBB)) {
        if (axisAlignedBB1.maxY > d1)
          d1 = axisAlignedBB1.maxY; 
      } 
      double d2 = d1 - mc.player.posY;
      if (d2 < 0.0D || d2 > 2.0D)
        return; 
      if (d2 == 2.0D && this.height.getValDouble() >= 2.0D) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.78D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.63D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.51D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.9D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.21D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.45D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.43D, mc.player.posZ, mc.player.onGround));
        mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
      } 
      if (d2 == 1.5D && this.height.getValDouble() >= 1.5D) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805212D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.24918707874468D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1707870772188D, mc.player.posZ, mc.player.onGround));
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
      } 
      if (d2 == 1.0D) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805212D, mc.player.posZ, mc.player.onGround));
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
      } 
    } 
  }
  
  public String getModInfo() {
    return this.mode.getValString();
  }
}
