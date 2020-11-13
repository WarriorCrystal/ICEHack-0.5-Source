package me.zero.alpine.listener;

import java.util.function.Predicate;
import net.jodah.typetools.TypeResolver;

public final class Listener<T> implements EventHook<T> {
  private final Class<T> target;
  
  private final EventHook<T> hook;
  
  private final Predicate<T>[] filters;
  
  private final byte priority;
  
  @SafeVarargs
  public Listener(EventHook<T> paramEventHook, Predicate<T>... paramVarArgs) {
    this(paramEventHook, (byte)3, paramVarArgs);
  }
  
  @SafeVarargs
  public Listener(EventHook<T> paramEventHook, byte paramByte, Predicate<T>... paramVarArgs) {
    this.hook = paramEventHook;
    this.priority = paramByte;
    this.target = TypeResolver.resolveRawArgument(EventHook.class, paramEventHook.getClass());
    this.filters = paramVarArgs;
  }
  
  public final Class<T> getTarget() {
    return this.target;
  }
  
  public final byte getPriority() {
    return this.priority;
  }
  
  public final void invoke(T paramT) {
    if (this.filters.length > 0)
      for (Predicate<T> predicate : this.filters) {
        if (!predicate.test(paramT))
          return; 
      }  
    this.hook.invoke(paramT);
  }
}
