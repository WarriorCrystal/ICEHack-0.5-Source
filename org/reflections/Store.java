package org.reflections;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
  private transient boolean concurrent;
  
  private final Map<String, Multimap<String, String>> storeMap;
  
  protected Store() {
    this.storeMap = new HashMap<>();
    this.concurrent = false;
  }
  
  public Store(Configuration paramConfiguration) {
    this.storeMap = new HashMap<>();
    this.concurrent = (paramConfiguration.getExecutorService() != null);
  }
  
  public Set<String> keySet() {
    return this.storeMap.keySet();
  }
  
  public Multimap<String, String> getOrCreate(String paramString) {
    SetMultimap setMultimap;
    Multimap multimap = this.storeMap.get(paramString);
    if (multimap == null) {
      SetMultimap setMultimap1 = Multimaps.newSetMultimap(new HashMap<>(), new Supplier<Set<String>>() {
            public Set<String> get() {
              return Sets.newSetFromMap(new ConcurrentHashMap<>());
            }
          });
      setMultimap = this.concurrent ? Multimaps.synchronizedSetMultimap(setMultimap1) : setMultimap1;
      this.storeMap.put(paramString, setMultimap);
    } 
    return (Multimap<String, String>)setMultimap;
  }
  
  public Multimap<String, String> get(String paramString) {
    Multimap<String, String> multimap = this.storeMap.get(paramString);
    if (multimap == null)
      throw new ReflectionsException("Scanner " + paramString + " was not configured"); 
    return multimap;
  }
  
  public Iterable<String> get(String paramString, String... paramVarArgs) {
    return get(paramString, Arrays.asList(paramVarArgs));
  }
  
  public Iterable<String> get(String paramString, Iterable<String> paramIterable) {
    Multimap<String, String> multimap = get(paramString);
    IterableChain<String> iterableChain = new IterableChain();
    for (String str : paramIterable)
      iterableChain.addAll(multimap.get(str)); 
    return iterableChain;
  }
  
  private Iterable<String> getAllIncluding(String paramString, Iterable<String> paramIterable, IterableChain<String> paramIterableChain) {
    paramIterableChain.addAll((Iterable)paramIterable);
    for (String str : paramIterable) {
      Iterable<String> iterable = get(paramString, new String[] { str });
      if (iterable.iterator().hasNext())
        getAllIncluding(paramString, iterable, paramIterableChain); 
    } 
    return paramIterableChain;
  }
  
  public Iterable<String> getAll(String paramString1, String paramString2) {
    return getAllIncluding(paramString1, get(paramString1, new String[] { paramString2 }), new IterableChain<>());
  }
  
  public Iterable<String> getAll(String paramString, Iterable<String> paramIterable) {
    return getAllIncluding(paramString, get(paramString, paramIterable), new IterableChain<>());
  }
  
  private static class IterableChain<T> implements Iterable<T> {
    private final List<Iterable<T>> chain = Lists.newArrayList();
    
    private void addAll(Iterable<T> param1Iterable) {
      this.chain.add(param1Iterable);
    }
    
    public Iterator<T> iterator() {
      return Iterables.concat(this.chain).iterator();
    }
    
    private IterableChain() {}
  }
}
