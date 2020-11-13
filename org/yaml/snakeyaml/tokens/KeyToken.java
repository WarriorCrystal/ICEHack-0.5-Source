package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

public final class KeyToken extends Token {
  public KeyToken(Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
  }
  
  public Token.ID getTokenId() {
    return Token.ID.Key;
  }
}
