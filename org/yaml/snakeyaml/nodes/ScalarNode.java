package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

public class ScalarNode extends Node {
  private Character style;
  
  private String value;
  
  public ScalarNode(Tag paramTag, String paramString, Mark paramMark1, Mark paramMark2, Character paramCharacter) {
    this(paramTag, true, paramString, paramMark1, paramMark2, paramCharacter);
  }
  
  public ScalarNode(Tag paramTag, boolean paramBoolean, String paramString, Mark paramMark1, Mark paramMark2, Character paramCharacter) {
    super(paramTag, paramMark1, paramMark2);
    if (paramString == null)
      throw new NullPointerException("value in a Node is required."); 
    this.value = paramString;
    this.style = paramCharacter;
    this.resolved = paramBoolean;
  }
  
  public Character getStyle() {
    return this.style;
  }
  
  public NodeId getNodeId() {
    return NodeId.scalar;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public String toString() {
    return "<" + getClass().getName() + " (tag=" + getTag() + ", value=" + getValue() + ")>";
  }
}
