package org.yaml.snakeyaml.events;

import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public final class DocumentStartEvent extends Event {
  private final boolean explicit;
  
  private final DumperOptions.Version version;
  
  private final Map<String, String> tags;
  
  public DocumentStartEvent(Mark paramMark1, Mark paramMark2, boolean paramBoolean, DumperOptions.Version paramVersion, Map<String, String> paramMap) {
    super(paramMark1, paramMark2);
    this.explicit = paramBoolean;
    this.version = paramVersion;
    this.tags = paramMap;
  }
  
  public boolean getExplicit() {
    return this.explicit;
  }
  
  public DumperOptions.Version getVersion() {
    return this.version;
  }
  
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  public boolean is(Event.ID paramID) {
    return (Event.ID.DocumentStart == paramID);
  }
}
