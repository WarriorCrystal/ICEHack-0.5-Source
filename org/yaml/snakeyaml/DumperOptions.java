package org.yaml.snakeyaml;

import java.util.Map;
import java.util.TimeZone;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.serializer.AnchorGenerator;
import org.yaml.snakeyaml.serializer.NumberAnchorGenerator;

public class DumperOptions {
  public enum ScalarStyle {
    DOUBLE_QUOTED((String)Character.valueOf('"')),
    SINGLE_QUOTED((String)Character.valueOf('\'')),
    LITERAL(
      (String)Character.valueOf('|')),
    FOLDED((String)Character.valueOf('>')),
    PLAIN(null);
    
    private Character styleChar;
    
    ScalarStyle(Character param1Character) {
      this.styleChar = param1Character;
    }
    
    public Character getChar() {
      return this.styleChar;
    }
    
    public String toString() {
      return "Scalar style: '" + this.styleChar + "'";
    }
    
    public static ScalarStyle createStyle(Character param1Character) {
      if (param1Character == null)
        return PLAIN; 
      switch (param1Character.charValue()) {
        case '"':
          return DOUBLE_QUOTED;
        case '\'':
          return SINGLE_QUOTED;
        case '|':
          return LITERAL;
        case '>':
          return FOLDED;
      } 
      throw new YAMLException("Unknown scalar style character: " + param1Character);
    }
  }
  
  public enum FlowStyle {
    FLOW((String)Boolean.TRUE),
    BLOCK((String)Boolean.FALSE),
    AUTO(null);
    
    private Boolean styleBoolean;
    
    FlowStyle(Boolean param1Boolean) {
      this.styleBoolean = param1Boolean;
    }
    
    public Boolean getStyleBoolean() {
      return this.styleBoolean;
    }
    
    public String toString() {
      return "Flow style: '" + this.styleBoolean + "'";
    }
  }
  
  public enum LineBreak {
    WIN("\r\n"),
    MAC("\r"),
    UNIX("\n");
    
    private String lineBreak;
    
    LineBreak(String param1String1) {
      this.lineBreak = param1String1;
    }
    
    public String getString() {
      return this.lineBreak;
    }
    
    public String toString() {
      return "Line break: " + name();
    }
    
    public static LineBreak getPlatformLineBreak() {
      String str = System.getProperty("line.separator");
      for (LineBreak lineBreak : values()) {
        if (lineBreak.lineBreak.equals(str))
          return lineBreak; 
      } 
      return UNIX;
    }
  }
  
  public enum Version {
    V1_0((String)new Integer[] { Integer.valueOf(1), Integer.valueOf(0) }),
    V1_1((String)new Integer[] { Integer.valueOf(1), Integer.valueOf(1) });
    
    private Integer[] version;
    
    Version(Integer[] param1ArrayOfInteger) {
      this.version = param1ArrayOfInteger;
    }
    
    public int major() {
      return this.version[0].intValue();
    }
    
    public int minor() {
      return this.version[1].intValue();
    }
    
    public String getRepresentation() {
      return this.version[0] + "." + this.version[1];
    }
    
    public String toString() {
      return "Version: " + getRepresentation();
    }
  }
  
  private ScalarStyle defaultStyle = ScalarStyle.PLAIN;
  
  private FlowStyle defaultFlowStyle = FlowStyle.AUTO;
  
  private boolean canonical = false;
  
  private boolean allowUnicode = true;
  
  private boolean allowReadOnlyProperties = false;
  
  private int indent = 2;
  
  private int indicatorIndent = 0;
  
  private int bestWidth = 80;
  
  private boolean splitLines = true;
  
  private LineBreak lineBreak = LineBreak.UNIX;
  
  private boolean explicitStart = false;
  
  private boolean explicitEnd = false;
  
  private TimeZone timeZone = null;
  
  private Version version = null;
  
  private Map<String, String> tags = null;
  
  private Boolean prettyFlow = Boolean.valueOf(false);
  
  private AnchorGenerator anchorGenerator = (AnchorGenerator)new NumberAnchorGenerator(0);
  
  public boolean isAllowUnicode() {
    return this.allowUnicode;
  }
  
  public void setAllowUnicode(boolean paramBoolean) {
    this.allowUnicode = paramBoolean;
  }
  
  public ScalarStyle getDefaultScalarStyle() {
    return this.defaultStyle;
  }
  
  public void setDefaultScalarStyle(ScalarStyle paramScalarStyle) {
    if (paramScalarStyle == null)
      throw new NullPointerException("Use ScalarStyle enum."); 
    this.defaultStyle = paramScalarStyle;
  }
  
  public void setIndent(int paramInt) {
    if (paramInt < 1)
      throw new YAMLException("Indent must be at least 1"); 
    if (paramInt > 10)
      throw new YAMLException("Indent must be at most 10"); 
    this.indent = paramInt;
  }
  
  public int getIndent() {
    return this.indent;
  }
  
  public void setIndicatorIndent(int paramInt) {
    if (paramInt < 0)
      throw new YAMLException("Indicator indent must be non-negative."); 
    if (paramInt > 9)
      throw new YAMLException("Indicator indent must be at most Emitter.MAX_INDENT-1: 9"); 
    this.indicatorIndent = paramInt;
  }
  
  public int getIndicatorIndent() {
    return this.indicatorIndent;
  }
  
  public void setVersion(Version paramVersion) {
    this.version = paramVersion;
  }
  
  public Version getVersion() {
    return this.version;
  }
  
  public void setCanonical(boolean paramBoolean) {
    this.canonical = paramBoolean;
  }
  
  public boolean isCanonical() {
    return this.canonical;
  }
  
  public void setPrettyFlow(boolean paramBoolean) {
    this.prettyFlow = Boolean.valueOf(paramBoolean);
  }
  
  public boolean isPrettyFlow() {
    return this.prettyFlow.booleanValue();
  }
  
  public void setWidth(int paramInt) {
    this.bestWidth = paramInt;
  }
  
  public int getWidth() {
    return this.bestWidth;
  }
  
  public void setSplitLines(boolean paramBoolean) {
    this.splitLines = paramBoolean;
  }
  
  public boolean getSplitLines() {
    return this.splitLines;
  }
  
  public LineBreak getLineBreak() {
    return this.lineBreak;
  }
  
  public void setDefaultFlowStyle(FlowStyle paramFlowStyle) {
    if (paramFlowStyle == null)
      throw new NullPointerException("Use FlowStyle enum."); 
    this.defaultFlowStyle = paramFlowStyle;
  }
  
  public FlowStyle getDefaultFlowStyle() {
    return this.defaultFlowStyle;
  }
  
  public void setLineBreak(LineBreak paramLineBreak) {
    if (paramLineBreak == null)
      throw new NullPointerException("Specify line break."); 
    this.lineBreak = paramLineBreak;
  }
  
  public boolean isExplicitStart() {
    return this.explicitStart;
  }
  
  public void setExplicitStart(boolean paramBoolean) {
    this.explicitStart = paramBoolean;
  }
  
  public boolean isExplicitEnd() {
    return this.explicitEnd;
  }
  
  public void setExplicitEnd(boolean paramBoolean) {
    this.explicitEnd = paramBoolean;
  }
  
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  public void setTags(Map<String, String> paramMap) {
    this.tags = paramMap;
  }
  
  public boolean isAllowReadOnlyProperties() {
    return this.allowReadOnlyProperties;
  }
  
  public void setAllowReadOnlyProperties(boolean paramBoolean) {
    this.allowReadOnlyProperties = paramBoolean;
  }
  
  public TimeZone getTimeZone() {
    return this.timeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone) {
    this.timeZone = paramTimeZone;
  }
  
  public AnchorGenerator getAnchorGenerator() {
    return this.anchorGenerator;
  }
  
  public void setAnchorGenerator(AnchorGenerator paramAnchorGenerator) {
    this.anchorGenerator = paramAnchorGenerator;
  }
}
