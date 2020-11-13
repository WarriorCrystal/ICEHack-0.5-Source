package javassist.util.proxy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class SecurityActions {
  static Method[] getDeclaredMethods(final Class clazz) {
    if (System.getSecurityManager() == null)
      return clazz.getDeclaredMethods(); 
    return AccessController.<Method[]>doPrivileged(new PrivilegedAction<Method>() {
          public Object run() {
            return clazz.getDeclaredMethods();
          }
        });
  }
  
  static Constructor[] getDeclaredConstructors(final Class clazz) {
    if (System.getSecurityManager() == null)
      return (Constructor[])clazz.getDeclaredConstructors(); 
    return AccessController.<Constructor[]>doPrivileged(new PrivilegedAction<Constructor>() {
          public Object run() {
            return clazz.getDeclaredConstructors();
          }
        });
  }
  
  static Method getDeclaredMethod(final Class clazz, final String name, final Class[] types) throws NoSuchMethodException {
    if (System.getSecurityManager() == null)
      return clazz.getDeclaredMethod(name, types); 
    try {
      return AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Object run() throws Exception {
              return clazz.getDeclaredMethod(name, types);
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      if (privilegedActionException.getCause() instanceof NoSuchMethodException)
        throw (NoSuchMethodException)privilegedActionException.getCause(); 
      throw new RuntimeException(privilegedActionException.getCause());
    } 
  }
  
  static Constructor getDeclaredConstructor(final Class clazz, final Class[] types) throws NoSuchMethodException {
    if (System.getSecurityManager() == null)
      return clazz.getDeclaredConstructor(types); 
    try {
      return AccessController.<Constructor>doPrivileged(new PrivilegedExceptionAction<Constructor>() {
            public Object run() throws Exception {
              return clazz.getDeclaredConstructor(types);
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      if (privilegedActionException.getCause() instanceof NoSuchMethodException)
        throw (NoSuchMethodException)privilegedActionException.getCause(); 
      throw new RuntimeException(privilegedActionException.getCause());
    } 
  }
  
  static void setAccessible(final AccessibleObject ao, final boolean accessible) {
    if (System.getSecurityManager() == null) {
      ao.setAccessible(accessible);
    } else {
      AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              ao.setAccessible(accessible);
              return null;
            }
          });
    } 
  }
  
  static void set(final Field fld, final Object target, final Object value) throws IllegalAccessException {
    if (System.getSecurityManager() == null) {
      fld.set(target, value);
    } else {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
              public Object run() throws Exception {
                fld.set(target, value);
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        if (privilegedActionException.getCause() instanceof NoSuchMethodException)
          throw (IllegalAccessException)privilegedActionException.getCause(); 
        throw new RuntimeException(privilegedActionException.getCause());
      } 
    } 
  }
}
