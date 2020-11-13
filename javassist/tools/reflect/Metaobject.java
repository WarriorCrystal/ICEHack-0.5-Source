package javassist.tools.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Metaobject implements Serializable {
  protected ClassMetaobject classmetaobject;
  
  protected Metalevel baseobject;
  
  protected Method[] methods;
  
  public Metaobject(Object paramObject, Object[] paramArrayOfObject) {
    this.baseobject = (Metalevel)paramObject;
    this.classmetaobject = this.baseobject._getClass();
    this.methods = this.classmetaobject.getReflectiveMethods();
  }
  
  protected Metaobject() {
    this.baseobject = null;
    this.classmetaobject = null;
    this.methods = null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(this.baseobject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.baseobject = (Metalevel)paramObjectInputStream.readObject();
    this.classmetaobject = this.baseobject._getClass();
    this.methods = this.classmetaobject.getReflectiveMethods();
  }
  
  public final ClassMetaobject getClassMetaobject() {
    return this.classmetaobject;
  }
  
  public final Object getObject() {
    return this.baseobject;
  }
  
  public final void setObject(Object paramObject) {
    this.baseobject = (Metalevel)paramObject;
    this.classmetaobject = this.baseobject._getClass();
    this.methods = this.classmetaobject.getReflectiveMethods();
    this.baseobject._setMetaobject(this);
  }
  
  public final String getMethodName(int paramInt) {
    char c;
    String str = this.methods[paramInt].getName();
    byte b = 3;
    do {
      c = str.charAt(b++);
    } while (c >= '0' && '9' >= c);
    return str.substring(b);
  }
  
  public final Class[] getParameterTypes(int paramInt) {
    return this.methods[paramInt].getParameterTypes();
  }
  
  public final Class getReturnType(int paramInt) {
    return this.methods[paramInt].getReturnType();
  }
  
  public Object trapFieldRead(String paramString) {
    Class clazz = getClassMetaobject().getJavaClass();
    try {
      return clazz.getField(paramString).get(getObject());
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new RuntimeException(noSuchFieldException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.toString());
    } 
  }
  
  public void trapFieldWrite(String paramString, Object paramObject) {
    Class clazz = getClassMetaobject().getJavaClass();
    try {
      clazz.getField(paramString).set(getObject(), paramObject);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new RuntimeException(noSuchFieldException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.toString());
    } 
  }
  
  public Object trapMethodcall(int paramInt, Object[] paramArrayOfObject) throws Throwable {
    try {
      return this.methods[paramInt].invoke(getObject(), paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (IllegalAccessException illegalAccessException) {
      throw new CannotInvokeException(illegalAccessException);
    } 
  }
}
