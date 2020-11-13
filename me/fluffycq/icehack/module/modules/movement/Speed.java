//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import java.util.ArrayList;
import java.util.Objects;
import me.fluffycq.icehack.events.PlayerMoveEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.modules.misc.TimerMod;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public class Speed extends Module {
  private float rotYaw() {
    float f1 = mc.player.rotationYaw;
    if (mc.player.moveForward < 0.0F)
      f1 += 180.0F; 
    float f2 = 1.0F;
    if (mc.player.moveForward < 0.0F) {
      f2 = -0.5F;
    } else if (mc.player.moveForward > 0.0F) {
      f2 = 0.5F;
    } 
    if (mc.player.moveStrafing > 0.0F)
      f1 -= 90.0F * f2; 
    if (mc.player.moveStrafing < 0.0F)
      f1 += 90.0F * f2; 
    return f1 * 0.017453292F;
  }
  
  private void handleY(PlayerMoveEvent paramPlayerMoveEvent) {
    double d = 0.40123128D;
    if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
      if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
        d += ((mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F); 
      paramPlayerMoveEvent.setY(mc.player.motionY = d);
      this.moveSpeed *= 2.149D;
    } 
  }
  
  public void onDisable() {
    this.modInfo = this.mode.getValString();
    TimerMod.SetOverrideSpeed(1.0F);
  }
  
  private double getBaseMoveSpeed() {
    double d = 0.272D;
    if (mc.player.isPotionActive(MobEffects.SPEED)) {
      int i = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
      d *= 1.0D + 0.2D * i;
    } 
    if (this.usetimer.getValBoolean())
      TimerMod.SetOverrideSpeed(1.088F); 
    return d;
  }
  
  public Speed() {
    super("Speed", 0, Category.MOVEMENT);
    this.moveEvent = new Listener(paramPlayerMoveEvent -> {
          if (mc.player.isElytraFlying())
            return; 
          if (mc.player.isRiding())
            return; 
          if (mc.player.capabilities != null && (mc.player.capabilities.isFlying || mc.player.isElytraFlying()))
            return; 
          if (!this.speedinwater.getValBoolean() && (mc.player.isInWater() || mc.player.isInLava()))
            return; 
          if (this.mode.getValString().equalsIgnoreCase("Strafe")) {
            switch (this.stage) {
              case 0:
                this.stage++;
                this.lastDist = 0.0D;
                break;
              case 2:
                handleY(paramPlayerMoveEvent);
                break;
              case 3:
                this.moveSpeed = this.lastDist - 0.76D * (this.lastDist - getBaseMoveSpeed());
                break;
              default:
                if ((mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || mc.player.collidedVertically) && this.stage > 0)
                  this.stage = (mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F) ? 0 : 1; 
                this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
                break;
            } 
            this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
            double d1 = mc.player.movementInput.moveForward;
            double d2 = mc.player.movementInput.moveStrafe;
            double d3 = mc.player.rotationYaw;
            if (d1 == 0.0D && d2 == 0.0D) {
              paramPlayerMoveEvent.setX(0.0D);
              paramPlayerMoveEvent.setZ(0.0D);
              TimerMod.SetOverrideSpeed(1.0F);
              if (this.autosprint.getValBoolean())
                mc.player.setSprinting(false); 
            } 
            if (d1 != 0.0D && d2 != 0.0D) {
              d1 *= Math.sin(0.7853981633974483D);
              d2 *= Math.cos(0.7853981633974483D);
              if (this.autosprint.getValBoolean())
                mc.player.setSprinting(true); 
            } 
            paramPlayerMoveEvent.setX((d1 * this.moveSpeed * -Math.sin(Math.toRadians(d3)) + d2 * this.moveSpeed * Math.cos(Math.toRadians(d3))) * 0.99D);
            paramPlayerMoveEvent.setZ((d1 * this.moveSpeed * Math.cos(Math.toRadians(d3)) - d2 * this.moveSpeed * -Math.sin(Math.toRadians(d3))) * 0.99D);
            paramPlayerMoveEvent.cancel();
            this.stage++;
          } else if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) {
            if (this.autosprint.getValBoolean())
              mc.player.setSprinting(true); 
            float f = rotYaw();
            mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
            mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.4D, mc.player.posZ, false));
          } 
        }new java.util.function.Predicate[0]);
    this.modes.add("Strafe");
    this.modes.add("OnGround");
    this.autosprint = new Setting("AutoSprint", this, true);
    this.usetimer = new Setting("UseTimer", this, true);
    this.speedinwater = new Setting("SpeedInWater", this, true);
    this.mode = new Setting("Speed", this, "Strafe", this.modes);
  }
  
  public void onEnable() {
    this.modInfo = this.mode.getValString();
    TimerMod.SetOverrideSpeed(1.0F);
  }
  
  public void onUpdate() {
    if (mc == null || mc.player == null)
      return; 
    if (this.mode.getValString().equalsIgnoreCase("Strafe"))
      this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ)); 
    this.modInfo = this.mode.getValString();
  }
  
  public String getModInfo() {
    return this.mode.getValString();
  }
}
