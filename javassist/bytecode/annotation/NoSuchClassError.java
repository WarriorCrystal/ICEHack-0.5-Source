package javassist.bytecode.annotation;

public class NoSuchClassError extends Error {
  private String className;
  
  public NoSuchClassError(String paramString, Error paramError) {
    super(paramError.toString(), paramError);
    this.className = paramString;
  }
  
  public String getClassName() {
    return this.className;
  }
}
