package org.yaml.snakeyaml.emitter;

public final class ScalarAnalysis {
  public String scalar;
  
  public boolean empty;
  
  public boolean multiline;
  
  public boolean allowFlowPlain;
  
  public boolean allowBlockPlain;
  
  public boolean allowSingleQuoted;
  
  public boolean allowBlock;
  
  public ScalarAnalysis(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6) {
    this.scalar = paramString;
    this.empty = paramBoolean1;
    this.multiline = paramBoolean2;
    this.allowFlowPlain = paramBoolean3;
    this.allowBlockPlain = paramBoolean4;
    this.allowSingleQuoted = paramBoolean5;
    this.allowBlock = paramBoolean6;
  }
}
