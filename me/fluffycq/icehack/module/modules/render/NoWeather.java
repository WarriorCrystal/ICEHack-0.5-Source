//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.events.EventRenderRainStrength;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class NoWeather extends Module {
  public NoWeather() {
    super("NoWeather", 0, Category.RENDER);
    this.OnRainStrength = new Listener(paramEventRenderRainStrength -> {
          if (mc.world == null)
            return; 
          paramEventRenderRainStrength.cancel();
        }new java.util.function.Predicate[0]);
  }
}
