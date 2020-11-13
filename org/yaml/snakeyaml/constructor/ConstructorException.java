package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ConstructorException extends MarkedYAMLException {
  private static final long serialVersionUID = -8816339931365239910L;
  
  protected ConstructorException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2, Throwable paramThrowable) {
    super(paramString1, paramMark1, paramString2, paramMark2, paramThrowable);
  }
  
  protected ConstructorException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2) {
    this(paramString1, paramMark1, paramString2, paramMark2, null);
  }
}
