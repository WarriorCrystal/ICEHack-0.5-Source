package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public abstract class CollectionEndEvent extends Event {
  public CollectionEndEvent(Mark paramMark1, Mark paramMark2) {
    super(paramMark1, paramMark2);
  }
}
