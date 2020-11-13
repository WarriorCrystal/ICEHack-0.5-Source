package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class ScalarEvent extends NodeEvent {
  private final String tag;
  
  private final Character style;
  
  private final String value;
  
  private final ImplicitTuple implicit;
  
  public ScalarEvent(String paramString1, String paramString2, ImplicitTuple paramImplicitTuple, String paramString3, Mark paramMark1, Mark paramMark2, Character paramCharacter) {
    super(paramString1, paramMark1, paramMark2);
    this.tag = paramString2;
    this.implicit = paramImplicitTuple;
    this.value = paramString3;
    this.style = paramCharacter;
  }
  
  public String getTag() {
    return this.tag;
  }
  
  public Character getStyle() {
    return this.style;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public ImplicitTuple getImplicit() {
    return this.implicit;
  }
  
  protected String getArguments() {
    return super.getArguments() + ", tag=" + this.tag + ", " + this.implicit + ", value=" + this.value;
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.Scalar == paramID);
  }
}
