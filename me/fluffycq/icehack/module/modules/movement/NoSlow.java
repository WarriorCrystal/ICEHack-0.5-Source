//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.BlockSoulSandSlowdownEvent;
import me.fluffycq.icehack.events.BlockWebSlowdownEvent;
import me.fluffycq.icehack.events.EventPlayerUpdateMoveState;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.PlayerMoveEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
  public static BlockPos localPlayerPosFloored() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
  
  public NoSlow() {
    super("NoSlow", 0, Category.MOVEMENT);
    this.soulSandEvent = new Listener(paramBlockSoulSandSlowdownEvent -> {
          if (this.soulsand.getValBoolean())
            paramBlockSoulSandSlowdownEvent.cancel(); 
        }new java.util.function.Predicate[0]);
    this.webEvent = new Listener(paramBlockWebSlowdownEvent -> {
        
        }new java.util.function.Predicate[0]);
    this.OnIsKeyPressed = new Listener(paramEventPlayerUpdateMoveState -> {
          if (this.invmove.getValBoolean() && mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat)) {
            if (mc.currentScreen != ICEHack.clickgui) {
              if (Keyboard.isKeyDown(200))
                mc.player.rotationPitch -= 4.0F; 
              if (Keyboard.isKeyDown(208))
                mc.player.rotationPitch += 4.0F; 
              if (Keyboard.isKeyDown(203))
                mc.player.rotationYaw -= 5.0F; 
              if (Keyboard.isKeyDown(205))
                mc.player.rotationYaw += 5.0F; 
              if (mc.player.rotationPitch > 90.0F)
                mc.player.rotationPitch = 90.0F; 
              if (mc.player.rotationPitch < -90.0F)
                mc.player.rotationPitch = -90.0F; 
            } 
            mc.player.movementInput.moveStrafe = 0.0F;
            mc.player.movementInput.moveForward = 0.0F;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
              mc.player.movementInput.moveForward++;
              mc.player.movementInput.forwardKeyDown = true;
            } else {
              mc.player.movementInput.forwardKeyDown = false;
            } 
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
              mc.player.movementInput.moveForward--;
              mc.player.movementInput.backKeyDown = true;
            } else {
              mc.player.movementInput.backKeyDown = false;
            } 
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
              mc.player.movementInput.moveStrafe++;
              mc.player.movementInput.leftKeyDown = true;
            } else {
              mc.player.movementInput.leftKeyDown = false;
            } 
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
              mc.player.movementInput.moveStrafe--;
              mc.player.movementInput.rightKeyDown = true;
            } else {
              mc.player.movementInput.rightKeyDown = false;
            } 
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
            mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
          } 
        }new java.util.function.Predicate[0]);
    this.moveEvent = new Listener(paramPlayerMoveEvent -> {
          if (mc.player.isInWeb && this.webs.getValBoolean() && mc.player.onGround) {
            paramPlayerMoveEvent.setX(paramPlayerMoveEvent.x * this.webspeed.getValDouble());
            paramPlayerMoveEvent.setZ(paramPlayerMoveEvent.z * this.webspeed.getValDouble());
            if (paramPlayerMoveEvent.x != 0.0D && paramPlayerMoveEvent.z != 0.0D) {
              mc.player.motionX = paramPlayerMoveEvent.x * this.webspeed.getValDouble();
              mc.player.motionZ = paramPlayerMoveEvent.x * this.webspeed.getValDouble();
            } 
            paramPlayerMoveEvent.cancel();
          } 
        }new java.util.function.Predicate[0]);
    this.OnUpdateMoveState = new Listener(paramEventPlayerUpdateMoveState -> {
          if (this.items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward /= 0.2F;
            mc.player.movementInput.moveStrafe /= 0.2F;
          } 
        }new java.util.function.Predicate[0]);
    this.PacketEvent = new Listener(paramSend -> {
          if (paramSend.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer && this.strict.getValBoolean() && this.items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding())
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, localPlayerPosFloored(), EnumFacing.DOWN)); 
        }new java.util.function.Predicate[0]);
    this.invmove = new Setting("InvMove", this, true);
    this.items = new Setting("Items", this, true);
    this.soulsand = new Setting("Soulsand", this, true);
    this.webs = new Setting("Webs", this, false);
    this.webspeed = new Setting("WebSpeed", this, 4.0D, 1.0D, 10.0D, true);
    this.pull = new Setting("Pull", this, true);
    this.webpull = new Setting("WebPull", this, 4.0D, 1.0D, 10.0D, true);
    this.autopull = new Setting("AutoPull", this, true);
    this.strict = new Setting("Strict", this, false);
  }
  
  public void onUpdate() {
    if (mc.player == null || mc.gameSettings == null)
      return; 
    if (mc.player.isInWeb && this.pull.getValBoolean() && mc.player.motionY < 0.0D)
      if (this.autopull.getValBoolean()) {
        mc.player.motionY *= this.webpull.getValDouble();
      } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
        mc.player.motionY *= this.webpull.getValDouble();
      }  
    if (mc.player.isHandActive() && mc.player.getHeldItem(mc.player.getActiveHand()).getItem() instanceof net.minecraft.item.ItemShield && (mc.player.movementInput.moveStrafe != 0.0F || (mc.player.movementInput.moveForward != 0.0F && mc.player.getItemInUseMaxCount() >= 8)))
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing())); 
  }
}
