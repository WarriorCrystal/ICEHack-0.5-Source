package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.events.EventHorseSaddled;
import me.fluffycq.icehack.events.EventSteerEntity;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class EntityControl extends Module {
  public EntityControl() {
    super("EntityControl", 0, Category.MOVEMENT);
    this.OnSteerEntity = new Listener(paramEventSteerEntity -> paramEventSteerEntity.cancel(), new java.util.function.Predicate[0]);
    this.OnHorseSaddled = new Listener(paramEventHorseSaddled -> paramEventHorseSaddled.cancel(), new java.util.function.Predicate[0]);
  }
}
