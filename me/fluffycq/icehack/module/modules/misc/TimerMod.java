//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import me.fluffycq.icehack.events.PlayerUpdateEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class TimerMod extends Module {
  public void onDisable() {
    super.onDisable();
    mc.timer.tickLength = 50.0F;
  }
  
  public void onUpdate() {
    setModInfo(getInfo());
  }
  
  public TimerMod() {
    super("Timer", 0, Category.MISC);
  }
  
  public static void SetOverrideSpeed(float paramFloat) {
    OverrideSpeed = paramFloat;
  }
  
  private float GetSpeed() {
    return (float)Math.max(this.speed.getValDouble(), 0.10000000149011612D);
  }
  
  public String getInfo() {
    return (OverrideSpeed != 1.0F && OverrideSpeed > 0.1F) ? String.valueOf(OverrideSpeed) : String.valueOf(this.speed.getValDouble());
  }
  
  public String getModInfo() {
    return getInfo();
  }
}
