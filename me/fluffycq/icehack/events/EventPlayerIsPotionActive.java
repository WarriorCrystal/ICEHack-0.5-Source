package me.fluffycq.icehack.events;

import net.minecraft.potion.Potion;

public class EventPlayerIsPotionActive extends ICEEvent {
  public EventPlayerIsPotionActive(Potion paramPotion) {
    this.potion = paramPotion;
  }
}
