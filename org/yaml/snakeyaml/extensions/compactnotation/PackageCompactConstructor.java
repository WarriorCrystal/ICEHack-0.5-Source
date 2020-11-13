package org.yaml.snakeyaml.extensions.compactnotation;

public class PackageCompactConstructor extends CompactConstructor {
  private String packageName;
  
  public PackageCompactConstructor(String paramString) {
    this.packageName = paramString;
  }
  
  protected Class<?> getClassForName(String paramString) throws ClassNotFoundException {
    if (paramString.indexOf('.') < 0)
      try {
        return Class.forName(this.packageName + "." + paramString);
      } catch (ClassNotFoundException classNotFoundException) {} 
    return super.getClassForName(paramString);
  }
}
