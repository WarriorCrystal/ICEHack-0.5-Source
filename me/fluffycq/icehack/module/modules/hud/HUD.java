package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.events.PotionHUDEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class HUD extends Module {
  public HUD() {
    super("HUD", 0, Category.HUD);
    this.potionHud = new Listener(paramPotionHUDEvent -> {
          if (!this.potionHUD.getValBoolean())
            paramPotionHUDEvent.cancel(); 
        }new java.util.function.Predicate[0]);
    this.customFont = new Setting("CustomFont", this, false);
    this.potionHUD = new Setting("EffectHUD", this, true);
  }
}
