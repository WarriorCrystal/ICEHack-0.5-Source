package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class MappingStartEvent extends CollectionStartEvent {
  public MappingStartEvent(String paramString1, String paramString2, boolean paramBoolean, Mark paramMark1, Mark paramMark2, Boolean paramBoolean1) {
    super(paramString1, paramString2, paramBoolean, paramMark1, paramMark2, paramBoolean1);
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.MappingStart == paramID);
  }
}
