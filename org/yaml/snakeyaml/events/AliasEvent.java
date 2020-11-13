package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class AliasEvent extends NodeEvent {
  public AliasEvent(String paramString, Mark paramMark1, Mark paramMark2) {
    super(paramString, paramMark1, paramMark2);
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.Alias == paramID);
  }
}
