//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.events.EventSetOpaqueCube;
import me.fluffycq.icehack.events.LightmapEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class FullBright extends Module {
  public FullBright() {
    super("Fullbright", 0, Category.RENDER);
    this.OnWorldEvent = new Listener(paramEntityJoinWorldEvent -> {
          if (paramEntityJoinWorldEvent.getEntity() == mc.player)
            brightGame(); 
        }new java.util.function.Predicate[0]);
    this.lightEvent = new Listener(paramLightmapEvent -> {
          if (mc.gameSettings.gammaSetting != 1000.0F)
            mc.gameSettings.gammaSetting = 1000.0F; 
        }new java.util.function.Predicate[0]);
    this.OnEventSetOpaqueCube = new Listener(paramEventSetOpaqueCube -> paramEventSetOpaqueCube.cancel(), new java.util.function.Predicate[0]);
  }
  
  public void brightGame() {
    PotionEffect potionEffect = new PotionEffect(Potion.getPotionById(16), 1000000);
    potionEffect.setPotionDurationMax(true);
    mc.player.addPotionEffect(potionEffect);
  }
  
  public void onDisable() {
    if (mc == null || mc.gameSettings == null || mc.player == null)
      return; 
    mc.gameSettings.gammaSetting = this.oldGamma;
    mc.player.removePotionEffect(Potion.getPotionById(16));
  }
  
  public void onEnable() {
    if (mc == null || mc.gameSettings == null || mc.player == null)
      return; 
    this.oldGamma = mc.gameSettings.gammaSetting;
  }
  
  public void onUpdate() {
    if (mc == null || mc.gameSettings == null || mc.player == null)
      return; 
    brightGame();
  }
}
