package javassist.compiler;

import java.util.HashMap;

public final class KeywordTable extends HashMap {
  public int lookup(String paramString) {
    V v = get(paramString);
    if (v == null)
      return -1; 
    return ((Integer)v).intValue();
  }
  
  public void append(String paramString, int paramInt) {
    put((K)paramString, (V)new Integer(paramInt));
  }
}
