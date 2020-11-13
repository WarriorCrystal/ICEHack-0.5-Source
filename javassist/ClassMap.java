package javassist;

import java.util.HashMap;
import javassist.bytecode.Descriptor;

public class ClassMap extends HashMap {
  private ClassMap parent;
  
  public ClassMap() {
    this.parent = null;
  }
  
  ClassMap(ClassMap paramClassMap) {
    this.parent = paramClassMap;
  }
  
  public void put(CtClass paramCtClass1, CtClass paramCtClass2) {
    put(paramCtClass1.getName(), paramCtClass2.getName());
  }
  
  public void put(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return; 
    String str1 = toJvmName(paramString1);
    String str2 = (String)get(str1);
    if (str2 == null || !str2.equals(str1))
      put((K)str1, (V)toJvmName(paramString2)); 
  }
  
  public void putIfNone(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return; 
    String str1 = toJvmName(paramString1);
    String str2 = (String)get(str1);
    if (str2 == null)
      put((K)str1, (V)toJvmName(paramString2)); 
  }
  
  protected final void put0(Object paramObject1, Object paramObject2) {
    put((K)paramObject1, (V)paramObject2);
  }
  
  public Object get(Object paramObject) {
    Object object = super.get(paramObject);
    if (object == null && this.parent != null)
      return this.parent.get(paramObject); 
    return object;
  }
  
  public void fix(CtClass paramCtClass) {
    fix(paramCtClass.getName());
  }
  
  public void fix(String paramString) {
    String str = toJvmName(paramString);
    put((K)str, (V)str);
  }
  
  public static String toJvmName(String paramString) {
    return Descriptor.toJvmName(paramString);
  }
  
  public static String toJavaName(String paramString) {
    return Descriptor.toJavaName(paramString);
  }
}
