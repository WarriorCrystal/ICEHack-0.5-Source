package javassist.runtime;

public class DotClass {
  public static NoClassDefFoundError fail(ClassNotFoundException paramClassNotFoundException) {
    return new NoClassDefFoundError(paramClassNotFoundException.getMessage());
  }
}
