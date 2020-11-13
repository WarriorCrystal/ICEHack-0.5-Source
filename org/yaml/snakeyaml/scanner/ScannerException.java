package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ScannerException extends MarkedYAMLException {
  private static final long serialVersionUID = 4782293188600445954L;
  
  public ScannerException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2, String paramString3) {
    super(paramString1, paramMark1, paramString2, paramMark2, paramString3);
  }
  
  public ScannerException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2) {
    this(paramString1, paramMark1, paramString2, paramMark2, null);
  }
}
