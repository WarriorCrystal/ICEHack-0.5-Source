package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;

public abstract class CollectionNode<T> extends Node {
  private Boolean flowStyle;
  
  public CollectionNode(Tag paramTag, Mark paramMark1, Mark paramMark2, Boolean paramBoolean) {
    super(paramTag, paramMark1, paramMark2);
    this.flowStyle = paramBoolean;
  }
  
  public abstract List<T> getValue();
  
  public Boolean getFlowStyle() {
    return this.flowStyle;
  }
  
  public void setFlowStyle(Boolean paramBoolean) {
    this.flowStyle = paramBoolean;
  }
  
  public void setEndMark(Mark paramMark) {
    this.endMark = paramMark;
  }
}
