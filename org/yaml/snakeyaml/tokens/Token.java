package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

public abstract class Token {
  private final Mark startMark;
  
  private final Mark endMark;
  
  public enum ID {
    Alias, Anchor, BlockEnd, BlockEntry, BlockMappingStart, BlockSequenceStart, Directive, DocumentEnd, DocumentStart, FlowEntry, FlowMappingEnd, FlowMappingStart, FlowSequenceEnd, FlowSequenceStart, Key, Scalar, StreamEnd, StreamStart, Tag, Value, Whitespace, Comment, Error;
  }
  
  public Token(Mark paramMark1, Mark paramMark2) {
    if (paramMark1 == null || paramMark2 == null)
      throw new YAMLException("Token requires marks."); 
    this.startMark = paramMark1;
    this.endMark = paramMark2;
  }
  
  public String toString() {
    return "<" + getClass().getName() + "(" + getArguments() + ")>";
  }
  
  public Mark getStartMark() {
    return this.startMark;
  }
  
  public Mark getEndMark() {
    return this.endMark;
  }
  
  protected String getArguments() {
    return "";
  }
  
  public abstract ID getTokenId();
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Token)
      return toString().equals(paramObject.toString()); 
    return false;
  }
  
  public int hashCode() {
    return toString().hashCode();
  }
}
