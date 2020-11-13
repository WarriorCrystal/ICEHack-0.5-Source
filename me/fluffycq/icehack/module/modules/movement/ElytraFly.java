//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import java.util.ArrayList;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventPlayerTravel;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.MathUtil;
import me.fluffycq.icehack.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.MathHelper;

public class ElytraFly extends Module {
  private void HandleControlMode(EventPlayerTravel paramEventPlayerTravel) {
    double[] arrayOfDouble = MathUtil.directionSpeed(this.speed.getValDouble());
    if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
      mc.player.motionX = arrayOfDouble[0];
      mc.player.motionZ = arrayOfDouble[1];
      mc.player.motionX -= mc.player.motionX * (Math.abs(mc.player.rotationPitch) + 90.0F) / 90.0D - mc.player.motionX;
      mc.player.motionZ -= mc.player.motionZ * (Math.abs(mc.player.rotationPitch) + 90.0F) / 90.0D - mc.player.motionZ;
    } else {
      mc.player.motionX = 0.0D;
      mc.player.motionZ = 0.0D;
    } 
    mc.player.motionY = -MathUtil.degToRad(mc.player.rotationPitch) * mc.player.movementInput.moveForward;
    mc.player.prevLimbSwingAmount = 0.0F;
    mc.player.limbSwingAmount = 0.0F;
    mc.player.limbSwing = 0.0F;
    paramEventPlayerTravel.cancel();
  }
  
  public void HandleImmediateModeElytra(EventPlayerTravel paramEventPlayerTravel) {
    paramEventPlayerTravel.cancel();
    boolean bool1 = mc.gameSettings.keyBindForward.isKeyDown();
    boolean bool2 = mc.gameSettings.keyBindBack.isKeyDown();
    boolean bool3 = mc.gameSettings.keyBindLeft.isKeyDown();
    boolean bool4 = mc.gameSettings.keyBindRight.isKeyDown();
    boolean bool5 = mc.gameSettings.keyBindJump.isKeyDown();
    boolean bool6 = mc.gameSettings.keyBindSneak.isKeyDown();
    float f1 = bool1 ? 1.0F : (bool2 ? -1 : false);
    float f2 = mc.player.rotationYaw;
    if (bool3 && (bool1 || bool2)) {
      f2 -= 40.0F * f1;
    } else if (bool4 && (bool1 || bool2)) {
      f2 += 40.0F * f1;
    } else if (bool3) {
      f2 -= 90.0F;
    } else if (bool4) {
      f2 += 90.0F;
    } 
    if (bool2)
      f2 -= 180.0F; 
    float f3 = (float)Math.toRadians(f2);
    double d = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    if (bool5 || bool1 || bool2 || bool3 || bool4) {
      if (bool5 && d > 1.0D) {
        if (mc.player.motionX == 0.0D && mc.player.motionZ == 0.0D) {
          mc.player.motionY = this.UpSpeed.getValDouble();
        } else {
          double d1 = d * 0.008D;
          mc.player.motionY += d1 * 3.2D;
          mc.player.motionX -= -MathHelper.sin(f3) * d1 / 1.0D;
          mc.player.motionZ -= MathHelper.cos(f3) * d1 / 1.0D;
          mc.player.motionX *= 0.9900000095367432D;
          mc.player.motionY *= 0.9800000190734863D;
          mc.player.motionZ *= 0.9900000095367432D;
        } 
      } else {
        mc.player.motionX = -MathHelper.sin(f3) * 1.7999999523162842D;
        mc.player.motionY = -(this.GlideSpeed.getValDouble() / 10000.0D);
        mc.player.motionZ = MathHelper.cos(f3) * 1.7999999523162842D;
      } 
    } else {
      mc.player.motionX = 0.0D;
      mc.player.motionY = 0.0D;
      mc.player.motionZ = 0.0D;
    } 
    if (bool6)
      mc.player.motionY = -this.DownSpeed.getValDouble(); 
    if (bool5 || bool6);
  }
  
  public void onDisable() {
    if (mc.player == null)
      return; 
    if (this.ElytraSlot != -1) {
      boolean bool = (!mc.player.inventory.getStackInSlot(this.ElytraSlot).isEmpty() || mc.player.inventory.getStackInSlot(this.ElytraSlot).getItem() != Items.AIR) ? true : false;
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      if (bool)
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, (EntityPlayer)mc.player); 
    } 
  }
  
  public void HandleNormalModeElytra(EventPlayerTravel paramEventPlayerTravel) {
    double d = mc.player.posY;
    if (d <= this.CancelAtHeight.getValDouble()) {
      if (!this.SendMessage) {
        Messages.sendChatMessage("&4WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
        this.SendMessage = true;
      } 
      return;
    } 
    boolean bool1 = (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) ? true : false;
    boolean bool2 = (!mc.player.isInWater() && !mc.player.isInLava() && this.CancelInWater.getValBoolean()) ? true : false;
    if (mc.gameSettings.keyBindJump.isKeyDown()) {
      paramEventPlayerTravel.cancel();
      Accelerate();
      return;
    } 
    if (!bool1) {
      this.AccelerationTimer.resetTimeSkipTo((int)-this.vAccelerationTimer.getValDouble());
    } else if ((mc.player.rotationPitch <= this.RotationPitch.getValDouble() || this.mode.getValString().equalsIgnoreCase("Tarzan")) && bool2) {
      if (this.Accelerate.getValBoolean() && this.AccelerationTimer.passed(this.vAccelerationTimer.getValDouble())) {
        Accelerate();
        return;
      } 
      return;
    } 
    paramEventPlayerTravel.cancel();
    Accelerate();
  }
  
  public void Accelerate() {
    if (this.AccelerationResetTimer.passed(this.vAccelerationTimer.getValDouble())) {
      this.AccelerationResetTimer.reset();
      this.AccelerationTimer.reset();
      this.SendMessage = false;
    } 
    float f = (float)this.speed.getValDouble();
    double[] arrayOfDouble = MathUtil.directionSpeed(f);
    mc.player.motionY = -(this.GlideSpeed.getValDouble() / 10000.0D);
    if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
      mc.player.motionX = arrayOfDouble[0];
      mc.player.motionZ = arrayOfDouble[1];
    } else {
      mc.player.motionX = 0.0D;
      mc.player.motionZ = 0.0D;
    } 
    if (mc.gameSettings.keyBindSneak.isKeyDown())
      mc.player.motionY = -this.DownSpeed.getValDouble(); 
    mc.player.prevLimbSwingAmount = 0.0F;
    mc.player.limbSwingAmount = 0.0F;
    mc.player.limbSwing = 0.0F;
  }
  
  public ElytraFly() {
    super("ElytraFly", 0, Category.MOVEMENT);
    this.OnTravel = new Listener(paramEventPlayerTravel -> {
          if (mc.player == null)
            return; 
          this.modInfo = this.mode.getValString().toUpperCase();
          if (this.debugmsg.getValBoolean())
            Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("Current Elytra Mode: ").append(this.mode.getValString()))); 
          if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA)
            return; 
          if (ICEHack.fevents.moduleManager.getModule("Freecam").isEnabled())
            return; 
          if (!mc.player.isElytraFlying()) {
            if (!mc.player.onGround && this.InstantFly.getValBoolean()) {
              if (!this.InstantFlyTimer.passed(1000.0D))
                return; 
              this.InstantFlyTimer.reset();
              mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            } 
            return;
          } 
          if (this.mode.getValString().equalsIgnoreCase("Packet")) {
            if (this.debugmsg.getValBoolean())
              Messages.sendChatMessage("Attempting to fly using mode Packet."); 
            HandleNormalModeElytra(paramEventPlayerTravel);
          } else if (this.mode.getValString().equalsIgnoreCase("Superior")) {
            if (this.debugmsg.getValBoolean())
              Messages.sendChatMessage("Attempting to fly using mode Superior."); 
            HandleImmediateModeElytra(paramEventPlayerTravel);
          } else if (this.mode.getValString().equalsIgnoreCase("Control")) {
            if (this.debugmsg.getValBoolean())
              Messages.sendChatMessage("Attempting to fly using mode Control."); 
            HandleControlMode(paramEventPlayerTravel);
          } 
        }new java.util.function.Predicate[0]);
    this.elytraMode.add("Normal");
    this.elytraMode.add("Tarzan");
    this.elytraMode.add("Superior");
    this.elytraMode.add("Packet");
    this.elytraMode.add("Control");
    this.mode = new Setting("Mode", this, "Superior", this.elytraMode);
    this.speed = new Setting("Speed", this, 1.82D, 0.1D, 5.0D, false);
    this.DownSpeed = new Setting("DownSpeed", this, 1.82D, 0.1D, 5.0D, false);
    this.GlideSpeed = new Setting("GlideSpeed", this, 1.0D, 0.0D, 5.0D, false);
    this.UpSpeed = new Setting("UpSpeed", this, 2.0D, 0.0D, 5.0D, false);
    this.Accelerate = new Setting("Accelerate", this, true);
    this.vAccelerationTimer = new Setting("Timer", this, 1000.0D, 0.0D, 10000.0D, true);
    this.RotationPitch = new Setting("Pitch", this, 0.0D, 0.0D, 90.0D, false);
    this.CancelInWater = new Setting("WaterCancel", this, true);
    this.CancelAtHeight = new Setting("HeightCancel", this, 5.0D, 0.0D, 10.0D, true);
    this.InstantFly = new Setting("InstantFly", this, true);
    this.EquipElytra = new Setting("EquipElytra", this, true);
    this.debugmsg = new Setting("DebugMsgs", this, false);
  }
  
  public String getModInfo() {
    return this.mode.getValString();
  }
  
  public void onEnable() {
    this.ElytraSlot = -1;
    if (this.EquipElytra.getValBoolean() && mc.player != null && mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
      byte b = 0;
      while (b < 44) {
        ItemStack itemStack = mc.player.inventory.getStackInSlot(b);
        if (itemStack.isEmpty() || itemStack.getItem() != Items.ELYTRA) {
          b++;
          continue;
        } 
        ItemElytra itemElytra = (ItemElytra)itemStack.getItem();
        this.ElytraSlot = b;
      } 
      if (this.ElytraSlot != -1) {
        b = (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR) ? 1 : 0;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        if (b != 0)
          mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player); 
      } 
    } 
  }
}
