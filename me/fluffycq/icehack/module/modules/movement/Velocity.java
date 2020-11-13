//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.events.EventPlayerApplyCollision;
import me.fluffycq.icehack.events.EventPlayerPushedByWater;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.PushOutBlockEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.world.World;

public class Velocity extends Module {
  public Velocity() {
    super("Velocity", 0, Category.MOVEMENT);
    this.PushOutOfBlocks = new Listener(paramPushOutBlockEvent -> {
          if (!this.nopush.getValBoolean())
            return; 
          paramPushOutBlockEvent.cancel();
        }new java.util.function.Predicate[0]);
    this.PushByWater = new Listener(paramEventPlayerPushedByWater -> {
          if (!this.nopush.getValBoolean())
            return; 
          paramEventPlayerPushedByWater.cancel();
        }new java.util.function.Predicate[0]);
    this.ApplyCollision = new Listener(paramEventPlayerApplyCollision -> {
          if (!this.nopush.getValBoolean())
            return; 
          paramEventPlayerApplyCollision.cancel();
        }new java.util.function.Predicate[0]);
    this.PacketEvent = new Listener(paramReceive -> {
          if (mc.player == null)
            return; 
          if (paramReceive.getPacket() instanceof SPacketEntityStatus && this.fishhooks.getValBoolean()) {
            SPacketEntityStatus sPacketEntityStatus = (SPacketEntityStatus)paramReceive.getPacket();
            if (sPacketEntityStatus.getOpCode() == 31) {
              Entity entity = sPacketEntityStatus.getEntity((World)(Minecraft.getMinecraft()).world);
              if (entity != null && entity instanceof EntityFishHook) {
                EntityFishHook entityFishHook = (EntityFishHook)entity;
                if (entityFishHook.caughtEntity == (Minecraft.getMinecraft()).player)
                  paramReceive.cancel(); 
              } 
            } 
          } 
          if (paramReceive.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity sPacketEntityVelocity = (SPacketEntityVelocity)paramReceive.getPacket();
            if (sPacketEntityVelocity.getEntityID() == mc.player.getEntityId())
              paramReceive.cancel(); 
          } 
          if (paramReceive.getPacket() instanceof net.minecraft.network.play.server.SPacketExplosion && this.explosions.getValBoolean())
            paramReceive.cancel(); 
        }new java.util.function.Predicate[0]);
    this.explosions = new Setting("Explosions", this, true);
    this.fishhooks = new Setting("FishHooks", this, true);
    this.nopush = new Setting("NoPush", this, true);
  }
  
  public void onUpdate() {
    this.modInfo = "Normal";
  }
  
  public String getModInfo() {
    return "Normal";
  }
}
