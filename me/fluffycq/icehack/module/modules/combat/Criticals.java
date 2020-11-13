//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class Criticals extends Module {
  public Criticals() {
    super("Criticals", 0, Category.COMBAT);
    this.packetEvent = new Listener(paramSend -> {
          if (paramSend.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity cPacketUseEntity = (CPacketUseEntity)paramSend.getPacket();
            if (cPacketUseEntity.getAction() == CPacketUseEntity.Action.ATTACK && cPacketUseEntity.getEntityFromWorld((World)mc.world) != null) {
              Entity entity = cPacketUseEntity.getEntityFromWorld((World)mc.world);
              if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
                mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
                mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
                mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
                mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
              } 
              if (mc.player.onGround) {
                CPacketPlayer.Position position1 = new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625D, mc.player.posZ, false);
                mc.player.connection.sendPacket((Packet)position1);
                CPacketPlayer.Position position2 = new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false);
                mc.player.connection.sendPacket((Packet)position2);
                CPacketPlayer.Position position3 = new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6D, mc.player.posZ, false);
                mc.player.connection.sendPacket((Packet)position3);
                CPacketPlayer.Position position4 = new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false);
                mc.player.connection.sendPacket((Packet)position4);
                CPacketPlayer.Position position5 = new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6D, mc.player.posZ, false);
                mc.player.connection.sendPacket((Packet)position5);
                CPacketPlayer.Position position6 = new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false);
                mc.player.connection.sendPacket((Packet)position6);
                CPacketPlayer cPacketPlayer = new CPacketPlayer();
                mc.player.connection.sendPacket((Packet)cPacketPlayer);
                mc.player.onCriticalHit(entity);
              } 
            } 
          } 
        }new java.util.function.Predicate[0]);
  }
}
