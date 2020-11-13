package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class SequenceEndEvent extends CollectionEndEvent {
  public SequenceEndEvent(Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.SequenceEnd == paramID);
  }
}
