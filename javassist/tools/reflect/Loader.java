package javassist.tools.reflect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;

public class Loader extends Loader {
  protected Reflection reflection;
  
  public static void main(String[] paramArrayOfString) throws Throwable {
    Loader loader = new Loader();
    loader.run(paramArrayOfString);
  }
  
  public Loader() throws CannotCompileException, NotFoundException {
    delegateLoadingOf("javassist.tools.reflect.Loader");
    this.reflection = new Reflection();
    ClassPool classPool = ClassPool.getDefault();
    addTranslator(classPool, this.reflection);
  }
  
  public boolean makeReflective(String paramString1, String paramString2, String paramString3) throws CannotCompileException, NotFoundException {
    return this.reflection.makeReflective(paramString1, paramString2, paramString3);
  }
}
