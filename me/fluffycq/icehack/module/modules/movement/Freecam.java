//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import java.util.ArrayList;
import me.fluffycq.icehack.events.EventSetOpaqueCube;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.PlayerMoveEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.MathUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class Freecam extends Module {
  public void onDisable() {
    super.onDisable();
    if (mc.world != null && this.mode.getValString().equalsIgnoreCase("Normal")) {
      if (this.riding != null) {
        mc.player.startRiding(this.riding, true);
        this.riding = null;
      } 
      if (this.Camera != null)
        mc.world.removeEntity((Entity)this.Camera); 
      if (this.position != null)
        mc.player.setPosition(this.position.x, this.position.y, this.position.z); 
      mc.player.rotationYaw = this.yaw;
      mc.player.rotationPitch = this.pitch;
      mc.player.noClip = false;
      mc.player.setVelocity(0.0D, 0.0D, 0.0D);
    } else if (this.mode.getValString().equalsIgnoreCase("Camera")) {
      if (this.Camera != null)
        mc.world.removeEntity((Entity)this.Camera); 
      mc.setRenderViewEntity((Entity)mc.player);
    } 
  }
  
  public Freecam() {
    super("Freecam", 0, Category.MOVEMENT);
    this.OnEventSetOpaqueCube = new Listener(paramEventSetOpaqueCube -> paramEventSetOpaqueCube.cancel(), new java.util.function.Predicate[0]);
    this.OnWorldEvent = new Listener(paramEntityJoinWorldEvent -> {
          if (paramEntityJoinWorldEvent.getEntity() == mc.player)
            setState(!getState()); 
        }new java.util.function.Predicate[0]);
    this.PacketEvent = new Listener(paramSend -> {
          if (this.mode.getValString().equalsIgnoreCase("Normal")) {
            if (!this.cancelPackets.getValBoolean())
              return; 
            if (paramSend.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer || paramSend.getPacket() instanceof net.minecraft.network.play.client.CPacketInput)
              paramSend.cancel(); 
          } 
        }new java.util.function.Predicate[0]);
    this.modes.add("Normal");
    this.modes.add("Camera");
    this.mode = new Setting("Mode", this, "Normal", this.modes);
    this.speed = new Setting("Speed", this, 1.0D, 0.1D, 5.0D, false);
    this.cancelPackets = new Setting("PacketCancel", this, true);
  }
  
  public void onEnable() {
    super.onEnable();
    if (mc.world == null)
      return; 
    if (this.mode.getValString().equalsIgnoreCase("Normal")) {
      this.riding = null;
      if (mc.player.getRidingEntity() != null) {
        this.riding = mc.player.getRidingEntity();
        mc.player.dismountRidingEntity();
      } 
      this.Camera = new EntityOtherPlayerMP((World)mc.world, mc.getSession().getProfile());
      this.Camera.copyLocationAndAnglesFrom((Entity)mc.player);
      this.Camera.prevRotationYaw = mc.player.rotationYaw;
      this.Camera.rotationYawHead = mc.player.rotationYawHead;
      this.Camera.inventory.copyInventory(mc.player.inventory);
      mc.world.addEntityToWorld(-69, (Entity)this.Camera);
      this.position = mc.player.getPositionVector();
      this.yaw = mc.player.rotationYaw;
      this.pitch = mc.player.rotationPitch;
      mc.player.noClip = true;
    } else {
      this.Camera = new EntityOtherPlayerMP((World)mc.world, mc.getSession().getProfile());
      this.Camera.copyLocationAndAnglesFrom((Entity)mc.player);
      this.Camera.prevRotationYaw = mc.player.rotationYaw;
      this.Camera.rotationYawHead = mc.player.rotationYawHead;
      this.Camera.inventory.copyInventory(mc.player.inventory);
      this.Camera.noClip = true;
      mc.world.addEntityToWorld(-69, (Entity)this.Camera);
      mc.setRenderViewEntity((Entity)this.Camera);
    } 
  }
  
  public void onUpdate() {
    if (this.mode.getValString().equalsIgnoreCase("Normal")) {
      mc.player.noClip = true;
      mc.player.setVelocity(0.0D, 0.0D, 0.0D);
      double[] arrayOfDouble = MathUtil.directionSpeed(this.speed.getValDouble());
      if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
        mc.player.motionX = arrayOfDouble[0];
        mc.player.motionZ = arrayOfDouble[1];
      } else {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
      mc.player.setSprinting(false);
      if (mc.gameSettings.keyBindJump.isKeyDown())
        mc.player.motionY += 0.8D; 
      if (mc.gameSettings.keyBindSneak.isKeyDown())
        mc.player.motionY -= 0.8D; 
    } 
  }
}
