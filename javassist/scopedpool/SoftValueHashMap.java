package javassist.scopedpool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoftValueHashMap extends AbstractMap implements Map {
  private Map hash;
  
  private static class SoftValueRef extends SoftReference {
    public Object key;
    
    private SoftValueRef(Object param1Object1, Object param1Object2, ReferenceQueue<? super T> param1ReferenceQueue) {
      super((T)param1Object2, param1ReferenceQueue);
      this.key = param1Object1;
    }
    
    private static SoftValueRef create(Object param1Object1, Object param1Object2, ReferenceQueue param1ReferenceQueue) {
      if (param1Object2 == null)
        return null; 
      return new SoftValueRef(param1Object1, param1Object2, param1ReferenceQueue);
    }
  }
  
  public Set entrySet() {
    processQueue();
    return this.hash.entrySet();
  }
  
  private ReferenceQueue queue = new ReferenceQueue();
  
  private void processQueue() {
    SoftValueRef softValueRef;
    while ((softValueRef = (SoftValueRef)this.queue.poll()) != null) {
      if (softValueRef == (SoftValueRef)this.hash.get(softValueRef.key))
        this.hash.remove(softValueRef.key); 
    } 
  }
  
  public SoftValueHashMap(int paramInt, float paramFloat) {
    this.hash = new HashMap<Object, Object>(paramInt, paramFloat);
  }
  
  public SoftValueHashMap(int paramInt) {
    this.hash = new HashMap<Object, Object>(paramInt);
  }
  
  public SoftValueHashMap() {
    this.hash = new HashMap<Object, Object>();
  }
  
  public SoftValueHashMap(Map<? extends K, ? extends V> paramMap) {
    this(Math.max(2 * paramMap.size(), 11), 0.75F);
    putAll(paramMap);
  }
  
  public int size() {
    processQueue();
    return this.hash.size();
  }
  
  public boolean isEmpty() {
    processQueue();
    return this.hash.isEmpty();
  }
  
  public boolean containsKey(Object paramObject) {
    processQueue();
    return this.hash.containsKey(paramObject);
  }
  
  public Object get(Object paramObject) {
    processQueue();
    SoftReference softReference = (SoftReference)this.hash.get(paramObject);
    if (softReference != null)
      return softReference.get(); 
    return null;
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    processQueue();
    SoftReference softReference = (SoftReference)this.hash.put(paramObject1, SoftValueRef.create(paramObject1, paramObject2, this.queue));
    if (softReference != null)
      softReference = (SoftReference)((SoftReference<Object>)softReference).get(); 
    return softReference;
  }
  
  public Object remove(Object paramObject) {
    processQueue();
    return this.hash.remove(paramObject);
  }
  
  public void clear() {
    processQueue();
    this.hash.clear();
  }
}
