package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public abstract class Event {
  private final Mark startMark;
  
  private final Mark endMark;
  
  public enum ID {
    Alias, DocumentEnd, DocumentStart, MappingEnd, MappingStart, Scalar, SequenceEnd, SequenceStart, StreamEnd, StreamStart;
  }
  
  public Event(Mark paramMark1, Mark paramMark2) {
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
  
  public abstract boolean is(ID paramID);
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Event)
      return toString().equals(paramObject.toString()); 
    return false;
  }
  
  public int hashCode() {
    return toString().hashCode();
  }
}
