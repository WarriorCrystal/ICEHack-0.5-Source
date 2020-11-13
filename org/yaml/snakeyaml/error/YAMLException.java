package org.yaml.snakeyaml.error;

public class YAMLException extends RuntimeException {
  private static final long serialVersionUID = -4738336175050337570L;
  
  public YAMLException(String paramString) {
    super(paramString);
  }
  
  public YAMLException(Throwable paramThrowable) {
    super(paramThrowable);
  }
  
  public YAMLException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
}
