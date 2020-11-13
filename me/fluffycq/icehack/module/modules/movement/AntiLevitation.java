//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.events.EventPlayerIsPotionActive;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.MobEffects;

public class AntiLevitation extends Module {
  public AntiLevitation() {
    super("AntiLevitation", 0, Category.MOVEMENT);
    this.IsPotionActive = new Listener(paramEventPlayerIsPotionActive -> {
          if (paramEventPlayerIsPotionActive.potion == MobEffects.LEVITATION)
            paramEventPlayerIsPotionActive.cancel(); 
        }new java.util.function.Predicate[0]);
  }
}
