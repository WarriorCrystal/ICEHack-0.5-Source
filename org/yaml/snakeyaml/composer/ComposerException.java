package org.yaml.snakeyaml.composer;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ComposerException extends MarkedYAMLException {
  private static final long serialVersionUID = 2146314636913113935L;
  
  protected ComposerException(String paramString1, Mark paramMark1, String paramString2, Mark paramMark2) {
    super(paramString1, paramMark1, paramString2, paramMark2);
  }
}
