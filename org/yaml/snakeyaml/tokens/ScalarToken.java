package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

public final class ScalarToken extends Token {
  private final String value;
  
  private final boolean plain;
  
  private final char style;
  
  public ScalarToken(String paramString, Mark paramMark1, Mark paramMark2, boolean paramBoolean) {
    this(paramString, paramBoolean, paramMark1, paramMark2, false);
  }
  
  public ScalarToken(String paramString, boolean paramBoolean, Mark paramMark1, Mark paramMark2, char paramChar) {
    super(paramMark1, paramMark2);
    this.value = paramString;
    this.plain = paramBoolean;
    this.style = paramChar;
  }
  
  public boolean getPlain() {
    return this.plain;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public char getStyle() {
    return this.style;
  }
  
  protected String getArguments() {
    return "value=" + this.value + ", plain=" + this.plain + ", style=" + this.style;
  }
  
  public Token.ID getTokenId() {
    return Token.ID.Scalar;
  }
}
