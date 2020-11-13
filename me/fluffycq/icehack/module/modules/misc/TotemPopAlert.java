//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import java.util.HashMap;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.PopTotemEvent;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;

public class TotemPopAlert extends Module {
  public void onUpdate() {
    for (EntityPlayer entityPlayer : mc.world.playerEntities) {
      if (entityPlayer.getHealth() <= 0.0F && this.popped.containsKey(entityPlayer.getName())) {
        sendMessage(String.valueOf((new StringBuilder()).append("&3").append(entityPlayer.getName()).append(" &4died after they popped &6").append(this.popped.get(entityPlayer.getName())).append(" totems")));
        this.popped.remove(entityPlayer.getName());
      } 
    } 
  }
  
  public void onDisable() {
    this.popped.clear();
  }
  
  public void onEnable() {
    this.popped.clear();
  }
  
  private void sendMessage(String paramString) {
    if (this.cName.getValBoolean()) {
      Messages.sendChatMessage(paramString);
    } else {
      Messages.sendMessage(paramString);
    } 
  }
  
  public TotemPopAlert() {
    super("TotemPopAlert", 0, Category.MISC);
    this.popEvent = new Listener(paramPopTotemEvent -> {
          if (this.popped == null)
            this.popped = new HashMap<>(); 
          if (this.popped.get(paramPopTotemEvent.getEntity().getName()) == null) {
            this.popped.put(paramPopTotemEvent.getEntity().getName(), Integer.valueOf(1));
            sendMessage(String.valueOf((new StringBuilder()).append("&3").append(paramPopTotemEvent.getEntity().getName()).append(" &4popped &61 totem")));
          } else if (this.popped.get(paramPopTotemEvent.getEntity().getName()) != null) {
            int i = ((Integer)this.popped.get(paramPopTotemEvent.getEntity().getName())).intValue();
            int j = ++i;
            this.popped.put(paramPopTotemEvent.getEntity().getName(), Integer.valueOf(j));
            sendMessage(String.valueOf((new StringBuilder()).append("&3").append(paramPopTotemEvent.getEntity().getName()).append(" &4popped &6").append(String.valueOf(j)).append(" totems")));
          } 
        }new java.util.function.Predicate[0]);
    this.totemPopListener = new Listener(paramReceive -> {
          if (mc.world == null || mc.player == null)
            return; 
          if (isEnabled() && paramReceive.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus)paramReceive.getPacket()).getOpCode() == 35) {
            Entity entity = ((SPacketEntityStatus)paramReceive.getPacket()).getEntity((World)mc.world);
            ICEHack.EVENT_BUS.post(new PopTotemEvent(entity));
          } 
        }new java.util.function.Predicate[0]);
    this.cName = new Setting("ICEHack", this, true);
  }
}
