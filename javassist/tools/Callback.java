package javassist.tools;

import java.util.HashMap;
import java.util.UUID;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public abstract class Callback {
  public static HashMap callbacks = new HashMap<Object, Object>();
  
  private final String sourceCode;
  
  public Callback(String paramString) {
    String str = UUID.randomUUID().toString();
    callbacks.put(str, this);
    this.sourceCode = "((javassist.tools.Callback) javassist.tools.Callback.callbacks.get(\"" + str + "\")).result(new Object[]{" + paramString + "});";
  }
  
  public abstract void result(Object[] paramArrayOfObject);
  
  public String toString() {
    return sourceCode();
  }
  
  public String sourceCode() {
    return this.sourceCode;
  }
  
  public static void insertBefore(CtBehavior paramCtBehavior, Callback paramCallback) throws CannotCompileException {
    paramCtBehavior.insertBefore(paramCallback.toString());
  }
  
  public static void insertAfter(CtBehavior paramCtBehavior, Callback paramCallback) throws CannotCompileException {
    paramCtBehavior.insertAfter(paramCallback.toString(), false);
  }
  
  public static void insertAfter(CtBehavior paramCtBehavior, Callback paramCallback, boolean paramBoolean) throws CannotCompileException {
    paramCtBehavior.insertAfter(paramCallback.toString(), paramBoolean);
  }
  
  public static int insertAt(CtBehavior paramCtBehavior, Callback paramCallback, int paramInt) throws CannotCompileException {
    return paramCtBehavior.insertAt(paramInt, paramCallback.toString());
  }
}
