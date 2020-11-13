package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public abstract class CollectionStartEvent extends NodeEvent {
  private final String tag;
  
  private final boolean implicit;
  
  private final Boolean flowStyle;
  
  public CollectionStartEvent(String paramString1, String paramString2, boolean paramBoolean, Mark paramMark1, Mark paramMark2, Boolean paramBoolean1) {
    super(paramString1, paramMark1, paramMark2);
    this.tag = paramString2;
    this.implicit = paramBoolean;
    this.flowStyle = paramBoolean1;
  }
  
  public String getTag() {
    return this.tag;
  }
  
  public boolean getImplicit() {
    return this.implicit;
  }
  
  public Boolean getFlowStyle() {
    return this.flowStyle;
  }
  
  protected String getArguments() {
    return super.getArguments() + ", tag=" + this.tag + ", implicit=" + this.implicit;
  }
}
