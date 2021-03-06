package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

public final class AliasToken extends Token {
  private final String value;
  
  public AliasToken(String paramString, Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
    this.value = paramString;
  }
  
  public String getValue() {
    return this.value;
  }
  
  protected String getArguments() {
    return "value=" + this.value;
  }
  
  public Token.ID getTokenId() {
    return Token.ID.Alias;
  }
}
