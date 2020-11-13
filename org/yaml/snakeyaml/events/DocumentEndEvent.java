package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class DocumentEndEvent extends Event {
  private final boolean explicit;
  
  public DocumentEndEvent(Mark paramMark1, Mark paramMark2, boolean paramBoolean) {
    super(paramMark1, paramMark2);
    this.explicit = paramBoolean;
  }
  
  public boolean getExplicit() {
    return this.explicit;
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.DocumentEnd == paramID);
  }
}
