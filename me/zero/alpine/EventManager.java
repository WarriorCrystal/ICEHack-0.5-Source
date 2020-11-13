package me.zero.alpine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class EventManager implements EventBus {
  private final Map<Object, List<Listener>> SUBSCRIPTION_CACHE = new HashMap<>();
  
  private final Map<Class<?>, List<Listener>> SUBSCRIPTION_MAP = new HashMap<>();
  
  private final List<EventBus> ATTACHED_BUSES = new ArrayList<>();
  
  public void subscribe(Object paramObject) {
    List list = this.SUBSCRIPTION_CACHE.computeIfAbsent(paramObject, paramObject -> (List)Arrays.<Field>stream(paramObject.getClass().getDeclaredFields()).filter(EventManager::isValidField).map(()).filter(Objects::nonNull).collect(Collectors.toList()));
    list.forEach(this::subscribe);
    if (!this.ATTACHED_BUSES.isEmpty())
      this.ATTACHED_BUSES.forEach(paramEventBus -> paramEventBus.subscribe(paramObject)); 
  }
  
  public void subscribe(Object... paramVarArgs) {
    Arrays.<Object>stream(paramVarArgs).forEach(this::subscribe);
  }
  
  public void subscribe(Iterable<Object> paramIterable) {
    paramIterable.forEach(this::subscribe);
  }
  
  public void unsubscribe(Object paramObject) {
    List list = this.SUBSCRIPTION_CACHE.get(paramObject);
    if (list == null)
      return; 
    this.SUBSCRIPTION_MAP.values().forEach(paramList2 -> {
          Objects.requireNonNull(paramList1);
          paramList2.removeIf(paramList1::contains);
        });
    if (!this.ATTACHED_BUSES.isEmpty())
      this.ATTACHED_BUSES.forEach(paramEventBus -> paramEventBus.unsubscribe(paramObject)); 
  }
  
  public void unsubscribe(Object... paramVarArgs) {
    Arrays.<Object>stream(paramVarArgs).forEach(this::unsubscribe);
  }
  
  public void unsubscribe(Iterable<Object> paramIterable) {
    paramIterable.forEach(this::unsubscribe);
  }
  
  public void post(Object paramObject) {
    List list = this.SUBSCRIPTION_MAP.get(paramObject.getClass());
    if (list != null)
      list.forEach(paramListener -> paramListener.invoke(paramObject)); 
    if (!this.ATTACHED_BUSES.isEmpty())
      this.ATTACHED_BUSES.forEach(paramEventBus -> paramEventBus.post(paramObject)); 
  }
  
  public void attach(EventBus paramEventBus) {
    if (!this.ATTACHED_BUSES.contains(paramEventBus))
      this.ATTACHED_BUSES.add(paramEventBus); 
  }
  
  public void detach(EventBus paramEventBus) {
    if (this.ATTACHED_BUSES.contains(paramEventBus))
      this.ATTACHED_BUSES.remove(paramEventBus); 
  }
  
  private static boolean isValidField(Field paramField) {
    return (paramField.isAnnotationPresent((Class)EventHandler.class) && Listener.class.isAssignableFrom(paramField.getType()));
  }
  
  private static Listener asListener(Object paramObject, Field paramField) {
    try {
      boolean bool = paramField.isAccessible();
      paramField.setAccessible(true);
      Listener listener = (Listener)paramField.get(paramObject);
      paramField.setAccessible(bool);
      if (listener == null)
        return null; 
      if (listener.getPriority() > 5 || listener.getPriority() < 1)
        throw new RuntimeException("Event Priority out of bounds! %s"); 
      return listener;
    } catch (IllegalAccessException illegalAccessException) {
      return null;
    } 
  }
  
  private void subscribe(Listener paramListener) {
    List<Listener> list = this.SUBSCRIPTION_MAP.computeIfAbsent(paramListener.getTarget(), paramClass -> new ArrayList());
    byte b = 0;
    for (; b < list.size() && 
      paramListener.getPriority() >= ((Listener)list.get(b)).getPriority(); b++);
    list.add(b, paramListener);
  }
}
