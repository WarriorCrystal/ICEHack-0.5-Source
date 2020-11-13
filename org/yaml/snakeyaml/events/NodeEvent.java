package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public abstract class NodeEvent extends Event {
  private final String anchor;
  
  public NodeEvent(String paramString, Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
    this.anchor = paramString;
  }
  
  public String getAnchor() {
    return this.anchor;
  }
  
  protected String getArguments() {
    return "anchor=" + this.anchor;
  }
}
