package org.yaml.snakeyaml.tokens;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

public final class DirectiveToken<T> extends Token {
  private final String name;
  
  private final List<T> value;
  
  public DirectiveToken(String paramString, List<T> paramList, Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
    this.name = paramString;
    if (paramList != null && paramList.size() != 2)
      throw new YAMLException("Two strings must be provided instead of " + 
          String.valueOf(paramList.size())); 
    this.value = paramList;
  }
  
  public String getName() {
    return this.name;
  }
  
  public List<T> getValue() {
    return this.value;
  }
  
  protected String getArguments() {
    if (this.value != null)
      return "name=" + this.name + ", value=[" + this.value.get(0) + ", " + this.value.get(1) + "]"; 
    return "name=" + this.name;
  }
  
  public Token.ID getTokenId() {
    return Token.ID.Directive;
  }
}
