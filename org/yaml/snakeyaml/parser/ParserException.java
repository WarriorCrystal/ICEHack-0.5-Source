package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ParserException extends MarkedYAMLException {
  private static final long serialVersionUID = -2349253802798398038L;
  
  public ParserException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2) {
    super(paramString1, paramMark1, paramString2, paramMark2, null, null);
  }
}
