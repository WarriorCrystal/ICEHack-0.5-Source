package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;

public abstract class MemberValue {
  ConstPool14 cp;
  
  char tag;
  
  MemberValue(char paramChar, ConstPool14 paramConstPool14) {
    this.cp = paramConstPool14;
    this.tag = paramChar;
  }
  
  abstract Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) throws ClassNotFoundException;
  
  abstract Class getType(ClassLoader paramClassLoader) throws ClassNotFoundException;
  
  static Class loadClass(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException, NoSuchClassError {
    try {
      return Class.forName(convertFromArray(paramString), true, paramClassLoader);
    } catch (LinkageError linkageError) {
      throw new NoSuchClassError(paramString, linkageError);
    } 
  }
  
  private static String convertFromArray(String paramString) {
    int i = paramString.indexOf("[]");
    if (i != -1) {
      String str = paramString.substring(0, i);
      StringBuffer stringBuffer = new StringBuffer(Descriptor.of(str));
      while (i != -1) {
        stringBuffer.insert(0, "[");
        i = paramString.indexOf("[]", i + 1);
      } 
      return stringBuffer.toString().replace('/', '.');
    } 
    return paramString;
  }
  
  public abstract void accept(MemberValueVisitor paramMemberValueVisitor);
  
  public abstract void write(AnnotationsWriter paramAnnotationsWriter) throws IOException;
}
