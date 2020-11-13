package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class StreamStartEvent extends Event {
  public StreamStartEvent(Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.StreamStart == paramID);
  }
}
