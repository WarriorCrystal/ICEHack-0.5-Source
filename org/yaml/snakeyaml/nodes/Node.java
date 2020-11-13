package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

public abstract class Node {
  private Tag tag;
  
  private Mark startMark;
  
  protected Mark endMark;
  
  private Class<? extends Object> type;
  
  private boolean twoStepsConstruction;
  
  private String anchor;
  
  protected boolean resolved;
  
  protected Boolean useClassConstructor;
  
  public Node(Tag paramTag, Mark paramMark1, Mark paramMark2) {
    setTag(paramTag);
    this.startMark = paramMark1;
    this.endMark = paramMark2;
    this.type = Object.class;
    this.twoStepsConstruction = false;
    this.resolved = true;
    this.useClassConstructor = null;
  }
  
  public Tag getTag() {
    return this.tag;
  }
  
  public Mark getEndMark() {
    return this.endMark;
  }
  
  public abstract NodeId getNodeId();
  
  public Mark getStartMark() {
    return this.startMark;
  }
  
  public void setTag(Tag paramTag) {
    if (paramTag == null)
      throw new NullPointerException("tag in a Node is required."); 
    this.tag = paramTag;
  }
  
  public final boolean equals(Object paramObject) {
    return super.equals(paramObject);
  }
  
  public Class<? extends Object> getType() {
    return this.type;
  }
  
  public void setType(Class<? extends Object> paramClass) {
    if (!paramClass.isAssignableFrom(this.type))
      this.type = paramClass; 
  }
  
  public void setTwoStepsConstruction(boolean paramBoolean) {
    this.twoStepsConstruction = paramBoolean;
  }
  
  public boolean isTwoStepsConstruction() {
    return this.twoStepsConstruction;
  }
  
  public final int hashCode() {
    return super.hashCode();
  }
  
  public boolean useClassConstructor() {
    if (this.useClassConstructor == null) {
      if (!this.tag.isSecondary() && isResolved() && !Object.class.equals(this.type) && 
        !this.tag.equals(Tag.NULL))
        return true; 
      if (this.tag.isCompatible(getType()))
        return true; 
      return false;
    } 
    return this.useClassConstructor.booleanValue();
  }
  
  public void setUseClassConstructor(Boolean paramBoolean) {
    this.useClassConstructor = paramBoolean;
  }
  
  public boolean isResolved() {
    return this.resolved;
  }
  
  public String getAnchor() {
    return this.anchor;
  }
  
  public void setAnchor(String paramString) {
    this.anchor = paramString;
  }
}
